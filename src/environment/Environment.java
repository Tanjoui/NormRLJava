package environment;
import java.util.Random;
/**
 * 
 * @author cadieux
 * Defines a roundabout environment, this is generated from it's number of lanes and exits
 */
public class Environment {
public int exitsnumber;
public int lanes;
Position[] exits; //positions des différentes sorties
Position[] starts;
char[][] road;


	/**
	 * génère une map selon les paramètres donnés
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
	 * @param posX
	 * @param posY
	 * @return -1 si not loop, 1 si loop gauche, 2 si loop droit
	 */
	public int isLoopingPoint(Position pos) {
		if(pos.x == 0) {
			return 1;
		}else if(pos.x == road.length-1) {
			return 2;
		}
		return -1;
					
	}

	public char[][] getRawRoad(){
		char[][] road2 = new char[road.length][road[0].length];
		for(int i = 0; i < road.length; i++) {
			for(int j = 0; j < road[0].length; j++) {
			road2[i][j] = road[i][j];
			}
		}
		return road2;
	}
	

	
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
	
	public Position findPositionExit(int n) {
		return exits[n];
	}
	public Position findPositionStart(int n) {
		return starts[n];
	}

	
	public char getCase(Position pos) {
		return road[pos.x][pos.y];
	}
	

	
	public void display() {
		for(int j = 0; j < road[0].length; j++) {
			for(int i = 0; i < road.length; i++) {
			
				System.out.print(road[i][j]);
			}
			System.out.println(' ');
			
		}
	}

}
