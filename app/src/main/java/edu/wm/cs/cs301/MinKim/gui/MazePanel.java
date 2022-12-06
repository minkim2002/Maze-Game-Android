package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class MazePanel extends View implements P7PanelF22 {
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;

    private int color;
    private Typeface font;

    private int width;
    private int height;

    public MazePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        canvas = null;
        paint = null;
        bitmap = null;
        update();
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        update();
    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        if (null != getCanvas()) {
            return true;
        }
        return false;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * Substitute for Graphics.setColor method.
     * @param argb gives the alpha, red, green, and blue encoded value of the color
     */
    @Override
    public void setColor(int argb) {
        color = argb;
        paint.setColor(argb);
    }

    /**
     * Returns the ARGB value for the current color setting.
     * @return integer ARGB value
     */
    @Override
    public int getColor() {
        return color;
    }

    /**
     * Set the font
     * @param fontString name
     */
    public void setFont(String fontString) {
        font = Typeface.create(fontString, Typeface.NORMAL);
        paint.setTypeface(Typeface.create(fontString, Typeface.NORMAL));
    }

    /**
     * Get the font
     */
    public Typeface getFont() {
        return font;
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        setColor(Color.BLACK);
        addFilledRectangle(0, 500, 1000, 1000);
        setColor(Color.GRAY);
        addFilledRectangle(0, 0, 1000, 500);
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        canvas.drawRect(x, y, x+width, y+height, paint);
    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        addPolygon(xPoints, yPoints, nPoints);
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Path polygonLine = new Path();
        polygonLine.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            polygonLine.lineTo(xPoints[i], yPoints[i]);
        }
        polygonLine.close();
        canvas.drawPath(polygonLine, paint);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Graphics.fillOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        canvas.drawOval(x, y, x+width, y+height, paint);
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * Substitute for Graphics.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        canvas.drawArc(x, y, x+width, y+height, startAngle, arcAngle, true, paint);
    }

    /**
     * Adds a string at the given position.
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        canvas.drawText(str, x, y, paint);
    }

    /**
     * Sets the value of a single preference for the rendering algorithms.
     * It internally maps given parameter values into corresponding java.awt.RenderingHints
     * and assigns that to the internal graphics object.
     * Hint categories include controls for rendering quality
     * and overall time/quality trade-off in the rendering process.
     *
     * Refer to the awt RenderingHints class for definitions of some common keys and values.
     *
     * Note for Android: start with an empty default implementation.
     * Postpone any implementation efforts till the Android default rendering
     * results in unsatisfactory image quality.
     *
     * @param hintKey the key of the hint to be set.
     * @param hintValue the value indicating preferences for the specified hint category.
     */
    @Override
    public void setRenderingHint(P7RenderingHints hintKey, P7RenderingHints hintValue) {

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v("Drawing", "Drawing");
        addBackground(0);
        myTestImage(canvas);
    }

    /**
     * @param c the canvas object
     */
    private void myTestImage(Canvas c) {
        setColor(Color.RED);
        addFilledOval(0, 0, 100, 100);
        setColor(Color.GREEN);
        addFilledOval(100, 0, 100, 100);
        setColor(Color.YELLOW);
        addFilledRectangle(0, 100, 100, 100);
        setColor(Color.BLUE);
        int[] xPoints = {100, 100, 150, 150, 200, 100};
        int[] yPoints = {100, 150, 200, 200, 150, 200};
        addFilledPolygon(xPoints, yPoints, 6);
        addLine(0, 200, 100, 300);
        update(c);
    }

    /**
     * Get a graphics object that could be used for drawing.
     */
    public Canvas getCanvas() {
        if (null == canvas) {
            if (null == bitmap) {
                bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                if (null == bitmap) {
                    Log.e("Error", "failed to create bitmap");
                    return null;
                }
            }
            canvas = new Canvas(bitmap);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return canvas;
    }

    /**
     * Draws the image to a given graphics object when the maze panel
     * needs to redraw
     */
    public void paint(Canvas canvas) {
        if (null == canvas) {
            Log.e("paint", "No canvas object to draw on");
        }
        else {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    /**
     * Draws the image on a canvas object
     */
    public void update(Canvas canvas) {
        paint(canvas);
    }

    /**
     * Draws the image on a graphics object
     */
    public void update() {
        paint(getCanvas());
    }

    /**
     * Set the first person dimensions for the FirstPersonView
     */
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

}



