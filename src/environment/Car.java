package environment;
import norms.PerceptionState;
/**
 * @author cadieux
 * Defines a Car object which is the agent of our simulation
 */
public class Car {
	Position pos;
	char direction;
	Position nextmove; 
	public PerceptionState perception;
	
	Position objectivepos;
	int objective; //sortie vers laquelle la voiture doit se diriger
	int start;
	
	boolean shouldmove;
	boolean anormalmove;
	boolean crashed;
	
	boolean placed; //determine si la voiture est placée sur la carte
	boolean done; //determine si la voiture a terminé son épisode
	int score; //score obtenu à la fin d'un épisode

	/**
	 * Constructor of a car
	 * A car is built based on an environment, in order to give it an objective
	 * @param map roundabout type Environment
	 */
	public Car(Environment map) {
		//On determine les points de départ et de fin d'une voiture compris dans l'environement en paramètre
		int obj = map.findRandomObjective();
		this.start = map.findStart(obj);
		this.objective = obj;
		this.objectivepos = map.findPositionExit(obj);
		this.pos = map.findPositionStart(this.start);
		
		this.direction = '^'; //on la place par défaut vers le haut
		this.placed = false;//la voiture ne se trouve pas encore sur la carte
		this.done = false;
		this.score = 0;
		this.shouldmove = true;
	}
	/**
	 * Alternative consturctor of a Car, where you can manually select the objective and start
	 * @param map roundabout type Environment
	 * @param objective Goal position to reach
	 * @param start starting position of the car
	 */
	public Car(Environment map, int objective, int start) {
		this.objective = objective;
		this.start = start;
		this.pos = map.findPositionStart(start);
		
		this.direction = '^'; //on la place par défaut vers le haut
		this.placed = false;//la voiture ne se trouve pas encore sur la carte
		this.done = false;
		this.score = 0;
	}
	/**
	 * Getter of the perception state
	 * @return PerceptionState corresponding to the current perception
	 */
	public PerceptionState getPerception() {
		return this.perception;
	}
	/**
	 * Determines if the position of the car corresponds to the objectives
	 * @return true if the objective is reached
	 */
	public boolean arrived() {
		return(this.objectivepos.x == this.pos.x && this.objectivepos.y == this.pos.y);
	}
	/***
	 * Sets the perception state of a car according to the simulation state
	 * @param s Simulation where the car is evolving
	 */
	public void setPerception(Simulation s) {
		//TODO
		char DEFAULT = 'X';
		char left = '_';
		char front = '_';	
		char right = '_';
		char back = '_';
		char[][] road = s.MapWithCars();
		int loop = s.map.isLoopingPoint(this.pos);
		int x = this.pos.x;
		int y = this.pos.y;
		if(direction == '^') {
			if(loop == 1 && y != 0 && y!= road[0].length-1) {
				back = road[x][y+1];
				right = road[x+1][y-1];
				left = road[road.length-1][y-1];
				front = road[x][y-1];
			}else if(loop == 2 && y != 0 && y!= road[0].length-1) {
				right = road[0][y-1];
				front = road[x][y-1];
				back = road[x][y+1];
				left = road[x-1][y-1];
			}else if(y == 0){
				front = DEFAULT;
				left = DEFAULT;
				right = DEFAULT;
				back = road[x][y+1];
			}else if(y == road[0].length-1) {//en bas vers le haut (sortie)
				front = road[x][y-1];
				left = road[x-1][y-1];
				right = road[x+1][y-1];
				back = DEFAULT;
			}else {
				front = road[x][y-1];
				left = road[x-1][y-1];
				right = road[x+1][y-1];
				back = road[x][y+1];
			}
		}else if(direction == '>') {
			if(loop == 1 && y != road[0].length-1 && y != 0) {
				left = road[x+1][y-1];
				front = road[x+1][y];
				right = road[x+1][y+1];
				back = road[road.length-1][y];
			}else if(loop == 2 && y != road[0].length-1 && y != 0) {
				left = road[0][y-1];
				front = road[0][y];
				right = road[0][y+1];
				back = road[x-1][y];
			}else if(y == 0){
				left = DEFAULT;
				front = DEFAULT;
				right = '_';
				back = DEFAULT;	
			}else if(y == road[0].length-1) {
				left = '_';
				front = DEFAULT;
				right = DEFAULT;
				back = DEFAULT;
			}else {
				left = road[x+1][y-1];
				front = road[x+1][y];
				right = road[x+1][y+1];
				back = road[x-1][y];
			}
		}else if(direction == 'v') { //OK
			if(y == 0){
				left = road[x+1][y+1];
				front = road[x][y+1];
				right = road[x-1][y+1];
				back = DEFAULT;
			}else if(y == road[0].length-1) {
				left = DEFAULT;
				front = DEFAULT;
				right = DEFAULT;
				back = road[x][y-1];
			}else if(loop == 1 ) {
				left = road[x+1][y+1];
				front = road[x][y+1];
				right = DEFAULT;
				back = road[x][y-1];
			}else if(loop == 2 ) {
				left = DEFAULT;
				front = road[x][y+1];
				right = road[x-1][y+1];
				back = road[x][y-1];
			}else {
				left = road[x+1][y+1];
				front = road[x][y+1];
				right = road[x-1][y+1];
				back = road[x][y-1];
			}
		}else if(direction == '<') {
			if(y == 0 ){
				left = '_'; //TODO
				front = DEFAULT;
				right = DEFAULT;
				back = DEFAULT;
			}else if(y == road[0].length-1) {
				left = DEFAULT;
				front = DEFAULT;
				right = '_';
				back = DEFAULT;
			}else if(loop == 1 ) {
				//gauche droite front
				left = road[road.length-1][y+1];
				front = road[road.length-1][y];
				right = road[road.length-1][y-1];
				back = road[x+1][y];	
			}else if(loop == 2 ) {
				left = road[x-1][y+1];
				front = road[x-1][y];
				right = road[x-1][y-1];
				back = road[0][y];	
			}else {
				left = road[x-1][y+1];
				front = road[x-1][y];
				right = road[x-1][y-1];
				back = road[x+1][y];	
			}
		}
		this.perception = new PerceptionState(left, front, right, back);
		
	}
	/**
	 * 
	 */
	public String toString() {
		return "Position:"
				+ "\n direction "+this.pos.toString()
				+ "\n objectif "+this.objective
				+ "\n départ "+this.start
				+ "\n score "+this.score
				+ "\n placé " +this.placed
				+ "\n done "+this.done;
	}


	
	
	

	

	
}
