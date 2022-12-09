package edu.wm.cs.cs301.MinKim.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;

import java.util.Objects;

import edu.wm.cs.cs301.MinKim.R;
import edu.wm.cs.cs301.MinKim.generation.Maze;
import edu.wm.cs.cs301.MinKim.generation.MazeSingleton;

/**
 * @author Min Kim
 * Class: PlayAnimationActivity
 * Responsibilities: animate the playing the maze game with the selected driver and robot
 * Collaborators: WinningActivity, LosingActivity, AMazeActivity
 */
public class PlayAnimationActivity extends PlayActivity {
    private boolean auto = true;
    private double autoSpeed = 1000;
    private float startingEnergyLevel;
    final private int MEAN_TIME_BETWEEN_FAILURES=4000;
    final private int MEAN_TIME_TO_REPAIR=2000;

    private Robot robot;
    private Runnable animationThread;
    private Handler animation;

    /**
     * Set up the path length view and other UI components
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_playing);
        //Get the intent to check whether the information is sent
        Intent play = getIntent();
        Log.v("Maze", Objects.requireNonNull(play.getStringExtra("Maze")));
        String driver = play.getStringExtra("Driver");
        Log.v("Driver", Objects.requireNonNull(play.getStringExtra("Driver")));
        String robot = play.getStringExtra("Robot");
        Log.v("Robot", Objects.requireNonNull(play.getStringExtra("Robot")));

        statePlaying = new StatePlaying();
        statePlaying.setMaze(MazeSingleton.getMazeSingleton().getMaze());
        setUpDriverAndRobot(statePlaying, MazeSingleton.getMazeSingleton().getMaze(), driver, robot);
        statePlaying.start(this, findViewById(R.id.mazePanel));
        animation = new Handler(Looper.getMainLooper());

        setComponents();
        setAnimation();
        setPathLength(this.driver.getPathLength());
    }

    /**
     * Set up the user interface components
     */
    private void setComponents() {
        setMenu(this);
        setZoom();
        setAnimationScreen();
    }

    /**
     * Set up the start or stop animation button
     */
    private void setAnimationScreen() {
        //Listener for the slider of animation speed
        Slider animationSlider = findViewById(R.id.animationSlider);
        animationSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(Slider slider) {}
            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(Slider slider) {
                double speed = ((int)slider.getValue() * -0.5 + 1) * 1000;
                if (autoSpeed != speed) {
                    autoSpeed = speed;
                    Log.v("Animation Speed", "" + autoSpeed);
                    animation.removeCallbacks(animationThread);
                    if (auto){
                        animation.postDelayed(animationThread, (long) autoSpeed);
                    }
                }
            }
        });
        //Create a listener for the stop animation button
        final Button animationStop = findViewById(R.id.animationButton);
        animationStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start or stop
                auto = !auto;
                if (auto){
                    animation.postDelayed(animationThread, (long) autoSpeed);
                }
                else {
                    animation.removeCallbacks(animationThread);
                }
                //change the text, text color, and background color
                animationStop.setText(auto ? R.string.stopAutoText : R.string.startAutoText);
                animationStop.setTextColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorSecondary));
                animationStop.setBackgroundColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational));
            }
        });
    }

    public void setAnimation() {
        animationThread = () -> {
            try {
                driver.drive1Step2Exit();
                updateEnergyProgress(driver.getEnergyConsumption());
                setPathLength(driver.getPathLength());
                for (Robot.Direction dir: Robot.Direction.values()){
                    updateSensor(dir, isOperational(dir));
                }
                if (robot.isAtExit()) {
                    ((Wizard)driver).exit2End(robot.getCurrentPosition());
                    switchToEndScreen(true);
                } else {
                    animation.postDelayed(animationThread, (long) autoSpeed);
                }
            } catch (Exception e) {
                Log.w("Driver Error", e.toString());
                switchToEndScreen(false);
            }
        };
        animation.postDelayed(animationThread, (long) autoSpeed);
    }

    /**
     * Set up the final button
     * @param result whether the driver solved the maze or not
     */
    private void switchToEndScreen(final boolean result) {
        final Intent endScreen = result ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        endScreen.putExtra("Manual Mode", false);
        endScreen.putExtra("Energy Consumption", driver.getEnergyConsumption());
        endScreen.putExtra("Path Length", driver.getPathLength());
        endScreen.putExtra("Shortest Path", shortestPath);
        Log.v("Game Ended", result ? "Proceed to WinningActivity" : "Proceed to LosingActivity");
        Toast toast = Toast.makeText(PlayAnimationActivity.this,
                result ? "Maze Solved" : "Maze Unsolved", Toast.LENGTH_SHORT);
        toast.show();
        startActivity(endScreen);
        finish();
    }

    private void setUpDriverAndRobot(StatePlaying statePlaying, Maze maze, String driver, String robot) {
        if (driver.equals("Wizard")) {
            this.driver = new Wizard();
            this.robot = new ReliableRobot();
        } else {
            this.driver = new WallFollower();
            if (robot.equals("Premium")) {
                this.robot = new UnreliableRobot(1, 1, 1, 1);
            }
            if (robot.equals("Mediocre")) {
                this.robot = new UnreliableRobot(1, 1, 1, 1);
            }
            if (robot.equals("SoSo")) {
                this.robot = new UnreliableRobot(0, 1, 0, 1);
            }
            if (robot.equals("Shaky")) {
                this.robot = new UnreliableRobot(0, 0, 0, 0);
            }
            for (Robot.Direction direction: Robot.Direction.values())
                try {
                    this.robot.startFailureAndRepairProcess(direction, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
                    Thread.sleep(1300);
                } catch (Exception e) {
                    Log.v("Driver Setup", "Reliable Sensor, moving on...");
                }
        }
        this.driver.setMaze(maze);
        this.driver.setRobot(this.robot);
        this.robot.setController(statePlaying);
        startingEnergyLevel = this.robot.getBatteryLevel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animation.removeCallbacks(animationThread);
        Log.v("Animation Activity", "Game Ended");
        finish();
    }

    private void updateEnergyProgress(float energyConsumption) {
        ProgressBar energyBar = findViewById(R.id.energyBar);
        energyBar.setProgress((int) (((startingEnergyLevel-energyConsumption)/startingEnergyLevel) * 100));
    }

    protected boolean isOperational(Robot.Direction direction) {
        try {
            // the sensor is operational if no exception is thrown
            robot.distanceToObstacle(direction);
            float defaultEnergyLevel = robot.getBatteryLevel();
            robot.setBatteryLevel(defaultEnergyLevel);
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    private void updateSensor(Robot.Direction sensor, boolean isOperational) {
        switch(sensor) {
            case FORWARD:
                View forwardSensor = findViewById(R.id.forwardSensor);
                forwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case LEFT:
                View leftSensor = findViewById(R.id.leftSensor);
                leftSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case RIGHT:
                View rightSensor = findViewById(R.id.rightSensor);
                rightSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case BACKWARD:
                View backwardSensor = findViewById(R.id.backwardSensor);
                backwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
        }
    }

}
