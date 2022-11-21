package edu.wm.cs.cs301.MinKim.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: PlayManuallyActivity
 * Responsibilities: Play through the maze without the robot and driver
 * Collaborators: WinningActivity, LosingActivity, AMazeActivity
 */
public class PlayManuallyActivity extends PlayActivity {

    /**
     * Set up the path length view and other UI components
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manually_playing);
        //set up process
        setButtons();
        setPathLength();
    }

    /**
     * Set up the buttons for movement, menu, and zoom
     */
    private void setButtons() {
        //set up buttons
        setMove();
        setMenu(this);
        setZoom();
        //shortcut button (navigate to winning)
        Button shortcut = findViewById(R.id.shortcutButton);
        shortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent for winning, passing extra information needed
                Intent winningState = new Intent(PlayManuallyActivity.this, WinningActivity.class);
                winningState.putExtra("Manual", true);
                winningState.putExtra("Path Length", pathLength);
                winningState.putExtra("Shortest Path", shortestPath);
                Log.v("Manual Activity", "Proceed to WinningActivity");
                Toast toast = Toast.makeText(PlayManuallyActivity.this, "Solved the maze", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(winningState);
                finish();
            }
        });
    }

    /**
     * Set up listeners for the movement buttons
     */
    private void setMove() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(v -> {
            pathLength += 1;
            setPathLength();
        });
        ImageView leftButton = findViewById(R.id.leftButton);
        leftButton.setOnClickListener(v -> {
            pathLength += 1;
            setPathLength();
        });
        ImageView rightButton = findViewById(R.id.rightButton);
        rightButton.setOnClickListener(v -> {
            pathLength += 1;
            setPathLength();
        });
        ImageView backButton = findViewById(R.id.backwardButton);
        backButton.setOnClickListener(v -> {
            pathLength += 1;
            setPathLength();
        });
        ImageView jumpButton = findViewById(R.id.jumpButton);
        jumpButton.setOnClickListener(v -> {
            pathLength += 1;
            setPathLength();
        });
    }

}
