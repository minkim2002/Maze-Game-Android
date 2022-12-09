package edu.wm.cs.cs301.MinKim.gui;

import edu.wm.cs.cs301.MinKim.generation.CardinalDirection;
import edu.wm.cs.cs301.MinKim.generation.Maze;
import edu.wm.cs.cs301.MinKim.gui.Robot.Turn;

/**
 * This class has the responsibility to get out of the maze as quick as
 * possible, be the most efficient in terms of energy consumption and path
 * length.
 * 
 * This class implements RobotDriver and uses Maze to find the exit by looking
 * for a neighbor that is closer to the exit. Also, it uses Reliable Robot to
 * navigate through the maze.
 * 
 * @author Min Kim
 *
 */

public class Wizard implements RobotDriver {

	protected Robot robot;
	protected Maze referenceMaze;
	protected float initialBattery;

	/**
	 * Assigns a robot platform to the driver. The driver uses a robot to perform,
	 * this method provides it with this necessary information.
	 * 
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		robot = r;
		initialBattery = robot.getBatteryLevel();
	}

	/**
	 * Set the robot driver with the maze information.
	 * 
	 * @param maze represents the maze
	 */
	@Override
	public void setMaze(Maze maze) {
		referenceMaze = maze;
	}

	/**
	 * Drives the robot towards the exit following its solution strategy and given
	 * the exit exists and given the robot's energy supply lasts long enough. When
	 * the robot reached the exit position and its forward direction points to the
	 * exit the search terminates and the method returns true. If the robot failed
	 * due to lack of energy or crashed, the method throws an exception. If the
	 * method determines that it is not capable of finding the exit it returns
	 * false, for instance, if it determines it runs in a cycle and can't resolve
	 * this.
	 * 
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem.
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while (!robot.hasStopped()) {
			int[] currentPosition;
			// run drive1Step2Exit
			try {
				drive1Step2Exit();
				// update position
				currentPosition = robot.getCurrentPosition();
			} catch (Exception e) {
				throw new Exception();
			}
			// check if it has reached the exit
			if (robot.isAtExit()) {
				// cross the exit to win the game
				exit2End(currentPosition);
				return true;
			}
		}
		return false;
	}

	/**
	 * Drives the robot one step towards the exit following its solution strategy
	 * and given the exists and given the robot's energy supply lasts long enough.
	 * the wizard uses the information of the Maze object (handed to it via the
	 * setMaze method) to find the exit by looking for a neighbor that is closer to
	 * the exit. It returns true if the driver successfully moved the robot from its
	 * current location to an adjacent location. At the exit position, it rotates
	 * the robot such that if faces the exit in its forward direction and returns
	 * false. If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * 
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of
	 *                   energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		// get the next neighbor closest to the exit
		int[] currentPosition = robot.getCurrentPosition();
		int[] neighbor = referenceMaze.getNeighborCloserToExit(currentPosition[0], currentPosition[1]);
		// rotate robot in that direction
		CardinalDirection currentDirection = robot.getCurrentDirection();
		int[] changeDirection = { neighbor[0] - currentPosition[0], neighbor[1] - currentPosition[1] };
		switch (currentDirection) {
		case North:
			if (changeDirection[0] == -1)
				robot.rotate(Turn.RIGHT);
			else if (changeDirection[0] == 1)
				robot.rotate(Turn.LEFT);
			else if (changeDirection[1] == 1)
				robot.rotate(Turn.AROUND);
			break;
		case East:
			if (changeDirection[0] == -1)
				robot.rotate(Turn.AROUND);
			else if (changeDirection[1] == -1)
				robot.rotate(Turn.RIGHT);
			else if (changeDirection[1] == 1)
				robot.rotate(Turn.LEFT);
			break;
		case South:
			if (changeDirection[0] == -1)
				robot.rotate(Turn.LEFT);
			else if (changeDirection[0] == 1)
				robot.rotate(Turn.RIGHT);
			else if (changeDirection[1] == -1)
				robot.rotate(Turn.AROUND);
			break;
		case West:
			if (changeDirection[0] == 1)
				robot.rotate(Turn.AROUND);
			else if (changeDirection[1] == -1)
				robot.rotate(Turn.LEFT);
			else if (changeDirection[1] == 1)
				robot.rotate(Turn.RIGHT);
			break;
		}
		// move by either jumping or moving
		if (referenceMaze.hasWall(currentPosition[0], currentPosition[1], currentDirection))
			robot.jump();
		else
			robot.move(1);
		// throw an exception when robot has stopped
		if (robot.hasStopped())
			throw new Exception();
		return true;
	}
	
	/**
	 * Rotates the robot to face the exit and then move once to exit
	 * @param currentPosition of the robot at the exit
	 */
	protected boolean exit2End(int[] currentPosition) {
		// check whether the direction has a wall and the adjacent cell is outside the maze
		while (referenceMaze.hasWall(currentPosition[0], currentPosition[1], robot.getCurrentDirection()) 
			|| !isNeighborOutsideMaze(currentPosition, robot.getCurrentDirection())) {
			robot.rotate(Turn.LEFT);
		}
		// exit
		robot.move(1);
		return true;
	}
	
	/**
	 * Check if the neighbor of the current cell is outside of the maze.
	 * @param currentPosition of the robot
	 * @param currentDirection the direction used
	 * @return whether the neighbor is outside of the maze
	 */
	protected boolean isNeighborOutsideMaze(int[] currentPosition, CardinalDirection currentDirection) {
		switch (currentDirection) {
		case North:
			if (currentPosition[1]-1<0) return true;
			break;
		case East:
			if (currentPosition[0]+1>=referenceMaze.getWidth()) return true;
			break;
		case South:
			if (currentPosition[1]+1>=referenceMaze.getHeight()) return true;
			break;
		case West:
			if (currentPosition[0]-1<0) return true;
			break;
		}
		return false;
	}

	/**
	 * Returns the total energy consumption of the journey, i.e., the difference
	 * between the robot's initial energy level at the starting position and its
	 * energy level at the exit position. This is used as a measure of efficiency
	 * for a robot driver.
	 * 
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		// TODO Auto-generated method stub
		return initialBattery - robot.getBatteryLevel();
	}

	/**
	 * Returns the total length of the journey in number of cells traversed. Being
	 * at the initial position counts as 0. This is used as a measure of efficiency
	 * for a robot driver.
	 * 
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		// TODO Auto-generated method stub
		return robot.getOdometerReading();
	}

}

