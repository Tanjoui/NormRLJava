package norms;

import java.util.Random;

/**
 * @author cadieux
 * If a certain Perception State is encountered, an action can be decided according to a probability related to this action
 * This probability is evaluated each time the action is used, making this a Reinforcement Learning tool
 */
public class Norm {
	PerceptionState perception;
	double probability;//probability of action happening
	int output; //output action, here it will always be "stopping" so it will remain unused
	/**
	 * Constructor of a norm according to a perception
	 * @param p perception encountered
	 */
	public Norm(PerceptionState p) {
		this.perception = p;
		this.probability = 0.7;
	}
	/**
	 * Getter of the perception state of the norm
	 * @return the perception state of the norm
	 */
	public PerceptionState getPerception() {
		return this.perception;
	}
	
	/**
	 * Determines randomly if an action should be changed
	 * @return true if we should follow the norm, false if we violate it
	 */
	public boolean randomMove() {
		Random rand = new Random();
		if (this.probability > rand.nextDouble()) {
			return true;
		}
		return false;
	}
	/**
	 * Lowers the probability of the norm
	 */
	public void reevaluate() {
		if(probability > 0.01) {
			this.probability = Math.pow(this.probability, 2);		
		}else {
			this.probability = 0;
		}
	}
	/**
	 * increases the probability of a norm
	 */
	public void reinforce() {
		this.probability = Math.sqrt(this.probability);
	}
	/**
	 * Prints the state of the norm in the console
	 */
	public void print() {
		System.out.println("(l"+perception.left+" , r"+perception.right+" , f"+perception.front+" , b"+perception.back+")="+probability);
	}
	
	
}


