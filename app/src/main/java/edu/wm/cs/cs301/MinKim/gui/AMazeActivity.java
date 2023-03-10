package edu.wm.cs.cs301.MinKim.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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

import edu.wm.cs.cs301.MinKim.R;
import edu.wm.cs.cs301.MinKim.generation.SingleRandom;

/**
 * @author Min Kim
 * Class: AMazeActivity
 * Responsibilities: Serves as a main screen for the maze game, select a skill level, choose a builder,
 * choose whether there will be rooms or not, and connect other activities
 * Collaborators: GeneratingActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class AMazeActivity extends AppCompatActivity {

    private SharedPreferences mazePreference;
    private SharedPreferences.Editor editor;
    private MediaPlayer titleSong;

    private String builderString;

    private int difficultyLevel;
    private int seed;

    private boolean room;

    /**
     * Create the builder spinner with various builder options,
     * set up the navigating buttons which will generate the maze.
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        mazePreference = getPreferences(Activity.MODE_PRIVATE);
        titleSong = MediaPlayer.create(AMazeActivity.this, R.raw.title_music);
        titleSong.start();
        Log.v("Launching Maze App", "Successful");

        //Builder spinner with various builder options
        Spinner builder = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builder.setAdapter(adapter);
        // Listeners for the buttons that will generate the maze
        setGeneration((Button) findViewById(R.id.revisitButton), "revisit");
        setGeneration((Button) findViewById(R.id.exploreButton), "explore");
    }

    /**
     * Configure the listeners for the buttons that will generate the maze
     * @param button the button the listener resides in
     */
    public void setGeneration(Button button, String function) {
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // receive the selected builder
                Spinner builder = findViewById(R.id.builderSpinner);
                builderString = builder.getSelectedItem().toString();
                // receive the selected level
                SeekBar difficultyLevelBar = findViewById(R.id.difficultySeekBar);
                difficultyLevel = difficultyLevelBar.getProgress();
                //receive the information about the presence of rooms
                SwitchMaterial roomSwitch = findViewById(R.id.roomSwitch);
                room = roomSwitch.isChecked();

                //In case of revisiting, get the information from the last round
                if(function.equals("explore")){
                    seed = SingleRandom.getRandom().nextInt();
                }else{
                    Log.v("yea", "yea");
                    seed = mazePreference.getInt(builderString+difficultyLevel+room, SingleRandom.getRandom().nextInt());
                }

                //Generating intent with received information
                Intent generation = new Intent(AMazeActivity.this, GeneratingActivity.class);
                generation.putExtra("Builder", builderString);
                generation.putExtra("Difficulty Level", difficultyLevel);
                generation.putExtra("Room", room);
                generation.putExtra("Seed", seed);

                //Toast as a notification about the new activity
                Toast toast = Toast.makeText(AMazeActivity.this, "Generating maze", Toast.LENGTH_SHORT);
                toast.show();
                titleSong.pause();
                titleSong.reset();
                startForResult.launch(generation);
            }
        });
    }

    /**
     * Set the settings to default values when this activity is in background
     */
    @Override
    protected void onStop() {
        super.onStop();
        Spinner builderSpinner = findViewById(R.id.builderSpinner);
        builderSpinner.setSelection(0);
        SeekBar skillLevelSeekBar = findViewById(R.id.difficultySeekBar);
        skillLevelSeekBar.setProgress(0);
        SwitchMaterial roomSwitch = findViewById(R.id.roomSwitch);
        roomSwitch.setChecked(false);
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

                //Configure the driver and robot bases on the information received
                String driverString = maze.getString("Driver");
                Log.v("Driver Chosen", driverString);
                String robotString = maze.getString("Robot");
                Log.v("Robot Chosen", robotString);
                int seed = maze.getInt("Seed");
                Log.v("Seed", seed+"");
                SharedPreferences.Editor editor = mazePreference.edit();
                editor.putInt(builderString+difficultyLevel+room, seed);
                editor.apply();

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
