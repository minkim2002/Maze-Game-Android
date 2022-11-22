package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;



public class MazePanelAnimating extends View{
    private Bitmap bitmap;
    private Canvas mazeCanvas;
    private Paint mazePaint;

    public MazePanelAnimating(Context context, AttributeSet attrs) {
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

        mazePaint.setColor(Color.GREEN);
        int[] gX = new int[]{200, 400, 400, 200};
        int[] gY = new int[]{600, 100, 300, 700};
        Path gPath = new Path();
        gPath.moveTo(gX[0], gY[0]);
        for(int i = 1; i<4; i++){
            gPath.lineTo(gX[i], gY[i]);
        }
        gPath.close();
        mazeCanvas.drawPath(gPath, mazePaint);

        mazePaint.setColor(Color.YELLOW);
        int[] yX = new int[]{600, 800, 800, 600};
        int[] yY = new int[]{100, 600, 700, 300};
        Path yPath = new Path();
        yPath.moveTo(yX[0], yY[0]);
        for(int i = 1; i<4; i++){
            yPath.lineTo(yX[i], yY[i]);
        }
        yPath.close();
        mazeCanvas.drawPath(yPath, mazePaint);
    }
}
