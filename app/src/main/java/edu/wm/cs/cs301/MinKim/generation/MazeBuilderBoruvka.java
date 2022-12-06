package edu.wm.cs.cs301.MinKim.generation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;




/** Boruvka's Algorithm: Expanding the MST till all cells are connected.
 * for each round, find a minimum cost path for each cell, then connect the cells
 * through the minimum costs.
 * While one single tree is not formed, iterate the process.
 * 
 * @author Min Kim
 *
 */

public class MazeBuilderBoruvka extends MazeBuilder implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(MazeBuilderPrim.class.getName());
	int[][][] edgeWeight;
	Set<Integer> connected;
	ArrayList<Set<Integer>> path;
	
	
	/**
	 * The logger is used to track execution and report issues.
	 */
	public MazeBuilderBoruvka() {
		super();
		LOGGER.config("Using Boruvka's algorithm to generate maze.");
	}
	
	
	/**
	 * This method generates pathways into the maze by using Boruvka's algorithm.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wallboard separating these cells in the maze. 
	 */
	@Override
	public void generatePathways() {
		
		edgeWeight = new int [width][height][5];
		
		//initialize a value for each cell and wallboards of the cell. 
		initialization(edgeWeight);
		
		//list of trees
		final ArrayList<Set<Integer>> path = new ArrayList<>();
		for (int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				//adding a set of cells to the list, initially, each set would only contain one cell. 
				//each cell has an index value, which makes it easier to keep track of each cell.
				Set<Integer> connected = new HashSet<>();
				connected.add(edgeWeight[i][j][0]);
				path.add(connected);
			}
		}
		
		int data[] = new int[3];
		//Iterating the process until there is one big tree left in the list.
		while(path.size()!=1 && path.get(0).size()!=width*height) {
			for (Set<Integer> i : path){
				//find the minimum path for each set in the list
				data = findMinPlace(i);
				//declare a wallboard with the minimum path
				Wallboard curWallboard = new Wallboard(data[0],data[1],intToDir(data[2]));
				//if the wallboard cannot be torn down, pick other one.
				while(!floorplan.canTearDown(curWallboard))
				{
					data = findMinPlace(i);
					curWallboard = new Wallboard(data[0],data[1],intToDir(data[2]));
				}
				//update the set and delete the wallboard.
				updateSet(data, i);
				floorplan.deleteWallboard(curWallboard);
				
			}
			//update the list
			updateList(path);
		}
	}
	
	/**
	 * Get a value of a wallboard of a cell. If a value is already assigned to a wallboard, then it simply returns
	 * the original value. If a value is not assigned yet, pick a random value from 1 to 10 and return it.
	 * @param x the width-value of a cell
	 * @param y the height-value of a cell
	 * @param cd the direction of a wallboard of a cell. (ex. North, East, West, South) 
	 * @return an integer that is a value of a wallboard.
	 */
	public int getEdgeWeight(int x, int y, CardinalDirection cd) {
		int weight;
		int cdNum = dirToInt(cd);
		
		if (edgeWeight[x][y][cdNum]!=0) {
			weight = edgeWeight[x][y][cdNum];
		}else {
			Random random = new Random();
			weight = random.nextInt(9)+1;
		}
		return weight;
		
	}
	
	/**
	 * Set up for Boruvka's algorithm. Using 3-dimensional array, we store values for width, height, and direction of each cell.
	 * x side represents width, y side represents height, and z side represents either index value or direction value. 
	 * z[0] represents an index value for each cell, and z[1~4] represent direction value. 
	 * starting from top-left cell, assign an index value starting from 0, incrementing each time.
	 * Also, for each cell, assign a value for each wallboard. For wallboard to the east and south side, call getEdgeWeight
	 * to get a value randomly. For wallboard to the north and west side, since it is a duplicate wall with a cell above and next, 
	 * simply copy the value that is already assigned. 
	 * @param data 3-dimensional array data, where each cell in an array will take width, height, direction value of a cell in maze.
	 */
	public void initialization(int[][][] data) {
		int cellIndx = 0;
		for(int i = 0; i<width; i++) {
			for(int j = 0; j<height; j++) {
				for(int k = 0; k<=4; k++) {
					//give each cell an index value that can be referenced in the future.
					if(k==0) {
						edgeWeight[i][j][k]= cellIndx;
						cellIndx++;
					}
					//give each wallboard a specific integer value which represents the cost of the path.
					else if((k==2 || k==4) && !(floorplan.isPartOfBorder(new Wallboard(i, j, intToDir(k))))) {
						edgeWeight[i][j][k]=  getEdgeWeight(i, j, intToDir(k));
					}else if(k==1 && !(floorplan.isPartOfBorder(new Wallboard(i, j, intToDir(k))))) {
						edgeWeight[i][j][k]= edgeWeight[i][j-1][k]; 
					}else if(k==3 && !(floorplan.isPartOfBorder(new Wallboard(i, j, intToDir(k))))) {
						edgeWeight[i][j][k]= edgeWeight[i-1][j][k]; 
					}
				}
			}
		}
	}
	
	/**
	 * By finding a minimum path using the findMin method(below), we are ready to find the actual wallboard that we
	 * are going to remove and connect to the next cell. With the minimum value, iterate all the wallboards of the cells 
	 * in the sets to find the list of wallboards that contain the minimum value. If there is more than one, 
	 * we randomly select a wallboard to remove.
	 * @param a a set of integers which represents the set of already connected cells.
	 * @return an array of integers that represent the coordinate of a cell and the direction of a wallboard, which will be removed.
	 */
	public int[] findMinPlace (Set<Integer> a){
		//information for wallboards: coordinate & direction
		int min = findMin(a);
		int[] dimension = new int [3];
		int[] copy = new int[3];
		int[] data = new int[2];
		int edge;
		int count =0;
		//list for wallboards that contain a minimum value
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		
		
		for(int i : a) {
			data = indxTodim(i);
			for(int k = 1; k<=4; k++) {
				Wallboard curWallboard = new Wallboard(data[0],data[1],intToDir(k));
				if(floorplan.canTearDown(curWallboard)) {
				edge = getEdgeWeight(data[0], data[1], intToDir(k));
					if(edge == min) {
						count++;
						//if the wallboard contains the minimum value, record its information the the array. 
						dimension[0] = data[0];
						dimension[1] = data[1];
						dimension[2] = k;
						
						//after recording, add a reference to candidates
						//edgeWeight[data[0]][data[1]][k] = 11;
						candidates.add(dimension);
					}
				}
			}
		}
		if (candidates.size() == 0) {
			candidates.add(dimension);
		}
		Random random = new Random();
		int index=0;
		index=random.nextInt(candidates.size());
		copy = candidates.get(index);
		edgeWeight[copy[0]][copy[1]][copy[2]] = 11;
		return candidates.get(index);
		
	}
	
	
	/**
	 * For each trees(sets), we have to find a minimum path to connect to the next cells. This method helps finding a minimum
	 * value of a wallboard of the cell in the set, which has not been removed yet.
	 * @param a a set of integers which represents the set of already connected cells.
	 * @return an integer that represents the smallest value of a wallboard that has not been removed.
	 */
	public int findMin(Set<Integer> a) {
		//initialize minimum as 11 since no wallboard has a higher value than 10. 
		int min = 11;
		int edge;
		for(int i : a) {
			int[] data = indxTodim(i);
			//for each cell, check all four wallboards. 
			for(int k = 1; k<=4; k++) {
				Wallboard curWallboard = new Wallboard(data[0],data[1],intToDir(k));
				edge = getEdgeWeight(data[0], data[1], intToDir(k));
				if(floorplan.canTearDown(curWallboard)) {
					if(edge < min) {
						min = edge;
					}
				}
			}
		}
		return min;
		
	}
	
	/**
	 * input an index value of a cell to know where the cell is located at. 
	 * @param a an index value of a cell
	 * @return an array containing two values: x coordinate and y coordinate of a cell.
	 */
	public int[] indxTodim(int a){
		int[] data = new int[2];
		data[0] = a % width;
		data[1] = a / width;
		return data;
	}
	
	/**
	 * input a coordinate of a cell to find an index value of the cell.
	 * @param a an array containing two values: x coordinate and y coordinate of a cell.
	 * @return an index value of a cell
	 */
	public int dimToindx(int[] a) {
		return a[1] * width + a[0];
	}
	
	/**
	 * Switch the direction to the corresponding integer. 
	 * @param cd a direction of the wallboard of the cell.
	 * @return an integer which represents a direction.
	 */
	public int dirToInt(CardinalDirection cd) {
		
		if(cd.equals(CardinalDirection.North)){
			return 1;
		}
		if(cd.equals(CardinalDirection.East)){
			return 2;
		}
		if(cd.equals(CardinalDirection.West)){
			return 3;
		}
		if(cd.equals(CardinalDirection.South)){
			return 4;
		}
		return 0;
	}
	
	/**
	 * Switch the integer that represents a direction to a direction itself.
	 * @param drInt an integer which represents a direction.
	 * @return a direction of the wallboard of the cell.
	 */
	public CardinalDirection intToDir(int drInt) {
		
		if(drInt == 1){
			return CardinalDirection.North;
		}
		if(drInt == 2){
			return CardinalDirection.East;
		}
		if(drInt == 3){
			return CardinalDirection.West;
		}
		if(drInt == 4){
			return CardinalDirection.South;
		}
		return CardinalDirection.North;
	}
	
	/**
	 * When the wallboard is removed, we have to add the cell that is newly connected by the absence of the wallboard.
	 * using the index value of a cell gained from the coordinate and the direction, we can determine which new cell to be
	 * connected based on which side of the wallboard is removed.
	 * @param a an array of an integer, which represents the coordinate of a cell and the direction of wallboard of a cell. 
	 * @param b a set of Integers, which represent a tree.
	 */
	public void updateSet(int[] a, Set<Integer> b) {
		int indx = dimToindx(a);
		//if the wallboard to the north is to be removed, connect the cell to the north. 
		if(a[2]==1) {
			edgeWeight[a[0]][a[1]][a[2]] = 11;
			b.add(indx-width);
		}
		//if the wallboard to the east is to be removed, connect the cell to the east. 
		else if (a[2]==2) {
			edgeWeight[a[0]][a[1]][a[2]] = 11;
			b.add(indx+1);
		}
		//if the wallboard to the west is to be removed, connect the cell to the west. 
		else if (a[2]==3) {
			edgeWeight[a[0]][a[1]][a[2]] = 11;
			b.add(indx-1);
		}
		//if the wallboard to the south is to be removed, connect the cell to the south. 
		else {
			edgeWeight[a[0]][a[1]][a[2]] = 11;
			b.add(indx+width);
		}
	}
	
	/**
	 * After each time sets are newly updated, we also have to update the list of sets. If sets have duplicate values,
	 * meaning that they contain the same cell, we have to merge them, since they are connected already.
	 * After moving all the values of one set to another, delete the set, so that we now have one big set.
	 * @param a an Arraylist of sets of integers, which represent the list of trees that are making the maze.
	 */
	public void updateList(ArrayList<Set<Integer>> a) {
		for(int i = 0; i<a.size()-1; i++) {
			for(int j = i; j<a.size(); j++) {
				if(isDuplicate(a.get(i), a.get(j))){
					//if two sets have duplicated, merge them into one
					for (int x : a.get(j)){
						a.get(i).add(x);
					}
					//remove one that gave all its values to another
					a.remove(j);
				}
			}
		}
	}
	
	/**
	 * By iterating through two sets, we can find whether there is a duplicate value in the set, meaning that
	 * two sets are supposed to be connected, since they have the same cell.
	 * @param a two sets of Integers, which represent two trees.
	 * @param b two sets of Integers, which represent two trees.
	 * @return whether two sets have duplicate values or not. 
	 */
	public boolean isDuplicate(Set<Integer> a, Set<Integer> b) {
		for(int i : a){
			for(int j : b){
				if(i==j) {
					return true;
				}
			}
		}return false;
	}
}
	

