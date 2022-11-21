package edu.wm.cs.cs301.MinKim.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
 * choose whether there will be rooms or not, and connect other activities
 * Collaborators: GeneratingActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class AMazeActivity extends AppCompatActivity {

    /**
     * Create the builder spinner with various builder options,
     * set up the navigating buttons which will generate the maze.
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        Log.v("Launching Maze App", "Successful");

        //Builder spinner with various builder options
        Spinner builder = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builder.setAdapter(adapter);
        // Listeners for the buttons that will generate the maze
        setGeneration((Button) findViewById(R.id.revisitButton));
        setGeneration((Button) findViewById(R.id.exploreButton));
    }

    /**
     * Configure the listeners for the buttons that will generate the maze
     * @param button the button the listener resides in
     */
    public void setGeneration(Button button) {
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // receive the selected builder
                Spinner builder = findViewById(R.id.builderSpinner);
                String builderString = builder.getSelectedItem().toString();
                // receive the selected level
                SeekBar difficultyLevel = findViewById(R.id.difficultySeekBar);
                int lv = difficultyLevel.getProgress();
                //receive the information about the presence of rooms
                SwitchMaterial room = findViewById(R.id.roomSwitch);
                boolean isRoom = room.isChecked();

                //Generating intent with received information
                Intent generation = new Intent(AMazeActivity.this, GeneratingActivity.class);
                generation.putExtra("Builder", builderString);
                generation.putExtra("Difficulty Level", lv);
                generation.putExtra("Room", isRoom);

                //Toast as a notification about the new activity
                Toast toast = Toast.makeText(AMazeActivity.this, "Generating maze", Toast.LENGTH_SHORT);
                toast.show();
                startForResult.launch(generation);
            }
        });
    }

    /**
     * After the maze is generated, the generating activity returns the user inputted driver
     * and robot if inputted. Then, the game starts based on the information received
     * @param result returned information from the maze generation
     */
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //check the code
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle maze = result.getData().getExtras();
                assert(maze != null) : "Error: maze configuration is not supposed to be null";

                //Configure the driver and robot bases on the information received
                String driverString = Objects.requireNonNull(maze.get("Driver")).toString();
                String robotString = Objects.requireNonNull(maze.get("Robot")).toString();
                Log.v("Driver Chosen", driverString);
                Log.v("Robot Chosen", robotString);

                //Game intent with given information
                Intent game = new Intent(AMazeActivity.this,
                        (driverString.equals("Manual") ? PlayManuallyActivity.class : PlayAnimationActivity.class));
                game.putExtra("Maze", "");
                game.putExtra("Driver", driverString);
                game.putExtra("Robot", robotString);

                //Toast as a notification about the new activity
                Toast toast = Toast.makeText(AMazeActivity.this, "Loading game", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(game);
            }
            else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.w("Generation", "Canceled");
            }
        }
    });
}
