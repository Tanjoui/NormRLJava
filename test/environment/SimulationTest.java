package environment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimulationTest {

	@Test
	void testBasicSim() {
		Simulation sim3 = new Simulation(0, 2, 1, 0, true, false, 1, 0.6);
		sim3.displayMap(sim3.MapWithCars());
		System.out.println(' ');
		
		Simulation sim4 = new Simulation(0, 2, 1, 2, true, false, 1, 0.6);
		sim4.displayMap(sim4.MapWithCars());
		System.out.println(' ');
		
		Simulation sim = new Simulation(0, 12, 3, 0, true, false, 1, 0.6);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		
		Simulation sim2 = new Simulation(0, 9, 3, 3, true, false, 1, 0.6);
		sim.displayMap(sim2.MapWithCars());
		System.out.println(' ');
		

	}
	@Test
	void runASim() {

		Simulation sim = new Simulation(1000, 10, 5, 20, false, true, 1, 0.6);
		sim.displayMap(sim.MapWithCars());
		System.out.println(' ');
		sim.run();

	}

}
