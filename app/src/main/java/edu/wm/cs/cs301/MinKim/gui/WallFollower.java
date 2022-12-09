package edu.wm.cs.cs301.MinKim.gui;

import edu.wm.cs.cs301.MinKim.gui.Robot.Direction;

/**
 * @author Min Kim
 * 
 * This class has the responsibility to get out of the maze.
 * 
 * This class inherits Wizard and uses 
 * Reliable and Unreliable Robot, which have reliable or unreliable sensors,
 * to navigate through the maze.
 *
 */

public class WallFollower extends Wizard {
	
	//Keep track of all the sensors attached to the robot
	public SensorState sensorState;
	
	//Indicators for whether a specific sensor is operational or not
	protected int leftDist;
	protected int forwardDist;
	protected boolean isExitVisible;
	
	public boolean forwardStatus;
	public boolean leftStatus;
	public boolean rightStatus;
	public boolean backwardStatus;
	
	
	
	/**
	 * Drive the robot towards the exit using the left wall-follower algorithm.
	 * @return true if the algorithm successfully drives the robot out of the maze.
	 * @throws Exception thrown if robot stopped due to a specific reason.
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while(!robot.hasStopped()) {
			//keep track of a robot's current position.
			int[] currentPosition;
			try {
				Thread.sleep(500);
				//One step
				drive1Step2Exit();
				//Update the robot's position.
				currentPosition = robot.getCurrentPosition();
			} catch (Exception e) {
				System.out.println(e);
				throw new Exception();
			}
			// Check if the robot is at the exit.
			if (robot.isAtExit()) {
				// Take one final step to the right direction, and return true. 
				exit2End(currentPosition);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Drive the robot one step towards the exit according to the left wall-follower algorithm.
	 * @return true if the algorithm successfully moved the robot once.
	 * @throws Exception thrown if robot stopped due to a specific reason.
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		if(robot.hasStopped()) {
			throw new Exception();
		}
		
		//Indicator for whether the robot moved or not.
		boolean moved = false;
		
		//Check whether sensors are in need of repair.
		forwardStatus = isOperational(Direction.FORWARD);
		leftStatus = isOperational(Direction.LEFT);
		//Either forward or left sensor is not working
		if(!forwardStatus || !leftStatus) {
			rightStatus = isOperational(Direction.RIGHT);
			backwardStatus = isOperational(Direction.BACKWARD);
			boolean[] sensors = {forwardStatus, leftStatus, rightStatus, backwardStatus};
			// If all the sensors need to be repaired
			if (!sensors[0] && !sensors[1] && !sensors[2] && !sensors[3])
				waitTilOperational(sensors);
			// Reset the sensors
			setState(sensors[0], sensors[1], sensors[2], sensors[3]);
		//All sensors are operational
		} else {
			setState(true, true, true, true);
		}
		//According to the operational status of each sensor, take next move.
		moved = sensorState.nextMove();
		//If the robot stopped, throw an exception.
		if (robot.hasStopped()) {
			throw new Exception();
		}
		
		return moved;
	}
	
	/**
	 * Check if the sensor in that direction is operational.
	 * @param direction of the sensor to check
	 * @return whether the sensor is operational
	 */
	public boolean isOperational(Direction direction) {
		try {
			//Update the current robot's situation
			int dist = robot.distanceToObstacle(direction);
			if (direction == Direction.LEFT) {
				leftDist = dist;
			}
			if (direction == Direction.FORWARD) {
				forwardDist = dist;
			}
			isExitVisible = (dist == Integer.MAX_VALUE);
			//Sensor Operational: confirmed
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}
	
	/**
	 * Waits for a sensor to be operational again before future steps.
	 */
	public void waitTilOperational(boolean[] sensors) {
		while(!sensors[0] && !sensors[1] && !sensors[2] && !sensors[3]) {
			try {
				//Sleep for 2 seconds.
				Thread.sleep(2000);
				
				//Recheck the status.
				sensors[0] = isOperational(Direction.FORWARD);
				sensors[1] = isOperational(Direction.LEFT);
			} catch (Exception e) {
				System.err.println("Something went wrong!");
				return;
			}
		}
	}
	
	/**
	 * Sets the state of the driver to either the OperationalState or the RepairState
	 * @param forward status of the forward sensor
	 * @param left status of the left sensor
	 * @param right status of the right sensor
	 * @param backward status of the backward sensor
	 */
	public void setState(boolean forward, boolean left, boolean right, boolean backward) {
		sensorState = (forward && left && right && backward) ? new OperationalState(robot)
				: new RepairState(forward, left, right, backward, robot);
	}
	
	
}

