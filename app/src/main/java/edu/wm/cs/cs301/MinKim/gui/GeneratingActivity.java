package edu.wm.cs.cs301.MinKim.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: GeneratingActivity
 * Responsibilities: Screen during the maze generating process, Selecting a driver
 * and robot, Sending information back to AMazeActivity.
 * Collaborators: AMazeActivity
 */

public class GeneratingActivity extends AppCompatActivity implements Runnable {

    protected Thread generating;
    private Handler handler;

    /**
     * Start the maze generation process thread,
     * make the driver and robot spinners with their corresponding options,
     * set up listeners for the driver spinner (for robot selecting process) and play button
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating);

        // start the thread for generation
        handler = new Handler(Looper.getMainLooper());
        generating = new Thread(this);
        generating.start();

        // Robot spinner with robot options
        Spinner robot = findViewById(R.id.robotSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.robot, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robot.setAdapter(adapter);

        // Driver spinner with driver options
        Spinner driver = findViewById(R.id.driverSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.driver, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driver.setAdapter(adapter);

        // Listener for the driver spinner for different robot layout
        driver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout robot = findViewById(R.id.robotLayout);
                if(!parent.getItemAtPosition(position).toString().equals("Wall Follower") &&
                        !parent.getItemAtPosition(position).toString().equals("Wizard"))
                    robot.setVisibility(View.INVISIBLE);
                else robot.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                LinearLayout robot = findViewById(R.id.robotLayout);
                if(!parent.getSelectedItem().toString().equals("Wall Follower") &&
                        !parent.getSelectedItem().toString().equals("Wizard"))
                    robot.setVisibility(View.INVISIBLE);
                else robot.setVisibility(View.VISIBLE);
            }
        });

        //listener for the play button
        Button play = findViewById(R.id.playButton);
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //robot information
                Spinner robot = findViewById(R.id.robotSpinner);
                String robotString = robot.getSelectedItem().toString().split(" ")[0];

                //driver information
                Spinner driver = findViewById(R.id.driverSpinner);
                String driverString = driver.getSelectedItem().toString();

                //Intent with robot and driver information
                Intent result = new Intent(GeneratingActivity.this, AMazeActivity.class);
                result.putExtra("Driver", driverString);
                result.putExtra("Robot", robotString);

                // send the result back to the AMazeActivity
                GeneratingActivity.this.setResult(RESULT_OK, result);
                GeneratingActivity.this.finish();
            }
        });
    }

    /**
     * Thread process during the maze generation
     */
    @Override
    public void run() {
        //Get the intent sent from the AMazeActivity
        Intent intent = getIntent();
        Log.v("Difficulty Level", ""+intent.getIntExtra("Difficulty Level", 0));
        Log.v("Builder", ""+intent.getStringExtra("Builder"));
        Log.v("Room", ""+intent.getBooleanExtra("Room", true));

        // Progress
        int progress = 0;
        View progressBar = findViewById(R.id.progressBar);
        while (progress < 100) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, null);
                finish();
                return;
            }
            progress += 10;
            progressBar.getBackground().setLevel(progress);
        }

        //once the progress is done, play button becomes visible
        handler.post(new Runnable() {
            public void run() {
                Button play = findViewById(R.id.playButton);
                play.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * If the player presses the back button, the generating thread is interrupted and the title
     * activity is notified.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // stop the thread
        generating.interrupt();
        Log.v("Generation", "generation process interrupted");
        GeneratingActivity.this.setResult(RESULT_CANCELED, new Intent(GeneratingActivity.this, AMazeActivity.class));
        GeneratingActivity.this.finish();
    }
}
