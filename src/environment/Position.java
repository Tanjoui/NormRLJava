package environment;
/**
 * Defines a position object
 * @author cadieux
 * it is a simple couple of x value and y value 
 */
public class Position {
	public int x;
	public int y;
	/**
	 * Constructor of a Position
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Returns the position
	 */
	public String toString() {
		return "(x"+x+ " y"+y+")";
	}
	/**
	 * Prints the position in the console
	 */
	public void print() {
		System.out.println( "(x"+x+ " y"+y+")");
	}
	/**
	 * getter of the x coordinate
	 * @return x 
	 */
	public int getX() {
		return this.x;
	}
	/**
	 * getter of the y coordinate
	 * @return y
	 */
	public int getY() {
		return this.y;
	}
	/**
	 * Determines if a position corresponds to an exit position form a map
	 * @param map Environment
	 * @return true if the position is an exit on the given Environment
	 */
	public boolean isExit(Environment map) {
		for(Position p : map.exits) 
			if(this == p) 
				return true;
		return false;
	}
	
}
