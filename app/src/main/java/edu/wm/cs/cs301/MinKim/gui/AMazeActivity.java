
package edu.wm.cs.cs301.MinKim.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: AMazeActivity
 * Responsibilities: Serves as a main screen for the maze game, select a skill level, choose a builder,
 * choose whether there will be rooms or not, and connect other screens
 * Collaborators: GeneratingActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class AMazeActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle maze = result.getData().getExtras();

                assert(maze != null) : "Error: maze configuration is not supposed to be null";

                String driverString = Objects.requireNonNull(maze.get("Driver")).toString();
                String robotString = Objects.requireNonNull(maze.get("Robot")).toString();
                Log.v("Driver Chosen", driverString);
                Log.v("Robot Chosen", robotString);

                Intent game = new Intent(AMazeActivity.this,
                        (driverString.equals("Manual") ? PlayManuallyActivity.class : PlayAnimationActivity.class));
                game.putExtra("Maze", "");
                game.putExtra("Driver", driverString);
                game.putExtra("Robot", robotString);

                Toast toast = Toast.makeText(AMazeActivity.this, "Loading game", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(game);
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.w("Generation", "Canceled");
            }
        }
    });

    /**
     * Start the background animation, make the builder
     * spinner with various builder options, set up the navigation buttons
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        Log.v("Launching Maze App", "Passed");

        Spinner builder = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builder.setAdapter(adapter);

        ActivityResultLauncher<Intent> getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                    }
                });

        setUpNavigationButton((Button) findViewById(R.id.revisitButton));
        setUpNavigationButton((Button) findViewById(R.id.exploreButton));
    }

    public void setUpNavigationButton(Button navigationButton) {
        navigationButton.setOnClickListener(view -> {

            Spinner builder = findViewById(R.id.builderSpinner);
            String builderString = builder.getSelectedItem().toString();

            SeekBar skillLevel = findViewById(R.id.seekBar);
            int lv = skillLevel.getProgress();

            SwitchMaterial room = findViewById(R.id.roomSwitch);
            boolean isRoom = room.isChecked();


            Intent generation = new Intent(AMazeActivity.this, GeneratingActivity.class);
            generation.putExtra("Builder", builderString);
            generation.putExtra("SkillLevel", lv);
            generation.putExtra("Room", isRoom);

            // make a new toast to alert the user of the new activity, start the new activity
            // and await the result
            Toast toast = Toast.makeText(AMazeActivity.this, "Generating maze", Toast.LENGTH_SHORT);
            toast.show();
            startForResult.launch(generation);
        });
    }
}
