import environment.Simulation;

public class Main {

	public static void main (String[] args) {
		java.util.Scanner in =   new java.util.Scanner(System.in);
		System.out.println("Welcome chose your parameters");
		
		System.out.println("Type number of steps (< 10000)");
		int steps = in.nextInt();
		System.out.println("Type number of lanes(<= 10)");
		int lanes = in.nextInt();
		System.out.println("Type number of exits (<= 9)");
		
		int exits = in.nextInt();
		System.out.println("Type number of cars <20");
		int cars = in.nextInt();

		Simulation sim = new Simulation(steps, exits, lanes, cars, false);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		sim.run();
		in.close();
	}
}
