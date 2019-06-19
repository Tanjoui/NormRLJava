package QLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvWriter;

import environment.Environment;
import environment.Position;

public class QTable {
	State[] states;
	int nbstates;
	
	public double alpha = 1 ;
	public double gamma = 0.7 ;
	//gamma 0 = better solution
	//gamma 1 = faster solution
	
	int reward = 100; //récompense en atteignant l'objectif 
	int penalty = -20; //sanction en cas d'actrion illégale
	
	//assimilable à tableau en trois dimensions, déterminant la récompense, la position et la finalité de l'action
	//selon une action ) partir d'un état
	public double[][] Q;
	public int[][] R;
	public Position[][] P;
	boolean[][] done;
	
	
	
	public QTable(Environment map, double alpha, double gamma) {
		nbstates = (map.exitsnumber*6)*(map.lanes+2)*(map.exitsnumber);
		states = new State[nbstates];
		int largeur = map.exitsnumber*6;
		System.out.println("initialisation d'une table de "+nbstates+" etats");

		this.alpha = alpha;
		this.gamma = gamma;
		
		int c = 0;
		while(c <nbstates) {
			for(int o = 0; o<map.exitsnumber; o++) {//nombre d'objectifs possibles
				for(int l = 0; l<largeur; l++) {//parcours de la largeur	
					for(int h = 0; h<map.lanes+2; h++) {//parcours de la hauteur
						states[c] = new State(new Position(l, h), map.findPositionExit(o));
						c++;
					}
				}
			}
		}
		char[][] road = map.getRawRoad(); //servira à detecter les cases illégales
		R = new int[nbstates][4];
		Q = new double[nbstates][4];
		P = new Position[nbstates][4];
		done = new boolean[nbstates][4];

		for(int i = 0; i <nbstates; i++)//initialisation de Q à zero
			for(int j = 0; j <4; j++)
				Q[i][j] = 0;
		/**
		 * INITIALISATION ARBITRAIRE DE R et placement de P
		 * on désigne aussi la prochaine position correspondant à chaque action
		 */
		for(int j = 0; j < 4; j++) {//chaque action
			for(int i = 0; i<nbstates; i++) {//pour chaque état
				if(j == 0) { //HAUT
					if(states[i].pos.getY() == 0) {
						R[i][j] = penalty;
						P[i][j] = states[i].pos;
					}else {
							Position nextPos = new Position(states[i].pos.getX(), states[i].pos.getY()-1);
							if(road[nextPos.getX()][nextPos.getY()] == 'X') {
								R[i][j] = penalty;
							}else if(nextPos.getX() == states[i].objective.getX() && nextPos.getY() == states[i].objective.getY()) {
								R[i][j] = reward;
								done[i][j] = true;			
							}else 
								R[i][j] = -1;
							
							P[i][j] = nextPos;
					} 
				}
				
				if(j == 1) { //DROITE
					Position nextPos;
					if(states[i].pos.getX() == largeur-1) {//bordure droite
						nextPos = new Position(0, states[i].pos.getY());
					}else {
						nextPos = new Position(states[i].pos.getX()+1, states[i].pos.getY());
					}
					
					if(road[nextPos.getX()][nextPos.getY()] == 'X') {
						R[i][j] = penalty;
					}else if(nextPos.getX() == states[i].objective.getX() && nextPos.getY() == states[i].objective.getY()) {
						R[i][j] = reward;
						done[i][j] = true;	
					}else {
						R[i][j] = -1;
					}
					P[i][j] = nextPos;
				}
				
				if(j == 2) { //BAS
					
					if(states[i].pos.getY() == map.lanes+1) {
						//action illégale OUTOFBORDER
						R[i][j] = penalty;
						P[i][j] = states[i].pos;
					}else {
						Position nextPos = new Position(states[i].pos.getX(), states[i].pos.getY()+1);
						
						if(road[nextPos.getX()][nextPos.getY()] == 'X') {
							//ation illégale
							R[i][j] = penalty;
						}else if(nextPos.getX() == states[i].objective.getX() && nextPos.getY() == states[i].objective.getY()) {
							R[i][j] = reward;
							done[i][j] = true;	
						}else {
							R[i][j] = -1;
						}
						P[i][j] = nextPos;
					}
						
					
				}
				if(j == 3) { //GAUCHE
					Position nextPos;
					
					if(states[i].pos.getX() == 0) {//bordure gauche
						nextPos = new Position(largeur-1, states[i].pos.getY());
					}else {
						nextPos = new Position(states[i].pos.getX()-1, states[i].pos.getY());
					}
					
					if(road[nextPos.getX()][nextPos.getY()] == 'X') {
						//ation illégale
						R[i][j] = penalty;
					}else if(nextPos.getX() == states[i].objective.getX() && nextPos.getY() == states[i].objective.getY()) {
						R[i][j] = reward;
						done[i][j] = true;	
					}else {
						R[i][j] = -1;
					}
					P[i][j] = nextPos;
				}
			}
		}
	}

	
	

	public int getIdOfState(State current) {
		for(int i = 0; i<nbstates; i++) {
			if(states[i].objective.x == current.objective.x && states[i].objective.y == current.objective.y && states[i].pos.x == current.pos.x && states[i].pos.y == current.pos.y) {
				return i;
			}
		}
		System.out.println("ERROR state not found");
		return 0;
	}

	/**
	 * @param id
	 * @return valeur en tre 0 et 3 correspondant à la meilleure action selon Q
	 */
	public int getBestAction(int id) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		double maxQ = Q[id][0];
		for(int i = 0; i<4; i++) {
			if(Q[id][i] > maxQ) {
				list.clear();
				list.add(i);
				maxQ = Q[id][i];
			}else if(Q[id][i] == maxQ)
				list.add(i);
		}
		if(list.size() == 1) 
			return list.get(0);
		else {
			Random rand = new Random();
			int n = rand.nextInt(list.size());
			return list.get(n);
		}
	}
	/**
	 * @param id of a state
	 * @return the max Qvalue possible from a state
	 */
	public double maxQ(int id) {		
		double maxQ = -100;
		for(int i = 0; i<4; i++) {
			if(Q[id][i] > maxQ) {
				maxQ = Q[id][i];
			}
		}
		return maxQ;
		
	}
	public void printString() {
		for(int i = 0;i<nbstates; i++) {
			for(int j = 0; j<4; j++) {
				System.out.println("Etat " +i+ " Action "+j+" Reward="+ Q[i][j]+ " Pos "+P[i][j]);
			}
		}
	}
	/**
	 * updates QTable after a given state/action
	 * @param action
	 * @param state
	 * @param next
	 */
	public void updateQ(int action, int state, int next) {
		Q[state][action] = Q[state][action] + alpha*(R[state][action] + gamma*maxQ(next) -  Q[state][action]);
	}
	

}

