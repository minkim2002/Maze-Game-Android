package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import edu.wm.cs.cs301.MinKim.R;
import edu.wm.cs.cs301.MinKim.generation.MazeSingleton;

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
        playSong = MediaPlayer.create(PlayManuallyActivity.this, R.raw.title_music);
        playSong.start();
        if(!playSong.isPlaying()){
            playSong.reset();
            playSong.start();
        }
        //set up process
        Intent mazeGame = getIntent();
        Log.v("Game driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        statePlaying = new StatePlaying();
        statePlaying.setMaze(MazeSingleton.getMazeSingleton().getMaze());
        statePlaying.start(this, findViewById(R.id.mazePanel));
        setButtons();
        setPathLength(statePlaying.distTraveled);
    }

    @Override
    public void switchToWinning(Context context, int distance) {
        super.switchToWinning(context, distance);
        // Intent with an information about the game mode, the path length, and the shortest path
        stateWinning.putExtra("Manual", true);
        Log.v("Play Manual End", "Proceed to WinningActivity");
        // Notify the user that the game ended
        Toast toast = Toast.makeText(PlayManuallyActivity.this, "Maze Solved", Toast.LENGTH_SHORT);
        toast.show();
        playSong.pause();
        playSong.reset();
        startActivity(stateWinning);
        finish();
    }

    /**
     * Set up the buttons for movement, menu, and zoom
     */
    private void setButtons() {
        //set up buttons
        setMove();
        setMenu(this);
        setZoom();
    }

    /**
     * Set up listeners for the movement buttons
     */
    private void setMove() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        ImageView leftButton = findViewById(R.id.leftButton);
        ImageView rightButton = findViewById(R.id.rightButton);
        ImageView backButton = findViewById(R.id.backwardButton);
        ImageView jumpButton = findViewById(R.id.jumpButton);
        configureMoveButton(forwardButton, Constants.UserInput.UP);
        configureMoveButton(leftButton, Constants.UserInput.LEFT);
        configureMoveButton(rightButton, Constants.UserInput.RIGHT);
        configureMoveButton(jumpButton, Constants.UserInput.JUMP);
        configureMoveButton(backButton, Constants.UserInput.DOWN);
    }

    private void configureMoveButton(ImageView button, Constants.UserInput move){
        if (move == Constants.UserInput.UP || move == Constants.UserInput.DOWN || move == Constants.UserInput.JUMP) {
            button.setOnClickListener(v -> {
                int currentDist = statePlaying.distTraveled;
                Log.v("Move", move.toString());
                statePlaying.handleUserInput(move, 0);
                // update the path length
                if (statePlaying.distTraveled > currentDist) {
                    setPathLength(statePlaying.distTraveled);
                }
            });
        } else {
            button.setOnClickListener(v -> {
                Log.v("Rotate", move.toString());
                statePlaying.handleUserInput(move, 0);
            });
        }
    }
}
