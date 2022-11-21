package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import edu.wm.cs.cs301.MinKim.R;

public class FinalActivity extends AppCompatActivity {

    protected void setUpComponents(final Context context, final boolean winning) {
        Intent finalState = getIntent();
        Resources res = getResources();

        // For auto mode, Energy consumption into the text view
        if (!finalState.getBooleanExtra("Manual", true)) {
            String totalEnergyConsumptionString = res.getString(R.string.totalEnergyConsumptionText);
            TextView energyConsumption = findViewById(R.id.energyConsumptionText);
            energyConsumption.setVisibility(View.VISIBLE);
            String energyConsumptionString = String.format(Locale.US, "%.1f", finalState.getFloatExtra("Energy Consumption", 0));
            energyConsumption.setText(String.format(totalEnergyConsumptionString, energyConsumptionString));
        }

        // Path length into the text view
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLength = findViewById(R.id.pathLengthText);
        pathLength.setText(String.format(pathLengthString, finalState.getIntExtra("Path Length", 0)));

        // Shortest path into the text view
        String shortestPathString = res.getString(R.string.shortestPathText);
        TextView shortestPath = findViewById(R.id.shortestPathText);
        shortestPath.setText(String.format(shortestPathString, finalState.getIntExtra("Shortest Path", 0)));

        // create a listener for the button to navigate back to the AMazeActivity
        Button newMaze = findViewById(R.id.newMazeButton);
        newMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(winning ? "Winning" : "Losing", "Returning to the Main Page");
                Intent toTitle = new Intent(context, AMazeActivity.class);
                startActivity(toTitle);
                finish();
            }
        });
    }
}
