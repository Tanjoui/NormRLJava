package norms;
/**
 * Decrit un état perçu
 * @author cadieux
 *
 */
public class PerceptionState {
char left;
char front;
char right;
char back; 
	
	public PerceptionState(char l, char f, char r, char b) {
		this.left = l;
		this.front = f;
		this.right = r;
		this.back = b;
	}
	
	public boolean equalsState(PerceptionState p) {
		return(this.left == p.left && this.right == p.right && this.back == p.back && this.front == p.front);
	}
}
