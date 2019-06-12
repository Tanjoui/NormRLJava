package norms;

import java.util.ArrayList;


public class NormBase {
public ArrayList<Norm> Norms;
public Norm DEFAULT;

public NormBase() {
	this.Norms = new ArrayList<Norm>();
}

public boolean isnorm(PerceptionState view) {
	// TODO Auto-generated method stub
	return false;
}


public Norm findNorm(PerceptionState perception) {
	for (Norm n : Norms) {
		if(n.perception.left == perception.left && n.perception.front == perception.front && n.perception.right == perception.right && n.perception.back == perception.back) {
			return n;
		}
	}
	return null;
}

public void addNorm(PerceptionState perception) {
	Norm n = new Norm(perception);
	Norms.add(n);
}

public void printNorms() {
	for (Norm n : Norms) {
		n.print();
	}
}

}
