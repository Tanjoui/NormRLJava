package environment;

public class Position {
	public int x;
	public int y;
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "(x"+x+ " y"+y+")";
	}
	public void print() {
		System.out.println( "(x"+x+ " y"+y+")");
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	/**
	 * Determines if a position corresponds to an exit position form a map
	 * @param map
	 * @return
	 */
	public boolean isExit(Environment map) {
		for(Position p : map.exits) {
			if(this == p) {
				return true;
			}
		}
		return false;
	}
	
}
