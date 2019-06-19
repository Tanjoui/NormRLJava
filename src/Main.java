import environment.Simulation;
/**
 * Main class of the project
 * Launches a simulation based on the information entered
 * @author cadieux
 */
public class Main {

	public static void main (String[] args) {
		java.util.Scanner in =   new java.util.Scanner(System.in);
		System.out.println("*****************************************************************");
		System.out.println("*You can choose the parameters of the simulation:");
		System.out.println("");
		System.out.println("*Environment parameters:");
		System.out.println("	number of lanes of the roundabout (>= 1) ");
		System.out.println("	number of exits of the roundabout (>= 2) ");
		System.out.println("");
		System.out.println("*Simulation parameters:");
		System.out.println("	number of episodes to execute");
		System.out.println("	number of cars running on the simulation");
		System.out.println("	display the crashes: (true or false)");
		System.out.println("");
		System.out.println("*Q-Table parameters:");
		System.out.println("	alpha value (learning factor, 1 is optimal)");
		System.out.println("	gamma value (actualisation factor, prioritizes future reward)");
		System.out.println("*****************************************************************");

		System.out.println("Type number of lanes");
		int lanes = in.nextInt();
		System.out.println("Type number of exits");
		int exits = in.nextInt();
		
		System.out.println("Type number of episodes");
		int steps = in.nextInt();
		System.out.println("Type number of cars ");
		int cars = in.nextInt();
		System.out.println("Display the crashes? (true/false)");
		boolean crashmode = in.nextBoolean();
		
		System.out.println("Choose alpha factor");
		double alpha = in.nextDouble();	
		System.out.println("Choose gamma factor");
		double gamma = in.nextDouble();	


		
		Simulation sim = new Simulation(steps, lanes, exits, cars, false, crashmode, alpha, gamma);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		sim.run();
		in.close();
	}
}
