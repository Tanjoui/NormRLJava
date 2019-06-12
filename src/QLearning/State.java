package QLearning;
import environment.Position;
/**
 * 
 * @author cadieux
 *
 */
public class State {
	
Position pos;
Position objective;
	public State(Position p, Position objective) {
		this.pos = p;
		this.objective = objective;
	}
	
	public void print() {
		System.out.println("(px" +pos.x+ " py"+pos.y+ ") (ox"+objective.x+" oy"+objective.y+")");
	}
}
