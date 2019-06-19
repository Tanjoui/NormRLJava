package environment;
import java.util.Random;
/**
 * @author cadieux
 * Defines a roundabout environment generated from it's number of lanes and exits
 */
public class Environment {
public int exitsnumber;
public int lanes;
Position[] exits; //positions des différentes sorties
Position[] starts;
char[][] road;


	/**
	 * Constructor of an Environment
	 * Generates an Environment according to the paramaters given
	 * @param e the number of exits
	 * @param l the number of lanes
	 */
	public Environment(int e, int l) {
		this.exitsnumber = e;
		this.exits = new Position[e];
		this.starts = new Position[e];
		this.lanes = l;
		/*création de la route*/
		char[] chunk = new char[l+2];
		chunk[0] = 'X';
		for(int i = 0; i<l; i++) {
			chunk[i+1] = '_';
		}
		chunk[l+1] = 'X';
		char[] chunkexit = new char[l+2];
		chunkexit[0] = 'X';
		for(int i = 0 ; i<l+1; i++) {
			chunkexit[i+1] = '_';
		}
		this.road = new char[e*6][l+2];
		//ajout d'un premier demi-tronçon
		this.road[0] = chunk;
		this.road[1] = chunk;
		//ajout des sorties+tronçons
		int x = 2;
		for(int current = 0; current<e-1; current++) {//création d'une voie de sortie
			this.starts[current] = new Position(x, l+1);
			this.exits[current] = new Position(x+1, l+1);
			for(int i = 0; i < 2; i++) {
				this.road[x] = chunkexit;
				x++;
			}//creation du tronçon
			for(int i = 0; i < 4; i++) {
				this.road[x] = chunk;
				x++;
			}
		}
		

		
		//ajout de la sortie finale et son demi tronçon
		this.starts[e-1] = new Position(x, l+1);
		this.road[x] = chunkexit;
		this.exits[e-1] = new Position(x+1, l+1);
		this.road[x+1] = chunkexit;
		this.road[x+2] = chunk;
		this.road[x+3] = chunk;		
		}
	
	/**
	 * Tels if a position is located on a border of a road
	 * @param pos Position to know
	 * @return -1 if not loop, 1 if left border, 2 if right border
	 */
	public int isLoopingPoint(Position pos) {
		if(pos.x == 0) {
			return 1;
		}else if(pos.x == road.length-1) {
			return 2;
		}
		return -1;
					
	}
	/**
	 * Gets a two dimension char array corresponding to the state of the Environment without cars
	 * @return a two dimensional char array
	 */
	public char[][] getRawRoad(){
		char[][] road2 = new char[road.length][road[0].length];
		for(int i = 0; i < road.length; i++) {
			for(int j = 0; j < road[0].length; j++) {
			road2[i][j] = road[i][j];
			}
		}
		return road2;
	}
	

	/**
	 * Allows to find an objective to a car according to the number of exits composing the Environment
	 * @return a random objective included in the environment
	 */
	public int findRandomObjective() {
		return (int)(Math.random() * this.exitsnumber);
	}
	
	/**
	 * trouve une ligne de départ aléatoire qui n'est pas sur la sortie en paramètre
	 * @return un entier correspondant à un objectif
	 */
	public int findStart(int n) {
		int res;
		while(true) {
			res = (int)(Math.random() * this.exitsnumber);
			if(res != n) {
				return res;
			}
		}
	}
	/**
	 * @param n the id of the exit
	 * @return the position of an exits
	 */
	public Position findPositionExit(int n) {
		return exits[n];
	}
	/**
	 * @param n the id of the starting point
	 * @return the position of a starting point
	 */
	public Position findPositionStart(int n) {
		return starts[n];
	}

	/**
	 * @param pos given position on the environment
	 * @return
	 */
	public char getCase(Position pos) {
		if(pos.x > this.exitsnumber*6 || pos.y > this.lanes+2) {
			System.out.println("Error the position selected is not included in the environment");
			return 'e';
		}else {
			return road[pos.x][pos.y];
		}
	}
	

	/**
	 * Prints the current environment on the console 
	 */
	public void display() {
		for(int j = 0; j < road[0].length; j++) {
			for(int i = 0; i < road.length; i++) 
				System.out.print(road[i][j]);
			System.out.println(' ');
		}
	}

}
