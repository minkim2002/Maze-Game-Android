package edu.wm.cs.cs301.MinKim.gui;


/**
 * 
 * @author Min Kim
 * 
 * This class has the responsibility to know how far the wall is from the
 * direction it is looking at, move toward, turn 90 degree angle, and jump over
 * a wall. It works with sensors and manages its energy.
 * 
 * This class inherits ReliableRobot and collaborates with Controller, RobotDriver,
 * and DistanceSensor(Either Reliable or Unreliable).
 *
 */
public class UnreliableRobot extends ReliableRobot{
	public UnreliableRobot(int fw, int le, int ri, int bw) {
		super();
		//If the value is 0, unreliable, if the value is 1, reliable
		sensorForward = (fw == 0 ? new UnreliableSensor(Direction.FORWARD)
				: new ReliableSensor(Direction.FORWARD));
		sensorLeft = (le == 0 ? new UnreliableSensor(Direction.LEFT)
				: new ReliableSensor(Direction.LEFT));
		sensorRight = (ri == 0 ? new UnreliableSensor(Direction.RIGHT)
				: new ReliableSensor(Direction.RIGHT));
		sensorBackward = (bw == 0 ? new UnreliableSensor(Direction.BACKWARD)
				: new ReliableSensor(Direction.BACKWARD));
	}
}
