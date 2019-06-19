package norms;

import java.util.ArrayList;


public class NormBase {
public ArrayList<Norm> Norms;
/**
 * Constructor of a Norm base
 * It is an array list of norms
 */
public NormBase() {
	this.Norms = new ArrayList<Norm>();
}
/**
 * Returns true if the current perception is located in the norms base
 * @param view
 * @return
 */
public boolean isnorm(PerceptionState view) {
	for(Norm n : Norms) {
		if(view.left == n.perception.left
				&& view.right == n.perception.right
				&& view.front == n.perception.front
				&& view.back == n.perception.back) {
			return true;
		}
	}
	return false;
}

public void deleteNorm(Norm n) {
	Norms.remove(n);
}

/**
 * Returns the Norm defined by a certain perception state
 * @param perception Perception state of the norm
 * @return a Norm registered with the perception state
 */
public Norm findNorm(PerceptionState perception) {
	for (Norm n : Norms) 
		if(n.perception.left == perception.left && n.perception.front == perception.front && n.perception.right == perception.right && n.perception.back == perception.back) 
			return n;
		
	return null;
}

public void addNorm(PerceptionState perception) {
	Norm n = new Norm(perception);
	Norms.add(n);
}

public void reinforce(Norm n) {
	n.reinforce();
}

public void reevaluate(Norm n) {
	if(n.probability < 0.1)
		Norms.remove(n);
	else {
		n.reevaluate();
	}
}
/**
 * Prints in the console all of the norms present in the simulation
 */
public void printNorms() {
	for (Norm n : Norms) {
		n.print();
	}
}

}
