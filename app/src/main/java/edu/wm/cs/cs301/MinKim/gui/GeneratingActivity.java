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
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.MinKim.R;
import edu.wm.cs.cs301.MinKim.generation.Factory;
import edu.wm.cs.cs301.MinKim.generation.Maze;
import edu.wm.cs.cs301.MinKim.generation.MazeFactory;
import edu.wm.cs.cs301.MinKim.generation.MazeSingleton;
import edu.wm.cs.cs301.MinKim.generation.Order;

/**
 * @author Min Kim
 * Class: GeneratingActivity
 * Responsibilities: Screen during the maze generating process, Selecting a driver
 * and robot, Sending information back to AMazeActivity.
 * Collaborators: AMazeActivity
 */

public class GeneratingActivity extends AppCompatActivity implements Runnable, Order {

    protected Thread generating;
    private Handler handler;

    private Factory factory;
    private Order.Builder builder;

    private int skillLevel;
    private int seed;
    private int progress;

    private boolean gameStart;
    private boolean isPerfect;

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
        defaultState();

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
        skillLevel = intent.getIntExtra("Difficulty Level", 0);
        Log.v("SkillLevel", ""+ skillLevel);
        isPerfect = !intent.getBooleanExtra("Room", false);
        Log.v("Room", ""+ !isPerfect);
        if(intent.getStringExtra("Builder").equals("DFS")){
            builder = Order.Builder.DFS;
        }
        if(intent.getStringExtra("Builder").equals("Prim")){
            builder = Order.Builder.Prim;
        }
        if(intent.getStringExtra("Builder").equals("Boruvka")){
            builder = Order.Builder.Prim;
        }
        Log.v("Builder", ""+ builder);
        factory.order((Order) this);
        factory.waitTillDelivered();

        handler.post(new Runnable() {
            public void run() {
                assert(MazeSingleton.getMazeSingleton().getMaze()!=null):
                        "Maze Generation failed";
                Log.v("Generation", "Maze ready");
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

    private void defaultState(){
        factory = new MazeFactory();
        skillLevel = 0;
        builder = Order.Builder.DFS;
        progress = 0;
        seed = 2;
        isPerfect = false;
        gameStart = false;

    }

    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return isPerfect;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        MazeSingleton.getMazeSingleton().setMaze(mazeConfig);
    }

    @Override
    public void updateProgress(int percentage) {
        if (percentage <= 100 && progress < percentage) {
            progress = percentage;
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(progress);
            progressBar.setMax(100);
        }
    }

}
