package edu.wm.cs.cs301.MinKim.gui;
import edu.wm.cs.cs301.MinKim.gui.Robot.Direction;
import edu.wm.cs.cs301.MinKim.gui.Robot.Turn;

/**
 * 
 * @author Min Kim
 * 
 * This class represents the sensor state when all sensors are operational
 * It collaborates with RobotDriver.
 *
 */
public class OperationalState implements SensorState {
	private Robot robot;
	
	public OperationalState(Robot robot) {
		this.robot = robot;
	}
	
	/**
	 * Chooses which move to take next (move or rotate).
	 */
	@Override
	public boolean nextMove() {
		// Check that there is no wall to the left of the robot
		if (robot.distanceToObstacle(Direction.LEFT) != 0) {
			// turn left and take one step forward
			robot.rotate(Turn.LEFT);
			robot.move(1);
			return true;
		}
		// check that there is no wall in front of the robot
		else if (robot.distanceToObstacle(Direction.FORWARD) != 0) {
			// take one step forward
			robot.move(1);
			return true;
		}
		// Turn right
		else {
			robot.rotate(Turn.RIGHT);
			return false;
		}
	}

}
