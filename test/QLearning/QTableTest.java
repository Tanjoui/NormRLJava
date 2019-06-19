package QLearning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import environment.Environment;

class QTableTest {

	@Test
	void generate() {
		Environment map = new Environment(3, 3);
		QTable Q = new QTable(map, 1, 0.6);
		
	}
	



}
