package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;



public class MazePanel extends View{
    private Bitmap bitmap;
    private Canvas mazeCanvas;
    private Paint mazePaint;

    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mazeCanvas = new Canvas();
        mazePaint = new Paint();
    }

    protected void onDraw(Canvas canvas) {
        mazeCanvas = canvas;
        super.onDraw(mazeCanvas);

        mazePaint.setColor(Color.BLACK);
        mazeCanvas.drawRect(0,500,1000,1000,mazePaint);

        mazePaint.setColor(Color.GRAY);
        mazeCanvas.drawRect(0,0,1000,500,mazePaint);

        mazePaint.setColor(Color.RED);
        mazeCanvas.drawCircle(500,400,250,mazePaint);


    }
}



