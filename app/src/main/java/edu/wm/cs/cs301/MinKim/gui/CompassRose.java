package edu.wm.cs.cs301.MinKim.gui;

import android.graphics.Color;

import edu.wm.cs.cs301.MinKim.generation.CardinalDirection;


/**
 * A component that draws a compass rose.  
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 * Code copied from http://www.soupwizard.com/openrocket/code-coverage/eclemma-20121216/OpenRocket/src/net.sf.openrocket.gui.components.compass/CompassRose.java.html
 * adjusted for Maze setting by
 * @author Peter Kemper
 */
public class CompassRose {
	private static final long serialVersionUID = 1916497172430988388L;
	
	
	// fixed configuration for arms
    private static final float MAIN_LENGTH = 0.95f;
    private static final float MAIN_WIDTH = 0.15f;
    
    // fixed configuration for circle surrounding arms
    private static final int CIRCLE_BORDER = 2;
    
    // The scaler of the rose.  
    // The bordering circle will be this portion of the component dimensions.
    private double scaler;
    
    // The radius for the marker positions (N/E/S/W), or NaN for no markers.  
    // A value greater than one
    // will position the markers outside of the bordering circle.
    // Current default setting in default constructor is 1.7
    private double markerRadius;
	private String markerFont;

    // (x,y) coordinates of center point on overall area
    private int centerX; // x coordinate of center point
    private int centerY; // y coordinate of center point
    private int size; // size of compass rose
    private CardinalDirection currentDir; // current direction in maze
    
    /**
     * Construct a compass rose with the default settings.
     */
    public CompassRose(MazePanel mazePanel) {
        this(mazePanel, 0.9, 1.7, "Serif-PLAIN-16");
    }
     
    /**
     * Construct a compass rose with the specified settings.
     * 
     * @param scaler        The scaler of the rose.  The bordering circle will be this portion of the component dimensions.
     * @param markerRadius  The radius for the marker positions (N/E/S/W), or NaN for no markers.  A value greater than one
     *                      will position the markers outside of the bordering circle.
     * @param markerFont    The font used for the markers.
     */
    public CompassRose(MazePanel mazePanel, double scaler, double markerRadius, String markerFont) {
        this.scaler = scaler;
        this.markerRadius = markerRadius;
        setMarkerFont(mazePanel, markerFont);
    }
    /**
     * Sets the center position for the compass rose and its size
     * @param x The x coordinate of the center point
     * @param y The x coordinate of the center point
     * @param size The size of the compass rose
     */
    public void setPositionAndSize(int x, int y, int size) {
    	centerX = x;
    	centerY = y;
    	this.size = size;
    }
    /**
     * Set the current direction such that it can
     * be highlighted on the display
     * @param cd The current cardinal direction
     */
    public void setCurrentDirection(CardinalDirection cd) {
    	currentDir = cd;
    }

    /**
     * Paint a compass rose on the given graphics object.
     * For compatibility with the JComponent class, this 
     * method's signature matches with the corresponding
     * one in JComponent.
     */
    public void paintComponent(MazePanel mazePanel) {
        /* Original code
        Dimension dimension = this.getSize();
        int width = Math.min(dimension.width, dimension.height);
        int mid = width / 2;
        width = (int) (scaler * width);
        */
        
        // Determine the dimensions for the visualization
        int width = (int) (scaler * size);
        final int armLength = (int) (width * MAIN_LENGTH / 2);
        final int armWidth = (int) (width * MAIN_WIDTH / 2);
        
        // Set rendering hints to adjust quality of rendering  
        mazePanel.setRenderingHint(P7PanelF22.P7RenderingHints.KEY_RENDERING, P7PanelF22.P7RenderingHints.VALUE_RENDER_QUALITY);
        mazePanel.setRenderingHint(P7PanelF22.P7RenderingHints.KEY_ANTIALIASING, P7PanelF22.P7RenderingHints.VALUE_ANTIALIAS_ON);
        
        /*
         * The compass rose is drawn from several components, 
         * a filled white circle as the background,
         * one arm for each direction, starting at the center point
         * a circle connecting the end points of each arm
         * one letter to tell the direction, positioned at end of each arm
         */
        drawBackground(mazePanel);
        drawArms(mazePanel, armLength, armWidth);
        drawBorderCircle(mazePanel, width); // note: not currently visible due to color settings
        drawDirectionMarker(mazePanel, width);
    }

    /**
     * Draw four arms. One in each direction. 
     * An arm is visualized with 2 triangles.
     * @param mazePanel The graphics object to draw on
     * @param length The length of an arm
     * @param width The width of an arm
     */
	private void drawArms(final MazePanel mazePanel, final int length, final int width) {
		// Each arm of the compass rose is a symbol created
        // with 2 triangles, one filled, the other one not
        // The first point in the triangle is always the center point.
		// We share the int arrays for the 3 points among method calls for drawing.
		// The first entry, the starting point is set here and never changed.
		// Methods for drawing adjust the setting for the 2nd and 3rd point.
        final int[] x = new int[3];
        final int[] y = new int[3];
        x[0] = centerX;
        y[0] = centerY;
        // use the same color for all arms
        mazePanel.setColor(Color.BLACK);
        // draw each arm
        drawArmNorth(mazePanel, length, width, x, y);
        drawArmEast(mazePanel, length, width, x, y);
        drawArmSouth(mazePanel, length, width, x, y);
        drawArmWest(mazePanel, length, width, x, y);
	}

    /**
     * Draw the background, which is visualized as a filled white circle
     * at the current center x,y position and for the current size.
     * @param mazePanel The graphics object to draw on
     */
	private void drawBackground(final MazePanel mazePanel) {
		// color setting hard coded as white
		mazePanel.setColor(Color.WHITE);
		// determine x,y coordinates for oval
		final int x = centerX - size;
		final int y = centerY - size;
		// determine width and height of oval
		// for a circle both are same, so one variable suffices
		final int w = 2 * size;// - 2 * CIRCLE_BORDER;
        mazePanel.addFilledOval(x,y,w,w);
	}

	/**
	 * Draw an arm in west direction.
	 * @param mazePanel The graphics object to draw on
	 * @param length The length of the arm
	 * @param width The width of the arm
	 * @param x For the x coordinates of the triangle points, first entry given and fixed
	 * @param y For the y coordinates of the triangle points, first entry given and fixed
	 */
	private void drawArmWest(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// x[0] and y[0] are already set to the coordinates of the center point
		// set coordinates for 2nd and 3rd point and draw filled triangle
		x[1] = centerX - length;
        y[1] = centerY;
        x[2] = centerX - width;
        y[2] = centerY + width;
        mazePanel.addFilledPolygon(x, y, 3);
        // adjust coordinate for 2nd point and draw 2nd triangle
        y[2] = centerY - width;
        mazePanel.addPolygon(x, y, 3);
	}
	/**
	 * Draw an arm in east direction.
	 * @param mazePanel The graphics object to draw on
	 * @param length The length of the arm
	 * @param width The width of the arm
	 * @param x For the x coordinates of the triangle points, first entry given and fixed
	 * @param y For the y coordinates of the triangle points, first entry given and fixed
	 */
	private void drawArmEast(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// observation: the 2 triangles to the right are drawn the same
		// way as for the left if one inverts the sign for length and width
		// i.e., exchanges addition and subtraction
		drawArmWest(mazePanel, -length, -width, x, y);
	}
	/**
	 * Draw an arm in south direction.
	 * @param mazePanel The graphics object to draw on
	 * @param length The length of the arm
	 * @param width The width of the arm
	 * @param x For the x coordinates of the triangle points, first entry given and fixed
	 * @param y For the y coordinates of the triangle points, first entry given and fixed
	 */
	private void drawArmSouth(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// x[0] and y[0] are already set to the coordinates of the center point
		// set coordinates for 2nd and 3rd point and draw filled triangle
		x[1] = centerX;
        y[1] = centerY + length;
        x[2] = centerX + width;
        y[2] = centerY + width;
        mazePanel.addFilledPolygon(x, y, 3);
        // adjust coordinate for 2nd point and draw 2nd triangle
        x[2] = centerX - width;
        mazePanel.addPolygon(x, y, 3);
	}
	/**
	 * Draw an arm in north direction.
	 * @param mazePanel The graphics object to draw on
	 * @param length The length of the arm
	 * @param width The width of the arm
	 * @param x For the x coordinates of the triangle points, first entry given and fixed
	 * @param y For the y coordinates of the triangle points, first entry given and fixed
	 */
	private void drawArmNorth(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// observation: the 2 triangles to the top are drawn the same
		// way as for the bottom if one inverts the sign for length and width
		// i.e., exchanges addition and subtraction
		drawArmSouth(mazePanel, -length, -width, x, y);
	}

	/**
	 * Draws a circle surrounding the compass rose in two colors,
	 * partly shaded, partly highlighted.
	 * @param mazePanel The graphics object to draw on
	 * @param width
	 */
	private void drawBorderCircle(MazePanel mazePanel, int width) {
		// determine x,y coordinates for arc
		final int x = centerX - width / 2 + CIRCLE_BORDER;
		final int y = centerY - width / 2 + CIRCLE_BORDER;
		// determine width and height for arc, 
		// symmetric so both are same
		// only one variable is needed
		final int w = width - 2 * CIRCLE_BORDER;
		// draw both arcs
		mazePanel.setColor(Color.WHITE);
        mazePanel.addArc(x, y, w, w, 45, 180);
        mazePanel.setColor(Color.WHITE);
        mazePanel.addArc(x, y, w, w, 180 + 45, 180);
	}

	/**
	 * Draw the letters N, E, S, W for directions on the compass rose.
	 * N is always towards the top. The current direction is highlighted
	 * by using a different color for that letter.
	 * @param mazePanel The graphics object to draw on
	 * @param width Used to calculate the offset from the center for each letter
	 */
	private void drawDirectionMarker(MazePanel mazePanel, int width) {
		// catch special cases where drawing is not possible
		if (Double.isNaN(markerRadius) || mazePanel.getFont() == null)
			return;
		
		// determine offset from center for position of each string
		// the overall arrangement is symmetric so each letter is 
		// the same distance away from the center of the compass rose
		int offset = (int) (width * markerRadius / 2);

		/* version with color highlighting but stable orientation 
		 * so North is always on top, highlight the current direction.
		 * Highlighting with MarkerColor
		 * use gold as color for others 
		 */
		// WARNING: north south confusion
		// currendDir South is going upward on the map
		mazePanel.setColor((CardinalDirection.South == currentDir) ?
				Color.GREEN :
					Color.YELLOW);
		drawMarker(mazePanel, centerX, centerY - offset, "N");

		mazePanel.setColor((CardinalDirection.East == currentDir) ?
				Color.GREEN :
				Color.YELLOW);
		drawMarker(mazePanel, centerX + offset, centerY, "E");

		// WARNING: north south confusion
		// currendDir North is going downwards on the map
		mazePanel.setColor((CardinalDirection.North == currentDir) ?
				Color.GREEN :
				Color.YELLOW);
		drawMarker(mazePanel, centerX, centerY + offset, "S");

		mazePanel.setColor((CardinalDirection.West == currentDir) ?
				Color.GREEN :
				Color.YELLOW);
		drawMarker(mazePanel, centerX - offset, centerY, "W");

	}
 
	/**
	 * Draws the given string at the given coordinates.
	 * It uses the field markerFont to determine the font.
	 * @param mazePanel The graphics object to draw on
	 * @param x The x coordinate where to draw
	 * @param y The y coordinate where to draw
	 * @param str The string to draw
	 */
    private void drawMarker(MazePanel mazePanel, float x, float y, String str) {
        mazePanel.addMarker(x,y,str);
        
    }

	public void setMarkerFont(MazePanel mazePanel, String markerFont) {
		this.markerFont = markerFont;
		mazePanel.setFont(markerFont);
		mazePanel.update();
	}
}
