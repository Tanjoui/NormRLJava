package environment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EnvironmentTest {

	
	@Test
	void createBasicEnv() {
		Environment map = new Environment(2, 2);
		char[][] road = map.getRawRoad();
		char[][] road2 = {
				{'X','_','_','X'},
				{'X','_','_','X'},
				{'X','_','_','_'},
				{'X','_','_','_'},
				{'X','_','_','X'},
				{'X','_','_','X'},
				{'X','_','_','X'},
				{'X','_','_','X'},
				{'X','_','_','_'},
				{'X','_','_','_'},
				{'X','_','_','X'},
				{'X','_','_','X'}
		};
		for(int i = 0; i<road2.length; i++)
			for(int j = 0; j<4; j++)
				assertEquals(road[i][j], road2[i][j]);
	}
	
	@Test
	void testBasicExits() {
		Environment map = new Environment(2, 2);
		assertEquals(3, map.exits[0].x);
		assertEquals(3, map.exits[0].y);
		
		assertEquals(3, map.findPositionExit(0).x);
		assertEquals(3, map.findPositionExit(0).y);
		
		assertEquals(9, map.exits[1].x);
		assertEquals(3, map.exits[1].y);
		
		assertEquals(9, map.findPositionExit(1).x);
		assertEquals(3, map.findPositionExit(1).y);
	}
	
	@Test
	void testBasicStarts() {
		Environment map = new Environment(2, 2);
		assertEquals(2, map.starts[0].x);
		assertEquals(3, map.starts[0].y);
		assertEquals(8, map.starts[1].x);
		assertEquals(3, map.starts[1].y);
	}
	
	//@Test
	void testLoopingPoints() {
		Environment map = new Environment(2, 2);
		Position pos = new Position(1, 2);
		Position pos2 = new Position(1, 2);
		Position pos3 = new Position(1, 2);
		assertEquals(1,map.isLoopingPoint(pos));
		assertEquals(-1,map.isLoopingPoint(pos2));
		assertEquals(2,map.isLoopingPoint(pos3));
	}

}
