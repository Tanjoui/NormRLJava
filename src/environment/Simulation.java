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
	public Simulation(int nbepisodes, int lanes, int exits, int nbcars, boolean debug) {
		this.crash = new int[nbepisodes];
		this.reward = new int[nbepisodes];
		this.episodemax = nbepisodes;
		this.map = new Environment(lanes, exits);
		this.currentcrashes = 0;
		this.step = 0;
		this.episode = 0;
		this.normbase = new NormBase();
		this.cars = new Car[nbcars];
		for(int i = 0; i<nbcars; i++) {
			cars[i] = new Car(this.map);			
		}
		this.debug = debug;
		qtable = new QTable(map);

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
	 * Execution d'une étape de simulation
	 * cette étape se produit en 3 parties:
	 * 1) une phase de perception, où on determine si chaque voiture doit se déplacer
	 * 2) phase de mouvement, chaque voiture effectue son mouvement selon le pathfinding établi par la Qtable
	 * 3) phase d'évaluation, on évalue les crash et résulats des mouvements pour améliorer la simulation
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
		currentcrashes += detectCrash();
		for (Car c : cars) 
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
	 * replace tous les attributs des voitures à leurs valeurs initiales pour débuter un nouvel épisode dans les mêmes conditions que le précédent
	 * -remet leur score à 0
	 * -les repositionne
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
	
	private boolean nocarsleft() {
		for (Car c : cars) 
			if(c.placed == true) 
				return false;
			
		return true;
	}

	private boolean alldone() {
	for (Car c : cars) 
		if(c.done == false) 
			return false;
	
	return true;
	}
	
	/**
	 * Detects if crash happened at the current state of the environment
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
	 * Permet d'obtenir l'état actuel de la carte, en comprenant les voitures présentes.
	 * @return un tableau de caractère représentant l'état de la carte comprenant les voitures
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
	 * permet d'afficher l'état d'une carte donnée
	 * @param map carte pouvant être avec ou sans voitures
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
	 * Récupère le score de chaque voiture de la simulation à un épisode donné donnée
	 * @return the global score at a certain simulation state
	 */
	public int getTotalReward() {
		int r = 0;
		for (Car c : cars) 
			r = r + c.score;
		return r;
	}
	

	
	/**
	 * enregistre un diagramme du nombre de crash et du reward en fonction de l'épisode
	 * Utilise l'API XChart
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
		System.out.println("Creating reward and crash charts");
		XYChart crashchart = QuickChart.getChart("Number of crashes by episode ("+map.lanes+" lanes,"
				+ " "+map.exitsnumber+" exits, "+nbcars+" cars)", "episode", "crash number", "Number of crashes ", tab, crashes);
		XYChart rewardchart = QuickChart.getChart("Average Reward by episode ("+map.lanes+" lanes, "
				+ ""+map.exitsnumber+" exits, "+nbcars+" cars)", "episode", "reward", "Average reward ", tab, rewards);
		new SwingWrapper(crashchart).displayChart();
		new SwingWrapper(rewardchart).displayChart();
		BitmapEncoder.saveBitmap(crashchart, "./Crashes", BitmapFormat.PNG);
		BitmapEncoder.saveBitmap(rewardchart, "./Rewards", BitmapFormat.PNG);
		System.out.println("PNGs generated");
	}
}
