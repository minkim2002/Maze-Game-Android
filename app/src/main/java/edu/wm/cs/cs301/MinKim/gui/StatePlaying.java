package edu.wm.cs.cs301.MinKim.gui;

import edu.wm.cs.cs301.MinKim.generation.CardinalDirection;
import edu.wm.cs.cs301.MinKim.generation.Floorplan;
import edu.wm.cs.cs301.MinKim.generation.Maze;
import edu.wm.cs.cs301.MinKim.gui.Constants.UserInput;

/**
 * Class handles the user interaction while the game is in the third stage where
 * the user plays the game. This class is part of a state pattern for the
 * Controller class.
 * 
 * It implements a state-dependent behavior that controls the display and reacts
 * to key board input from a user. At this point user keyboard input is first
 * dealt with a key listener in control and then and then handed over by way of
 * the handleUserInput method.
 * 
 * Responsibilities: Keep track of the current position and direction in the
 * game. Show the first person view and the map view, Accept input for manual
 * operation (left, right, up, down etc), Update the graphics, recognize
 * termination.
 *
 * This code contains refactored code from Maze.java by Paul Falstad,
 * www.falstad.com, Copyright (C) 1998, all rights reserved Paul Falstad granted
 * permission to modify and use code for teaching purposes. Refactored by Peter
 * Kemper
 */
public class StatePlaying implements State {
	/**
	 * The compass rose provides additional guidance on the current direction and
	 * visualizes it with a compass rose. It draws on top of the first person view.
	 * As map and compass rose compete for space on the screen, one can show at most
	 * one of the two at any point in time.
	 */
	private CompassRose cr;

	/**
	 * Maze holds the main information on where walls are.
	 */
	Maze maze;
	int distTraveled;
	int walkStep;

	private boolean showMaze; // toggle switch to show overall maze on screen
	private boolean showSolution; // toggle switch to show solution in overall maze on screen
	private boolean mapMode; // true: display map of maze, false: do not display map of maze
	// mapMode is toggled by user keyboard input, causes a call to drawMap during
	// play mode

	// current position and direction with regard to MazeConfiguration
	int px, py; // current position on maze grid (x,y)
	CardinalDirection cd;

	Floorplan seenCells; // a matrix with cells to memorize which cells are visible from the current
							// point of view
	// the FirstPersonView obtains this information and the Map uses it for
	// highlighting currently visible walls on the map

	// debug stuff
	// private boolean deepdebug = false;
	// private boolean allVisible = false;
	// private boolean newGame = false;

	/**
	 * Started is used to enforce ordering constraint on method calls. start() must
	 * be called before keyDown() to make sure control variable has been set.
	 * initial setting: false, start sets it to true.
	 */
	boolean started;

	PlayActivity play;
	MazePanel mazePanel;
	FirstPersonView firstPersonView;
	Map mapView;

	/**
	 * Constructor uses default settings but does not deliver a fully operation
	 * instance, requires a call to start() and setMaze().
	 */
	public StatePlaying() {
		started = false;
	}

	/**
	 * Provides the maze to play.
	 * 
	 * @param maze a fully operational complete maze ready to play
	 */
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	/**
	 * Start the actual game play by showing the playing screen. If the panel is
	 * null, all drawing operations are skipped. This mode of operation is useful
	 * for testing purposes, i.e., a dryrun of the game without the graphics part.
	 * 
	 * @param playActivity provides access to the controller this state resides in
	 * @param panel      is part of the UI and visible on the screen, needed for
	 *                   drawing
	 */
	public void start(PlayActivity playActivity, MazePanel panel) {
		started = true;
		// keep the reference to the controller to be able to call method to switch the
		// state
		play = playActivity;
		// keep the reference to the panel for drawing
		mazePanel = panel;
		//
		// adjust visibility settings to default value
		showMaze = false;
		showSolution = false;
		mapMode = false;
		// adjust internal state of maze model
		// init data structure for visible walls
		seenCells = new Floorplan(maze.getWidth() + 1, maze.getHeight() + 1);
		// set the current position and direction consistently with the viewing
		// direction
		setPositionDirectionViewingDirection();
		walkStep = 0;
		if (panel != null) {
			startDrawer();
		} else {
			// else: dry-run without graphics, most likely for testing purposes
			printWarning();
		}
	}

	/**
	 * Initializes the drawer for the first person view and the map view and then
	 * draws the initial screen for this state.
	 */
	protected void startDrawer() {
		cr = new CompassRose(mazePanel);
		cr.setPositionAndSize(Constants.VIEW_WIDTH / 2, (int) (0.1 * Constants.VIEW_HEIGHT), 145);

		firstPersonView = new FirstPersonView(mazePanel, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
				Constants.STEP_SIZE, seenCells, maze.getRootnode());

		mapView = new Map(seenCells, 75, maze);
		// draw the initial screen for this state
		draw(cd.angle(), 0);
	}

	/**
	 * Internal method to set the current position, the direction and the viewing
	 * direction to values consistent with the given maze.
	 */
	private void setPositionDirectionViewingDirection() {
		int[] start = maze.getStartingPosition();
		setCurrentPosition(start[0], start[1]);
		cd = CardinalDirection.East;
	}

	/**
	 * The method provides an appropriate response to user keyboard input. The
	 * control calls this method to communicate input and delegate its handling.
	 * Method requires {@link #start(PlayActivity, MazePanel) start} to be called before.
	 * 
	 * @param userInput provides the feature the user selected
	 * @param value     is not used in this state, exists only for consistency
	 *                  across State classes
	 * @return false if not started yet otherwise true
	 */
	public boolean handleUserInput(UserInput userInput, int value) {
		// user input too early, not sure how this could happen
		if (!started) {
			return false;
		}

		// react to input for directions and interrupt signal (ESCAPE key)
		// react to input for displaying a map of the current path or of the overall
		// maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		switch (userInput) {
		case START: // misplaced, do nothing
			break;
		case UP: // move forward
			walk(1);
			// check termination, did we leave the maze?
			if (isOutside(px, py)) {
				play.switchToWinning(play, distTraveled);
			}
			break;
		case LEFT: // turn left
			rotate(1);
			break;
		case RIGHT: // turn right
			rotate(-1);
			break;
		case DOWN: // move backward
			walk(-1);
			// check termination, did we leave the maze?
			if (isOutside(px, py)) {
				play.switchToWinning(play, distTraveled);
			}
			break;
		case RETURNTOTITLE: // escape to title screen
			break;
		case JUMP: // make a step forward even through a wall
			// go to position if within maze
			int[] tmpDxDy = cd.getDxDyDirection();
			if (maze.isValidPosition(px + tmpDxDy[0], py + tmpDxDy[1])) {
				setCurrentPosition(px + tmpDxDy[0], py + tmpDxDy[1]);
				draw(cd.angle(), 0);
			}
			break;
		case TOGGLELOCALMAP: // show local information: current position and visible walls
			// precondition for showMaze and showSolution to be effective
			// acts as a toggle switch
			mapMode = !mapMode;
			draw(cd.angle(), 0);
			break;
		case TOGGLEFULLMAP: // show the whole maze
			// acts as a toggle switch
			showMaze = !showMaze;
			draw(cd.angle(), 0);
			break;
		case TOGGLESOLUTION: // show the solution as a yellow line towards the exit
			// acts as a toggle switch
			showSolution = !showSolution;
			draw(cd.angle(), 0);
			break;
		case ZOOMIN: // zoom into map
			mapView.incrementMapScale();
			draw(cd.angle(), 0);
			break;
		case ZOOMOUT: // zoom out of map
			mapView.decrementMapScale();
			draw(cd.angle(), 0);
			break;
		} // end of internal switch statement for playing state
		return true;
	}

	/**
	 * Draws the current content on panel to show it on screen.
	 */
	protected void draw(int angle, int walkStep) {

		if (mazePanel == null) {
			printWarning();
			return;
		}
		// draw the first person view and the map view if wanted
		firstPersonView.draw(mazePanel, px, py, walkStep, angle, maze.getPercentageForDistanceToExit(px, py));
		if (isInMapMode()) {
			mapView.draw(mazePanel, px, py, angle, walkStep, isInShowMazeMode(), isInShowSolutionMode());
		}
		// update the screen with the buffer graphics
		mazePanel.commit();
	}

	/**
	 * Prints the warning about a missing panel only once
	 */
	boolean printedWarning = false;

	protected void printWarning() {
		if (printedWarning)
			return;
		System.out.println("No panel for drawing during executing, dry-run game without graphics!");
		printedWarning = true;
	}

	////////////////////////////// set methods
	////////////////////////////// ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model
	////////////////////////////// ///////////////////////////
	protected void setCurrentPosition(int x, int y) {
		px = x;
		py = y;
	}

	////////////////////////////// get methods
	////////////////////////////// ///////////////////////////////////////////////////////////////
	protected int[] getCurrentPosition() {
		int[] result = new int[2];
		result[0] = px;
		result[1] = py;
		return result;
	}

	protected CardinalDirection getCurrentDirection() {
		return cd;
	}

	boolean isInMapMode() {
		return mapMode;
	}

	boolean isInShowMazeMode() {
		return showMaze;
	}

	boolean isInShowSolutionMode() {
		return showSolution;
	}

	public Maze getMaze() {
		return maze;
	}
	//////////////////////// Methods for move and rotate operations ///////////////

	/**
	 * Determines if one can walk in the given direction
	 * 
	 * @param dir is the direction of interest, either 1 or -1
	 * @return true if there is no wall in this direction, false otherwise
	 */
	public boolean wayIsClear(int dir) {
		CardinalDirection cd = null;
		switch (dir) {
		case 1: // forward
			cd = getCurrentDirection();
			return !maze.hasWall(px, py, cd);
		case -1: // backward
			cd = getCurrentDirection().oppositeDirection();
			return !maze.hasWall(px, py, cd.oppositeDirection());
		default:
			throw new RuntimeException("Unexpected direction value: " + dir);
		}
	}

	/**
	 * Draws and waits. Used to obtain a smooth appearance for rotate and move
	 * operations
	 */
	private void slowedDownRedraw(int angle, int walkStep) {
		draw(angle, walkStep);
		try {
			Thread.sleep(25);
		} catch (Exception e) {
			// may happen if thread is interrupted
			// no reason to do anything about it, ignore exception
		}
	}

	/**
	 * Performs a rotation with 4 intermediate views, updates the screen and the
	 * internal direction
	 * 
	 * @param dir for current direction, values are either 1 or -1
	 */
	public synchronized void rotate(int dir) {
		final int originalAngle = cd.angle();// angle;
		final int steps = 4;
		int angle = originalAngle;
		for (int i = 0; i != steps; i++) {
			// add 1/4 of 90 degrees per step
			// if dir is -1 then subtract instead of addition
			angle = originalAngle + dir * (90 * (i + 1)) / steps;
			angle = (angle + 1800) % 360;
			// draw method is called and uses angle field for direction
			// information.
			slowedDownRedraw(angle, 0);
		}
		// update maze direction only after intermediate steps are done
		// because choice of direction values are more limited.
		cd = CardinalDirection.getDirection(angle);
		drawHintIfNecessary();
	}

	/**
	 * Moves in the given direction with 4 intermediate steps, updates the screen
	 * and the internal position
	 * 
	 * @param dir, only possible values are 1 (forward) and -1 (backward)
	 */
	public synchronized void walk(int dir) {
		// check if there is a wall in the way
		if (!wayIsClear(dir))
			return;
		// walkStep is a parameter of FirstPersonView.draw()
		// it is used there for scaling steps
		// so walkStep is implicitly used in slowedDownRedraw
		// which triggers the draw operation in
		// FirstPersonView and Map
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			slowedDownRedraw(cd.angle(), walkStep);
		}
		// update position to neighbor
		int[] tmpDxDy = cd.getDxDyDirection();
		setCurrentPosition(px + dir * tmpDxDy[0], py + dir * tmpDxDy[1]);
		walkStep = 0;
		distTraveled++;
		drawHintIfNecessary();
	}

	/**
	 * Checks if the given position is outside the maze
	 * 
	 * @param x coordinate of position
	 * @param y coordinate of position
	 * @return true if position is outside, false otherwise
	 */
	private boolean isOutside(int x, int y) {
		return !maze.isValidPosition(x, y);
	}

	/**
	 * Draw a visual cue to help the user unless the map is on display anyway. This
	 * is the map if current position faces a dead end otherwise it is a compass
	 * rose.
	 */
	private void drawHintIfNecessary() {
		if (isInMapMode())
			return; // no need for help
		// in testing environments, there is sometimes no panel to draw on
		// or the panel is unable to deliver a graphics object
		// check this and quietly move on if drawing is impossible
		if (mazePanel == null){
			printWarning();
			return;
		}
		// if current position faces a dead end, show map with solution
		// for guidance
		CardinalDirection cd = null;
		cd = getCurrentDirection();
		if (maze.isFacingDeadEnd(px, py, cd)) {
			// System.out.println("Facing deadend, help by showing solution");
			mapView.draw(mazePanel, px, py, cd.angle(), 0, true, true);
		} else {
			// draw compass rose
			cr.setCurrentDirection(cd);
			cr.paintComponent(mazePanel);
		}
		mazePanel.commit();
	}

	@Override
	public void start(MazePanel panel) {
		throw new RuntimeException("DefaultState:using unimplemented method");
	}

	/////////////////////// Methods for debugging ////////////////////////////////


}
