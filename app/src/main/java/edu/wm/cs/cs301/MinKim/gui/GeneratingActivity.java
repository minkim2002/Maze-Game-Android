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

public class GeneratingActivity extends AppCompatActivity implements Runnable {

    protected Thread generatingThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating);

        handler = new Handler(Looper.getMainLooper());
        generatingThread = new Thread(this);
        generatingThread.start();

        Spinner robot = findViewById(R.id.robotSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.robot, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robot.setAdapter(adapter);

        Spinner driver = findViewById(R.id.driverSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.driver, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driver.setAdapter(adapter);

        driver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout robotLayout = findViewById(R.id.robotLayout);
                if(!parent.getItemAtPosition(position).toString().equals("Wallfollower") &&
                        !parent.getItemAtPosition(position).toString().equals("SmarterWallfollower") ) robotLayout.setVisibility(View.INVISIBLE);
                else robotLayout.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                LinearLayout robotLayout = findViewById(R.id.robotLayout);
                if(!parent.getSelectedItem().toString().equals("Wallfollower") &&
                        !parent.getSelectedItem().toString().equals("SmarterWallfollower") ) robotLayout.setVisibility(View.INVISIBLE);
                else robotLayout.setVisibility(View.VISIBLE);
            }
        });

        Button play = findViewById(R.id.playButton);
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Spinner driver = findViewById(R.id.driverSpinner);
                String driverString = driver.getSelectedItem().toString();

                Spinner robot = findViewById(R.id.robotSpinner);
                String robotString = robot.getSelectedItem().toString().split(" ")[0];

                Intent result = new Intent(GeneratingActivity.this, AMazeActivity.class);
                result.putExtra("Driver", driverString);
                result.putExtra("Robot", robotString);

                GeneratingActivity.this.setResult(RESULT_OK, result);
                GeneratingActivity.this.finish();
            }
        });
    }

    @Override
    public void run() {
        Intent intent = getIntent();
        Log.v("SkillLevel", ""+intent.getIntExtra("SkillLevel", 0));
        Log.v("Builder", ""+intent.getStringExtra("Builder"));
        Log.v("Room", ""+intent.getBooleanExtra("Room", false));

        int currentProgress = 0;
        while (currentProgress < 100) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, null);
                finish();
                return;
            }
            currentProgress += 10;
        }

        handler.post(new Runnable() {
            public void run() {
                Button playButton = findViewById(R.id.playButton);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        generatingThread.interrupt();
        Log.v("Generation", "Cancelling generation process");
        GeneratingActivity.this.setResult(RESULT_CANCELED, new Intent(GeneratingActivity.this, AMazeActivity.class));
        GeneratingActivity.this.finish();
    }
}
