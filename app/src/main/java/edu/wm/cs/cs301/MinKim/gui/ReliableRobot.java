package edu.wm.cs.cs301.MinKim.gui;

import edu.wm.cs.cs301.MinKim.generation.CardinalDirection;
import edu.wm.cs.cs301.MinKim.generation.Maze;
import edu.wm.cs.cs301.MinKim.gui.Constants.UserInput;

/**
 * This class has the responsibility to know how far the wall is from the
 * direction it is looking at, move toward, turn 90 degree angle, and jump over
 * a wall.
 * 
 * This class implements Robot and uses ReliableSensor to add the sensors to the
 * robot.
 * 
 * @author Min Kim
 *
 */

public class ReliableRobot implements Robot {

	protected StatePlaying control;
	protected DistanceSensor sensorForward;
	protected DistanceSensor sensorLeft;
	protected DistanceSensor sensorRight;
	protected DistanceSensor sensorBackward;

	private Maze referenceMaze;
	private int width;
	private int height;

	private float battery;
	protected int distanceTraveled;
	protected boolean isStopped;
	
	protected final static float INITIAL_BATTERY = 3500;

	public ReliableRobot() {
		distanceTraveled = 0;
		setBatteryLevel(INITIAL_BATTERY);
		sensorForward = new ReliableSensor(Direction.FORWARD);
		sensorLeft = new ReliableSensor(Direction.LEFT);
		sensorRight = new ReliableSensor(Direction.RIGHT);
		sensorBackward = new ReliableSensor(Direction.BACKWARD);
	}

	/**
	 * Set a reference to the controller to cooperate with. The controller serves as
	 * the main source of information for the robot about the current position, the
	 * presence of walls, the reaching of an exit.
	 * 
	 * @param controller the communicator for robot
	 */
	@Override
	public void setController(StatePlaying controller) {
		
		// check if controller is null, controller is not in a playing state, or it doesn't have a maze
		if (controller == null || controller.getMaze() == null)
			throw new IllegalArgumentException();
		control = controller;
		
		//set traveled distance to zero
		resetOdometer();
		isStopped = false;
		//get the maze and its information
		referenceMaze = controller.getMaze();
		width = referenceMaze.getWidth();
		height = referenceMaze.getHeight();
		
		//set each sensor for each direction, 4 in total
		sensorForward.setMaze(referenceMaze);
		sensorLeft.setMaze(referenceMaze);
		sensorBackward.setMaze(referenceMaze);
		sensorRight.setMaze(referenceMaze);
	}

	/**
	 * Adds a distance sensor to the robot such that it measures in the given
	 * direction. This method is used when a robot is initially configured to get
	 * ready for operation. A robot can have at most four sensors in total, and at
	 * most one for any direction.
	 * 
	 * @param sensor the distance sensor to be added
	 * @param mountedDirection the direction that it points to relative to the
	 * robot's forward direction
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		if(mountedDirection == Direction.FORWARD) {
			sensorForward=sensor;
		}
		if(mountedDirection == Direction.LEFT) {
			sensorLeft=sensor;
		}
		if(mountedDirection == Direction.RIGHT) {
			sensorRight=sensor;
		}
		if(mountedDirection == Direction.BACKWARD) {
			sensorBackward=sensor;
		}
	}

	/**
	 * Get the current position as (x,y) coordinates for the maze as an array
	 * 
	 * @return array of length 2, x = array[0], y = array[1]
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] currentPosition = control.getCurrentPosition();
		
		// check if the current position is outside of the maze
		if (currentPosition[0] < 0 || currentPosition[0] >= width || currentPosition[1] < 0
				|| currentPosition[1] >= height) {
			throw new Exception();
		}
		return currentPosition;
	}

	/**
	 * Get the robot's current direction.
	 * 
	 * @return the robot's current direction in absolute terms
	 */
	@Override
	public CardinalDirection getCurrentDirection() {
		return control.getCurrentDirection();
	}

	/**
	 * Returns the current battery level.
	 * 
	 * @return current battery level
	 */
	@Override
	public float getBatteryLevel() {
		return battery;
	}

	/**
	 * Set the current battery level.
	 * 
	 * @param level the current battery level
	 */
	@Override
	public void setBatteryLevel(float level) {
		// check if battery level is negative
		if (level < 0) {
			throw new IllegalArgumentException();
		}
		battery = level;
	}

	/**
	 * Gives the energy consumption for a full 360 degree rotation. Scaling by other
	 * degrees approximates the corresponding consumption.
	 * 
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		return 12;
	}

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step. For
	 * moving a distance of n steps takes n times the energy for a single step.
	 * 
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		return 6;
	}

	/**
	 * Gets the distance traveled by the robot. The robot has an odometer that
	 * calculates the distance the robot has moved. Whenever the robot moves
	 * forward, the distance that it moves is added to the odometer counter. The
	 * odometer reading gives the path length if its setting is 0 at the start of
	 * the game. The counter can be reset to 0 with resetOdomoter().
	 * 
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		return distanceTraveled;
	}

	/**
	 * Resets the odometer counter to zero.
	 */
	@Override
	public void resetOdometer() {
		distanceTraveled = 0;
	}

	/**
	 * Turn robot on the spot for amount of degrees.
	 * 
	 * @param turn the direction to turn and relative to current forward direction.
	 */
	@Override
	public void rotate(Turn turn) {
		// check battery level
		if (getBatteryLevel() < 3 || (turn == Turn.AROUND && getBatteryLevel() < 6)) {
			setBatteryLevel(0);
			isStopped = true;
			return;
		}
		
		// Turn the robot and update the battery level
		switch (turn) {
		case LEFT:
			setBatteryLevel(getBatteryLevel() - 3);
			control.handleUserInput(UserInput.LEFT, 0);
			break;
		case RIGHT:
			setBatteryLevel(getBatteryLevel() - 3);
			control.handleUserInput(UserInput.RIGHT, 0);
			break;
		case AROUND:
			setBatteryLevel(getBatteryLevel() - 6);
			control.handleUserInput(UserInput.LEFT, 0);
			control.handleUserInput(UserInput.LEFT, 0);
			break;
		}
		
		// check if the battery level is 0 and stop the robot if so
		if (getBatteryLevel() == 0) {
			isStopped = true;
		}
	}

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell. If
	 * the robot runs out of energy somewhere on its way, it stops, which can be
	 * checked by hasStopped() == true and by checking the battery level. If the
	 * robot hits an obstacle like a wall, it remains at the position in front of
	 * the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual
	 * maze do not share a consistent view on where walls are and where not.
	 * 
	 * @param distance is the number of cells to move in the robot's current forward
	 *                 direction
	 */
	@Override
	public void move(int distance) {
		//Check battery
		if (getBatteryLevel() < 6) {
			setBatteryLevel(0);
			isStopped = true;
			return;
		}
		
		// create a new variable to keep track of distance moved
		int distanceMoved = 0;
		
		// while the distance moved is less than the inputed distance
		while (distanceMoved < distance) {
			try {
				int[] currentPosition = getCurrentPosition();
				// check if there is an obstacle in front of the robot and stop if so 
				if (distanceMoved != distance
						&& referenceMaze.hasWall(currentPosition[0], currentPosition[1], getCurrentDirection())) {
					setBatteryLevel(0);
					isStopped = true;
					break;
				}
			} catch (Exception e) {
				System.out.println("outside Maze");
				return;
			}
			// move the robot one step forward and update the distance traveled and battery level
			control.handleUserInput(UserInput.UP, 0);
			distanceTraveled++;
			distanceMoved++;
			setBatteryLevel(getBatteryLevel() - 6);
			
			// check if the energy has been run out and stop if so
			if (getBatteryLevel() == 0) {
				isStopped = true;
				break;
			}
			// check if there's enough energy to continue moving
			if (distanceMoved != distance && getBatteryLevel() < 6) {
				setBatteryLevel(0);
				isStopped = true;
				return;
			}
		}
	}

	/**
	 * Makes robot move in a forward direction even if there is a wall in front of
	 * it. In this sense, the robot jumps over the wall if necessary. The distance
	 * is always 1 step and the direction is always forward. If the robot runs out
	 * of energy somewhere on its way, it stops, which can be checked by
	 * hasStopped() == true and by checking the battery level. If the robot tries to
	 * jump over an exterior wall and would land outside of the maze that way, it
	 * remains at its current location and direction, hasStopped() == true as this
	 * is not supposed to happen.
	 */
	@Override
	public void jump() {
		// check battery
		if (getBatteryLevel() < 40) {
			setBatteryLevel(0);
			isStopped = true;
			return;
		}
		try {
			int[] currentPosition = getCurrentPosition();
			// check if the wall in front is a border and stop if so
			switch (getCurrentDirection()) {
			case North:
				if (referenceMaze.hasWall(currentPosition[0], currentPosition[1], CardinalDirection.North)
						&& currentPosition[1] - 1 < 0) {
					setBatteryLevel(0);
					isStopped = true;
					return;
				}
				break;
			case East:
				if (referenceMaze.hasWall(currentPosition[0], currentPosition[1], CardinalDirection.East)
						&& currentPosition[0] + 1 == width) {
					setBatteryLevel(0);
					isStopped = true;
					return;
				}
				break;
			case South:
				if (referenceMaze.hasWall(currentPosition[0], currentPosition[1], CardinalDirection.South)
						&& currentPosition[1] + 1 == height) {
					setBatteryLevel(0);
					isStopped = true;
					return;
				}
				break;
			case West:
				if (referenceMaze.hasWall(currentPosition[0], currentPosition[1], CardinalDirection.West)
						&& currentPosition[0] - 1 < 0) {
					setBatteryLevel(0);
					isStopped = true;
					return;
				}
				break;
			}
		} catch (Exception e) {
			System.out.println("Outside maze");
			return;
		}
		
		// jump and update the distance traveled and battery level
		control.handleUserInput(UserInput.JUMP, 0);
		distanceTraveled++;
		setBatteryLevel(getBatteryLevel() - 40);
		if (getBatteryLevel() == 0)
			isStopped = true;
	}

	/**
	 * Tells if the current position is right at the exit but still inside the maze.
	 * The exit can be in any direction. It is not guaranteed that the robot is
	 * facing the exit in a forward direction.
	 * 
	 * @return true if robot is at the exit, false otherwise
	 * @throws Exception
	 */
	@Override
	public boolean isAtExit() {
		int[] currentPosition;
		try {
			currentPosition = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Outside maze");
			return false;
		}
		return control.getMaze().getFloorplan().isExitPosition(currentPosition[0], currentPosition[1]);
	}

	/**
	 * Tells if current position is inside a room.
	 * 
	 * @return true if robot is inside a room, false otherwise
	 */
	@Override
	public boolean isInsideRoom() {
		int[] currentPosition;
		try {
			currentPosition = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Outside maze");
			return false;
		}
		return control.getMaze().getFloorplan().isInRoom(currentPosition[0], currentPosition[1]);
	}

	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an
	 * obstacle, etc. Once a robot is has stopped, it does not rotate or move
	 * anymore.
	 * 
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		return isStopped;
	}

	/**
	 * Tells the distance to an obstacle (a wall) in the given direction. The
	 * direction is relative to the robot's current forward direction. Distance is
	 * measured in the number of cells towards that obstacle, e.g. 0 if the current
	 * cell has a wallboard in this direction, 1 if it is one step forward before
	 * directly facing a wallboard, Integer.MaxValue if one looks through the exit
	 * into eternity. The robot uses its internal DistanceSensor objects for this
	 * and delegates the computation to the DistanceSensor which need to be
	 * installed by calling the addDistanceSensor() when configuring the robot.
	 * 
	 * @param direction specifies the direction of interest
	 * @return number of steps towards obstacle if obstacle is visible in a straight
	 *         line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if robot has no sensor in this
	 *                                       direction or the sensor exists but is
	 *                                       currently not operational
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		
		int[] currentPosition;
		try {
			currentPosition = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("Outside maze");
			return -1;
		}
		
		float[] batteryLevel = {getBatteryLevel()};
		int distance = -1;
		//pass it to a specific sensor depends on the direction
		try {
			switch (direction) {
			case FORWARD:
				distance = sensorForward.distanceToObstacle(currentPosition, getCurrentDirection(), batteryLevel);
				break;
			case LEFT:
				distance = sensorLeft.distanceToObstacle(currentPosition, getCurrentDirection(), batteryLevel);
				break;
			case RIGHT:
				distance = sensorRight.distanceToObstacle(currentPosition, getCurrentDirection(), batteryLevel);
				System.out.println(distance);
				break;
			case BACKWARD:
				distance = sensorBackward.distanceToObstacle(currentPosition, getCurrentDirection(), batteryLevel);
				break;
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg == "PowerFailure") {
				setBatteryLevel(0);
				isStopped = true;
			}
			throw new UnsupportedOperationException(msg);
		}
		setBatteryLevel(batteryLevel[0]);
		if (getBatteryLevel() == 0)
			isStopped = true;
		return distance;
	}

	/**
	 * Tells if a sensor can identify the exit in the given direction relative to
	 * the robot's current forward direction from the current position. It is a
	 * convenience method is based on the distanceToObstacle() method and transforms
	 * its result into a boolean indicator.
	 * 
	 * @param direction is the direction of the sensor
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this
	 *                                       direction or the sensor exists but is
	 *                                       currently not operational
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		try {
			return distanceToObstacle(direction) == Integer.MAX_VALUE ? true : false;
		} catch (Exception e) {
			throw new UnsupportedOperationException();
		}
	}


	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures,
			int meanTimeToRepair) throws UnsupportedOperationException {
		try {
			switch (direction) {
				case FORWARD:
					sensorForward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
					break;
				case LEFT:
					sensorLeft.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
					break;
				case RIGHT:
					sensorRight.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
					break;
				case BACKWARD:
					sensorBackward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
					break;
			}
		} catch (UnsupportedOperationException e) {
			throw new UnsupportedOperationException();
		}
	}
	
	
	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 * 
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 * 
	 * If called with no running failure and repair process, 
	 * the method will return an UnsupportedOperationException.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		try {
			switch (direction) {
				case FORWARD:
					sensorForward.stopFailureAndRepairProcess();
					break;
				case LEFT:
					sensorLeft.stopFailureAndRepairProcess();
					break;
				case RIGHT:
					sensorRight.stopFailureAndRepairProcess();
					break;
				case BACKWARD:
					sensorBackward.stopFailureAndRepairProcess();
					break;
			}
		} catch (UnsupportedOperationException e) {
			throw new UnsupportedOperationException();
		}
	}

}
