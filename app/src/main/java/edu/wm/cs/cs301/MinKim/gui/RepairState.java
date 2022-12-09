package edu.wm.cs.cs301.MinKim.gui;

import edu.wm.cs.cs301.MinKim.gui.Robot.Direction;
import edu.wm.cs.cs301.MinKim.gui.Robot.Turn;

/**
 * 
 * @author Min Kim
 * 
 * This class represents the sensor state when at least one sensor is not operational.
 * It keeps records of sensors under repair and get the closest operational sensor.
 * It collaborates with RobotDriver.
 *
 */

public class RepairState implements SensorState {
	
	protected Robot robot;
	
	protected boolean forwardSensorStatus;
	protected boolean leftSensorStatus;
	protected boolean rightSensorStatus;
	protected boolean backwardSensorStatus;

	public RepairState(boolean forward, boolean left, boolean right, boolean backward, Robot robot) {
		forwardSensorStatus = forward;
		leftSensorStatus = left;
		rightSensorStatus = right;
		backwardSensorStatus = backward;
		
		this.robot = robot;
	}
	
	/**
	 * Chooses which action to take next move (move or rotate) and executes it.
	 **/
	@Override
	public boolean nextMove() {
		//Get next closest operational sensors for both forward and left
		Direction workingSensorForForward = GetSpareOperationalSensor(Direction.FORWARD);
		Direction workingSensorForLeft = GetSpareOperationalSensor(Direction.LEFT);
		//Switch left sensor with an operating one
		switchSensor(Direction.LEFT, workingSensorForLeft);
		if(robot.distanceToObstacle(workingSensorForLeft) != 0){
			//After checking the distance, switch the sensor back
			switchSensor(workingSensorForLeft, Direction.LEFT);
			robot.rotate(Turn.LEFT);
			robot.move(1);
			return true;
		} else {
			switchSensor(workingSensorForLeft, Direction.LEFT);
		}
		
		//Switch forward sensor with an operating one
		switchSensor(Direction.FORWARD, workingSensorForForward);
		
		if(robot.distanceToObstacle(workingSensorForForward) != 0){
			//After checking the distance, switch the sensor back
			switchSensor(workingSensorForForward, Direction.FORWARD);
			robot.move(1);
			return true;
		} else {
			switchSensor(workingSensorForForward, Direction.FORWARD);
		}
		
		//If above two aren't the case, turn right.
		robot.rotate(Turn.RIGHT);
		return false;
	}
	
	
	/**
	 * Get the closest operational sensor
	 * @param direction that is being examined
	 * @return the direction of the closest operational sensor
	 */
	private Direction GetSpareOperationalSensor (Direction direction) {
		//All sensors are being repaired, return null
		if(!forwardSensorStatus && !leftSensorStatus && !rightSensorStatus && !backwardSensorStatus) {
			return null;
		}
		
		//If the sensor is operational, return that
		if(getSensorStatus(direction)) {
			return direction;
		}
		
		//Check the closest operational sensor.
		else {
			switch (direction) {
				case FORWARD:
					if (leftSensorStatus) {
						return Direction.LEFT;
					}
					if (rightSensorStatus) {
						return Direction.RIGHT;
					}
					if (backwardSensorStatus) {
						return Direction.BACKWARD;
					}
				case LEFT:
					if (backwardSensorStatus) {
						return Direction.BACKWARD;
					}
					if (forwardSensorStatus) {
						return Direction.FORWARD;
					}
					if (rightSensorStatus) {
						return Direction.RIGHT;
					}
				case RIGHT:
					if (forwardSensorStatus) {
						return Direction.FORWARD;
					}
					if (backwardSensorStatus) {
						return Direction.BACKWARD;
					}
					if (leftSensorStatus) {
						return Direction.LEFT;
					}
				case BACKWARD:
					if (rightSensorStatus) {
						return Direction.RIGHT;
					}
					if (leftSensorStatus) {
						return Direction.LEFT;
					}
					if (forwardSensorStatus) {
						return Direction.FORWARD;
					}
				default:
					throw new UnsupportedOperationException();
			}
		}
	}
	
	/**
	 * Get the status of a sensor
	 * @param direction of the sensor being examined
	 * @return whether the sensor is operational
	 */
	private boolean getSensorStatus(Direction direction) {
		switch (direction) {
			case FORWARD:
				return forwardSensorStatus;
			case LEFT:
				return leftSensorStatus;
			case RIGHT:
				return rightSensorStatus;
			case BACKWARD:
				return backwardSensorStatus;
			default:
				return false;
		}
	}
	
	/**
	 * Rotate the robot so that the spare sensor can work instead
	 * @param current the direction of the current sensor that needs to be repaired
	 * @param spare the direction of the spare sensor that is going to work as a substitute
	 */
	private void switchSensor(Direction current, Direction spare) {
		//Switch the sensor by simply rotating the robot.
		switch (current) {
			case FORWARD:
				if (spare == Direction.LEFT) {
					robot.rotate(Turn.RIGHT);
				} 
				else if (spare == Direction.RIGHT) {
					robot.rotate(Turn.LEFT);
				} 
				else if(spare == Direction.BACKWARD) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case LEFT:
				if (spare == Direction.BACKWARD) {
					robot.rotate(Turn.RIGHT);
				}
				else if (spare == Direction.FORWARD) {
					robot.rotate(Turn.LEFT);
				}
				else if (spare == Direction.RIGHT) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case RIGHT:
				if (spare == Direction.FORWARD) {
					robot.rotate(Turn.RIGHT);
				}
				else if (spare == Direction.BACKWARD) {
					robot.rotate(Turn.LEFT);
				}
				else if (spare == Direction.LEFT) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case BACKWARD:
				if (spare == Direction.RIGHT) {
					robot.rotate(Turn.RIGHT);
				}
				else if (spare == Direction.LEFT) {
					robot.rotate(Turn.LEFT);
				}
				else if (spare == Direction.FORWARD) {
					robot.rotate(Turn.AROUND);
				}
				break;
		}
	}

}
