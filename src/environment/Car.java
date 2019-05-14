package environment;
import norms.PerceptionState;
/**
 * 
 * @author cadieux
 *
 */
public class Car {
	//etat de la voiture
	Position pos;
	char direction;
	Position nextmove; 
	PerceptionState perception;
	
	int objective; //sortie vers laquelle la voiture doit se diriger
	int start;
	
	boolean shouldmove;
	boolean placed; //determine si la voiture est placée sur la carte
	boolean done; //determine si la voiture a terminé son épisode
	int score; //score obtenu à la fin d'un épisode
	
	public Car(Environment map) {
		int obj = map.findRandomObjective();
		this.start = map.findStart(obj);
		this.objective = obj;
		this.pos = map.findPositionStart(this.start);
		
		this.direction = '1'; //on la place par défaut vers le haut
		this.placed = false;//la voiture ne se trouve pas encore sur la carte
		this.done = false;
		this.score = 0;
	}
	
	public Car(Environment map, int objective, int start) {
		this.objective = objective;
		this.start = start;
		this.pos = map.findPositionStart(start);
		
		this.direction = '1'; //on la place par défaut vers le haut
		this.placed = false;//la voiture ne se trouve pas encore sur la carte
		this.done = false;
		this.score = 0;
	}
	public PerceptionState getPerception() {
		return this.perception;
	}
	public void setPerception(Simulation s) {
		char left = '_';
		char front = '_';	
		char right = '_';
		char back = '_';
		char[][] road = s.MapWithCars();
		int loop = s.map.isLoopingPoint(this.pos);
		int x = this.pos.x;
		int y = this.pos.y;
		
		if(direction == 1) {
			if(loop == 1) {
				back = road[x][y+1];
				right = road[x+1][y-1];
				left = road[road.length][y-1];
				front = road[x][y-1];
			}else if(loop == 2) {
				right = road[0][y-1];
				front = road[x][y-1];
				back = road[x][y+1];
				left = road[x-1][y-1];
			}else {
				front = road[x][y-1];
				left = road[x-1][y-1];
				right = road[x+1][y-1];
				back = road[x][y+1];
			}
		}else if(direction == 2) {
			if(loop == 1) {
				left = road[x+1][y-1];
				front = road[x+1][y];
				right = road[x+1][y+1];
				back = road[road.length][y];
			}else if(loop == 2) {
				left = road[0][y-1];
				front = road[0][y];
				right = road[0][y+1];
				back = road[x-1][y];
			}else {
				left = road[x+1][y-1];
				front = road[x+1][y];
				right = road[x+1][y+1];
				back = road[x-1][y];
			}
		}else if(direction == 3) {
			if(loop == 1) {
				//gauche
			}else if(loop == 2) {
				//droite
			}else {
				
			}
		}else if(direction == 4) {
			if(loop == 1) {
				//gauche droite front
			}else if(loop == 2) {
				//back
			}else {
				
			}
		}
		this.perception = new PerceptionState(left, front, right, back);
		
	}
	
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
