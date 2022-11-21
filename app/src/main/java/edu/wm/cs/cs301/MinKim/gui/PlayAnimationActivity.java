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

public class PlayAnimationActivity extends PlayActivity {

    private boolean auto = true;
    private int autoSpeed = 1;

    private final float InitialEnergyLevel = 3500;
    private float consumptionEnergyLevel = 3500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_playing);

        Intent mazeGame = getIntent();
        setUpComponents();
        setPathLength();
        Log.v("Driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        Log.v("Robot", Objects.requireNonNull(mazeGame.getStringExtra("Robot")));
        Log.v("Maze", Objects.requireNonNull(mazeGame.getStringExtra("Maze")));
    }

    private void setUpComponents() {
        setMenu(this);
        setZoom();
        setAnimationComponents();
        setFinalButton(true, (Button) findViewById(R.id.winningButton));
        setFinalButton(false, (Button) findViewById(R.id.losingButton));
    }

    private void setAnimationComponents() {

        final Button animationButton = findViewById(R.id.animationButton);
        animationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto = !auto;
                animationButton.setBackgroundColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorSecondary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary));
                animationButton.setText(auto ? R.string.stopAutoText : R.string.startAutoText);
                animationButton.setTextColor(auto ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorSecondary));
                Log.v("Animation", ""+auto);
            }
        });

        Slider animationSlider = findViewById(R.id.animationSpeedSlider);
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
    }

    private void setFinalButton(final boolean result, Button endgameButton) {
        final Intent finalState = result ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        endgameButton.setOnClickListener(new View.OnClickListener() {
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
