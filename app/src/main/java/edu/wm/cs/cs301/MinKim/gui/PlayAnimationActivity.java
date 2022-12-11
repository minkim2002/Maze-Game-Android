package edu.wm.cs.cs301.MinKim.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
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
    protected boolean showMap = true;
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
        playSong = MediaPlayer.create(PlayAnimationActivity.this, R.raw.title_music);
        playSong.start();
        if(!playSong.isPlaying()){
            playSong.reset();
            playSong.start();
        }
        //Get the intent to check whether the information is sent
        Bundle play = getIntent().getExtras();
        Maze maze = MazeSingleton.getMazeSingleton().getMaze();
        Log.v("Maze", Objects.requireNonNull(play.getString("Maze")));
        String driver = play.getString("Driver");
        Log.v("Driver", Objects.requireNonNull(play.getString("Driver")));
        String robot = play.getString("Robot");
        Log.v("Robot", Objects.requireNonNull(play.getString("Robot")));

        statePlaying = new StatePlaying();
        animation = new Handler(Looper.getMainLooper());
        statePlaying.setMaze(MazeSingleton.getMazeSingleton().getMaze());
        setUpDriverAndRobot(statePlaying, MazeSingleton.getMazeSingleton().getMaze(), driver, robot);
        statePlaying.start(this, findViewById(R.id.mazePanel));

        setComponents();
        setAnimation();

        setPathLength(this.driver.getPathLength());
        getShortestPath(maze);
    }

    /**
     * Set up the user interface components
     */
    private void setComponents() {
        setMenu(this);
        setZoom();
        setAnimationScreen();
        showMap = true;
        statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 0);
    }

    /**
     * Set up the start or stop animation button, set up the animation speed bar
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
                double speed = ((int)slider.getValue() * -0.2 + 1) * 1000;
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

    /**
     * Configure the animation thread and pass it on to the handler
     */
    public void setAnimation() {
        animationThread = () -> {
            try {
                driver.drive1Step2Exit();
                energyProgress(driver.getEnergyConsumption());
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
     * Link the robot energy consumption with energy consumption progress bar
     * @param energyConsumption the current energy level of the robot
     */
    private void energyProgress(float energyConsumption) {
        ProgressBar energyBar = findViewById(R.id.energyBar);
        energyBar.setProgress((int) (((startingEnergyLevel-energyConsumption)/startingEnergyLevel) * 100));
    }

    /**
     * Set up the final button
     * @param result whether the driver solved the maze or not
     */
    private void switchToEndScreen(final boolean result) {
        final Intent endScreen = result ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        endScreen.putExtra("Manual", false);
        endScreen.putExtra("Energy Consumption", driver.getEnergyConsumption());
        endScreen.putExtra("Path Length", driver.getPathLength());
        endScreen.putExtra("Shortest Path", shortestPath);
        Log.v("Game Ended", result ? "Proceed to WinningActivity" : "Proceed to LosingActivity");
        Toast toast = Toast.makeText(PlayAnimationActivity.this,
                result ? "Maze Solved" : "Maze Unsolved", Toast.LENGTH_SHORT);
        toast.show();
        playSong.pause();
        playSong.reset();
        startActivity(endScreen);
        finish();
    }

    /**
     * Set up the driver and robot
     * @param statePlaying statePlaying
     * @param maze the maze the driver will be traversing
     * @param driver type of driver
     * @param robot type of robot
     */
    private void setUpDriverAndRobot(StatePlaying statePlaying, Maze maze, String driver, String robot) {
        if (driver.equals("Wizard")) {
            this.driver = new Wizard();
            this.robot = new ReliableRobot();
        } else {
            this.driver = new WallFollower();
            if (robot.equals("KTX")) {
                this.robot = new UnreliableRobot(1, 1, 1, 1);
            }
            if (robot.equals("Car")) {
                this.robot = new UnreliableRobot(1, 0, 0, 1);
            }
            if (robot.equals("Bicycle")) {
                this.robot = new UnreliableRobot(0, 1, 1, 0);
            }
            if (robot.equals("Legs")) {
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
        this.robot.setController(statePlaying);
        this.driver.setMaze(maze);
        this.driver.setRobot(this.robot);
        startingEnergyLevel = this.robot.getBatteryLevel();
    }

    /**
     * the animation ends if the back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animation.removeCallbacks(animationThread);
        Log.v("Animation Activity", "Game Ended");
        finish();
    }

    /**
     * Determines whether the sensor is operational or not
     * @param direction of the sensor
     */
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

    /**
     * Update the display to indicate whether the robot is operational or under the repair process
     * @param sensor the sensor that is going to be updated
     * @param isOperational status of the sensor
     */
    private void updateSensor(Robot.Direction sensor, boolean isOperational) {
        switch(sensor) {
            case FORWARD:
                View forward = findViewById(R.id.forwardSensor);
                forward.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case LEFT:
                View left = findViewById(R.id.leftSensor);
                left.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case RIGHT:
                View right = findViewById(R.id.rightSensor);
                right.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case BACKWARD:
                View backward = findViewById(R.id.backwardSensor);
                backward.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
        }
    }

}
