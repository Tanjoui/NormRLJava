package environment;


import java.io.IOException;
import java.util.ArrayList;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import norms.Norm;
import norms.NormsBase;
import norms.PerceptionState;

/**
 * Classe gérant l'execution d'une simulation du début à la fin
 * Orchestre toutes les actions et comporte les objets nécéssaires à son déroulement
 * La finalité de la simulation résulte en deux graphes
 * @author cadieux
 *
 */
public class Simulation {
	int[] crash;
	int[] reward;
	int episode;
	int episodemax;
	int step;
	
	boolean done;
	
	Environment map;
	
	NormsBase normbase;
	PerceptionState[] perceptions;
	Car[] cars; //toutes les voitures présentes dans la simulation
	/**
	 * 
	 * crée une simulation paramétrée
	 * @param nbepisodes nombre d'épisode à calculer
	 * @param lanes nombre de voies du rond point PREREQUIS: l>=1
	 * @param exits nombre de sorties du rond point PREREQUIS: e>=2
	 * @param cars nombre de voitures
	 */
	public Simulation(int nbepisodes, int lanes, int exits, int nbcars) {
		this.crash = new int[nbepisodes];
		this.reward = new int[nbepisodes];
		this.episodemax = nbepisodes;
		this.map= new Environment(lanes, exits);
	
		this.cars = new Car[nbcars];
		for(int i = 0; i<nbcars; i++) {
			cars[i] = new Car(this.map);			
		}

	}
	
	public void run() {
		//INIT
		
		while(episode < episodemax) {
			while(done == false) {
				step();
			}
			reward[episode] = getTotalReward();
			resetReward();
			episode ++;
		}
			
		
		try {
			displayGraph();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void step() {
		//perception phase
		registerPerception();
		
		for (Car c : cars) {
			for (Norm n : normbase.Norms) {
				if(c.perception == n.getPerception()) {
					if(randomAction()) {
						c.shouldmove = true;
						//TODO register into anormal move
					}else {
						c.shouldmove = false;
					}
				}else {
					c.shouldmove = true;
				}
					
					
			}
		}
		
		
		Car[] crashed = detectCrash();
		//evaluation phase
	
		displayMap(MapWithCars());
		step ++;
	}

	private boolean randomAction() {
		// TODO Auto-generated method stub
		return false;
	}

	private void registerPerception() {
		for (int i = 0; i<cars.length; i++) {
			perceptions[i] = cars[i].getPerception();
		}
	}

	public Car[] detectCrash() {
		//TODO cette méthode n'est pas correcte
		ArrayList<Car> res = new ArrayList<Car>();
		for (Car c : cars) 
			for (Car c2 : cars) 
				if(c.pos == c2.pos) 
					res.add(c);		
		return (Car[]) res.toArray();
	}
	/**
	 * Permet d'obtenir l'état actuel de la carte, en comprenant les voitures présentes.
	 * @return un tableau de caractère représentant l'état de la carte comprenant les voitures
	 */
	public char[][] MapWithCars() {
		char[][] road = this.map.getRawRoad();
		for (Car c : cars) 
			//if(c.done == false && c.placed == true)
				road[c.pos.x][c.pos.y] =  c.direction;
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
	
	public int getTotalReward() {
		int r = 0;
		for (Car c : cars) 
			r+= c.score;
		return r;
	}
	
	public void resetReward() {
		for (Car c : cars) 
			c.score = 0;
	}
	
	/**
	 * enregistre un diagramme du nombre de crash et du reward en fonction de l'épisode
	 * Utilise l'API XChart
	 * @throws IOException 
	 */
	public void displayGraph() throws IOException {
		double[] tab = new double [episodemax];
		double[] crashes = new double [episodemax];
		double[] rewards = new double [episodemax];
		for(int i = 0; i <episodemax; i++) {
			tab[i] = i;
			crashes[i] = crash[i];
			rewards[i] = reward[i];
		}
		System.out.println("Creating reward and crash charts");
		XYChart crashchart = QuickChart.getChart("nombre de crash/temps", "crash", "temps", "nombre de crash", crashes, tab);
		XYChart rewardchart = QuickChart.getChart("reward/episode", "crash", "temps", "nombre de crash", rewards, tab);
		new SwingWrapper(crashchart).displayChart();
		new SwingWrapper(rewardchart).displayChart();
		BitmapEncoder.saveBitmap(crashchart, "./Crashes", BitmapFormat.PNG);
		BitmapEncoder.saveBitmap(rewardchart, "./Rewards", BitmapFormat.PNG);
		System.out.println("Bitmap of charts done");
	}
}
