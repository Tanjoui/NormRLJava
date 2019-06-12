package environment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimulationTest {

	@Test
	void testBasicSim() {
		Simulation sim3 = new Simulation(0, 2, 1, 0, true);
		sim3.displayMap(sim3.MapWithCars());
		System.out.println(' ');
		
		Simulation sim4 = new Simulation(0, 2, 1, 2, true);
		sim4.displayMap(sim4.MapWithCars());
		System.out.println(' ');
		
		Simulation sim = new Simulation(0, 12, 3, 0, true);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		
		Simulation sim2 = new Simulation(0, 9, 3, 3, true);
		sim.displayMap(sim2.MapWithCars());
		System.out.println(' ');
		

	}
	@Test
	void runASim() {

		Simulation sim = new Simulation(1000, 10, 5, 20, false);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		sim.run();

	}

}
