package environment;


import java.io.IOException;
import java.util.ArrayList;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import QLearning.QTable;
import QLearning.State;
import norms.Norm;
import norms.NormBase;
import norms.PerceptionState;


/**
 * @author cadieux
 * This class monitors a simulation from it's begining to it's end
 */
public class Simulation {
	//attributs globaux de la simulation
	boolean debug;
	boolean crashmode;
	
	int[] crash;
	int[] reward;

	int episodemax;

	//attributs sur l'état actuel de la simulation
	int episode;
	int currentcrashes; //cars crashed at the current episode
	int step;
	boolean done; //true when episode needs to end
	
	//objects composant la simulation
	Environment map;
	QTable qtable;
	NormBase normbase;
	Car[] cars; //toutes les voitures présentes dans la simulation
	
	/**
	 * crée une simulation paramétrée
	 * @param nbepisodes nombre d'épisode à calculer
	 * @param lanes nombre de voies du rond point PREREQUIS: l>=1
	 * @param exits nombre de sorties du rond point PREREQUIS: e>=2
	 * @param cars nombre de voitures
	 */
	public Simulation(int nbepisodes, int lanes, int exits, int nbcars, boolean debug, boolean c, double alpha, double gamma) {
		this.crash = new int[nbepisodes];
		this.reward = new int[nbepisodes];
		this.episodemax = nbepisodes;
		this.map = new Environment(exits, lanes);
		this.currentcrashes = 0;
		this.step = 0;
		this.episode = 0;
		this.normbase = new NormBase();
		this.cars = new Car[nbcars];
		for(int i = 0; i<nbcars; i++) {
			cars[i] = new Car(this.map);			
		}
		this.debug = debug;
		qtable = new QTable(map, alpha, gamma);
		this.crashmode = c;
	}
	
	/**
	 * Runs the simulation
	 * this has to be activated only once and will monitor a whole simulation from beginning to its end
	 */
	public void run() {
		System.out.println("Launching the simulation");
		
		while(episode < episodemax) {
			System.out.print("Episode number "+episode+" starting");
			while(done == false) {//the episode stops when a step encounters an ending scenario
				step();
			}
			step = 0; //reset the step for next episode 
			reward[episode] = getTotalReward(); //registers the total reward of the episode
			System.out.println(" reward = "+reward[episode]/cars.length);
			
			crash[episode] = currentcrashes;
			currentcrashes = 0;
			prepareCars(); //prepares the cars for a new episode
			done = false;
			episode ++;
		}
		
		try {
			displayGraph();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


	/**
	* execution of a step of the Simulation
	* it is composed in three parts
	* 1) The perception phase
	* 2) The movement phase
	* 3) The evaluation phase
	 */
	private void step() {
		
		for (Car c : cars) {
			if(c.placed == false && c.done == false) {
				if(MapWithCars()[c.pos.x][c.pos.y] == '_') {
					c.placed = true;
				}
			}
		}
		/**
		 * Phase de perception 
		 */
		if(crashmode) {
			for (Car c : cars) {
				c.setPerception(this); //on définit la perception actuelle de la voiture
				if(normbase.Norms.isEmpty()) 
					c.shouldmove = true;
				else 
					for (Norm n : normbase.Norms)  //si la perception de la voiture est présente dans la base
						if	(n.getPerception().left == c.perception.left && n.getPerception().front == c.perception.front 
							&& n.getPerception().right == c.perception.right && n.getPerception().back == c.perception.back && false){
							if(n.randomMove()) {
								c.shouldmove = true;
								c.anormalmove = true;
							}else 
								System.out.println("arret");
								c.shouldmove = false;
						}else 
							c.shouldmove = true;
			}
		}
		/**
		 * Phase de mouvement
		 */
		if(debug == true)
			System.out.println("episode: "+this.episode+" step: "+this.step+" Move phase");
		
		for (Car c : cars) {
			if(c.shouldmove && c.placed) {
				State s = new State(c.pos, map.findPositionExit(c.objective));
				setNextMove(s, c);
			}
		}
		MoveAllcars();
		/**
		 * Phase d'évaluation
		 */
		if(crashmode)
			currentcrashes += detectCrash();
		
		for (Car c : cars) 
			if(crashmode)
				if(c.crashed) { //la voiture s'est crashée
					if(c.shouldmove) //la voiture a bougé
						if(c.anormalmove) //la voiture a bougé mais elle n'aurait pas dû
							if(normbase.findNorm(c.perception) != null) 
								normbase.findNorm(c.perception).reinforce();
							else 
								normbase.addNorm(c.perception);
						else 
							if(normbase.findNorm(c.perception) != null) 
								normbase.findNorm(c.perception).reevaluate();
					else //la voiture n'a pas bougé
						if(normbase.findNorm(c.perception) != null) 
							normbase.findNorm(c.perception).reinforce();
						else 
							normbase.addNorm(c.perception);
					
					c.crashed = false;
				}else //la voiture n'a pas crash
					if(c.anormalmove)  //la voiture a fait un mouvement hors des règles
						normbase.findNorm(c.perception).reevaluate();
						
				
			
		
		if(episode >= episodemax-2) {
			System.out.println(" ");
			displayMap(MapWithCars());
			System.out.println(" ");
			//normbase.printNorms();
		}

		
		/**
		 * On détermine si on doit arrêter l'épisode ou continuer vers la prochaine étape
		 * Cas d'arrêt:
		 * -toutes les voitures ont disparu à cause d'un crash ou d'une sortie de piste
		 * -toutes les voitures ont terminé leur chemin
		 */
		if(alldone() || nocarsleft() || step == 100) {
			this.done = true;
		}else {
			step ++;
		}
		
	}
	/**
	 * Moves all the place cars of the simulation, and updates their values accordingly
	 */
	private void MoveAllcars() {
		for (Car c : cars) {
			if(c.placed) {
				c.pos = c.nextmove;
				c.nextmove = null;
				if(c.arrived()) {
					c.done = true;
					c.placed = false;
				}
			}
		}
	}
	/**
	 * Chooses and sets the best next move to a car according toit's current state
	 * @param current state of the car
	 * @param c car of the simulation
	 */
	public void setNextMove(State current, Car c) {
		int Stateid = qtable.getIdOfState(current);
		int Actionid = qtable.getBestAction(Stateid); //determine la meilleure action
		if(Actionid == 0) {
			c.direction = '^';
		}else if(Actionid== 1) {
			 c.direction = '>';
		}else if(Actionid== 2) {
			c.direction = 'v';
		}else if(Actionid== 3) {
			c.direction = '<';
		}
		State nextState = new State(qtable.P[Stateid][Actionid], map.findPositionExit(c.objective));
		qtable.updateQ(Actionid, Stateid, qtable.getIdOfState(nextState)); //mise à jour de Q
		c.nextmove = qtable.P[Stateid][Actionid]; //selection du prochain mouvement
		c.score = c.score + qtable.R[Stateid][Actionid]; //mise à jour de la récompense
	}
	/**
	* replaces all the atributes of the cars in order to dtart a new episode
	 */
	private void prepareCars() {
		for (Car c : cars) {
			c.score = 0;
			c.placed = true;
			c.pos = map.findPositionStart(c.start); //la remet à sa position initiale
			c.direction = '^';
			c.done = false;
		}
	}
	/**
	 * @return true if there are no placed cars in the simulation
	 */
	private boolean nocarsleft() {
		for (Car c : cars) 
			if(c.placed == true) 
				return false;
		return true;
	}
	/**
	 * @return true if all the cars are in final state during the episode
	 */
	private boolean alldone() {
	for (Car c : cars) 
		if(c.done == false) 
			return false;
	
	return true;
	}
	
	/**
	 * Detects if crash happened at the current state of the environment
	 * Also sets the values of the cars if they are involved during a crash
	 * @return the number of crashes of current episode
	 */
	public int detectCrash() {
		int i = 0;
		for (Car c : cars) 
			for (Car c2 : cars) 
				if(c.pos.x == c2.pos.x && c.pos.y == c2.pos.y && c != c2 && c.placed && c2.placed) {
					c.crashed = true;	
					c.score = -50;
					c.placed = false;
					i++;
				}
		return i;
	}
	
	/**
	 * Allows to get a two dimension char array corresponding to the current environment where the cars are placed
	 * it will show where the cars are placed in the environment and their directions
	 */
	public char[][] MapWithCars() {
		char[][] road = this.map.getRawRoad();
		for (Car c : cars) 
			if(c.placed == true)
				if(c.crashed == true) {
					road[c.pos.x][c.pos.y] =  'C';
				}else {
					road[c.pos.x][c.pos.y] =  c.direction;
				}
		return road;
	}
	/**
	 * Allow to print the current state of an Environment where the cars are placed
	 * @param map where the cars are present
	 */
	public void displayMap(char[][] map) {
		for(int j = 0; j < map[0].length; j++) {
			for(int i = 0; i < map.length; i++) {
			
				System.out.print(map[i][j]);
			}
			System.out.println(' ');
			
		}
	}
	
	/**
	 * Gets the score of all the cars present during the simulation
	 * @return the global score at a certain simulation state
	 */
	public int getTotalReward() {
		int r = 0;
		for (Car c : cars) 
			r = r + c.score;
		return r;
	}
	

	
	/**
	 * Saves diagrams according to the data of the simulation
	 * Two charts will be saved:
	 * A crash chart through the steps
	 * An average reward chart through the steps
	 * @throws IOException 
	 */
	public void displayGraph() throws IOException {
		System.out.println("Chart generation starting");
		double[] tab = new double [episodemax];
		double[] crashes = new double [episodemax];
		double[] rewards = new double [episodemax];
		double[] totalrewards = new double [episodemax];
		int nbcars = cars.length;
		for(int i = 0; i <episodemax; i++) {
			tab[i] = i;
			crashes[i] = crash[i];
			rewards[i] =reward[i]/nbcars;
			totalrewards[i] = reward[i];
		}
		System.out.println("Creating rewards chart");
		XYChart rewardchart = QuickChart.getChart("Average Reward by episode ("+map.lanes+" lanes, "
				+ ""+map.exitsnumber+" exits, "+nbcars+" cars)", "episode", "reward", "Average reward ", tab, rewards);
		new SwingWrapper(rewardchart).displayChart();
		BitmapEncoder.saveBitmap(rewardchart, "./Rewards", BitmapFormat.PNG);
		
		if(crashmode) {
			System.out.println("Creating crash chart");
			XYChart crashchart = QuickChart.getChart("Number of crashes by episode ("+map.lanes+" lanes,"
					+ " "+map.exitsnumber+" exits, "+nbcars+" cars)", "episode", "crash number", "Number of crashes ", tab, crashes);
			new SwingWrapper(crashchart).displayChart();
			BitmapEncoder.saveBitmap(crashchart, "./Crashes", BitmapFormat.PNG);
			System.out.println("PNGs generated");
		}
	}
}
