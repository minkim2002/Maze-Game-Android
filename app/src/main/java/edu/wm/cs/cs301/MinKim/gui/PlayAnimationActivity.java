package edu.wm.cs.cs301.MinKim.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;

import java.util.Objects;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: PlayAnimationActivity
 * Responsibilities: animate the playing the maze game with the selected driver and robot
 * Collaborators: WinningActivity, LosingActivity, AMazeActivity
 */
public class PlayAnimationActivity extends PlayActivity {

    private boolean auto = true;
    private int autoSpeed = 1;

    private final float InitialEnergyLevel = 3500;
    private float consumptionEnergyLevel = 3500;

    /**
     * Set up the path length view and other UI components
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_playing);
        setComponents();
        setPathLength();
        //Get the intent to check whether the information is sent
        Intent mazeGame = getIntent();
        Log.v("Maze", Objects.requireNonNull(mazeGame.getStringExtra("Maze")));
        Log.v("Driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        Log.v("Robot", Objects.requireNonNull(mazeGame.getStringExtra("Robot")));

    }

    /**
     * Set up the user interface components
     */
    private void setComponents() {
        setMenu(this);
        setZoom();
        setAnimation();
        //winning button
        setFinalButton(true, (Button) findViewById(R.id.winningButton));
        //losing button
        setFinalButton(false, (Button) findViewById(R.id.losingButton));
    }

    /**
     * Set up the start or stop animation button
     */
    private void setAnimation() {
        //Listener for the slider of animation speed
        Slider animationSlider = findViewById(R.id.animationSlider);
        animationSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}
            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                autoSpeed = (int) slider.getValue();
                Log.v("Animation speed", ""+autoSpeed);
            }
        });
        //Create a listen for the animation button
        final Button animation = findViewById(R.id.animationButton);
        animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start or stop
                auto = !auto;
                Log.v("Animation", ""+auto);
                //change the text, text color, and background color
                animation.setText(auto ? R.string.stopAutoText : R.string.startAutoText);
                animation.setTextColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorSecondary));
                animation.setBackgroundColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorSecondary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary));
            }
        });
    }

    /**
     * Set up the final button
     * @param result whether the driver solved the maze or not
     * @param finalButton the object of the final button
     */
    private void setFinalButton(final boolean result, Button finalButton) {
        final Intent finalState = result ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalState.putExtra("Manual", false);
                finalState.putExtra("Path Length", pathLength);
                finalState.putExtra("Shortest Path", shortestPath);
                finalState.putExtra("Energy Consumption", consumptionEnergyLevel);
                Log.v("Animation", result ? "Proceeding to WinningActivity" : "Proceeding to LosingActivity");
                Toast toast = Toast.makeText(PlayAnimationActivity.this,
                        result ? "Maze Solved" : "Maze Unsolved", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(finalState);
                finish();
            }
        });
    }
}
