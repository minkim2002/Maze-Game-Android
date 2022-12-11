package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import edu.wm.cs.cs301.MinKim.R;
import edu.wm.cs.cs301.MinKim.generation.Maze;

/**
 * @author Min Kim
 * Class: PlayActivity
 * Responsibilities: parent activity of two play activities (Manual, Animation)
 * Collaborators: N/A
 */
public class PlayActivity extends AppCompatActivity {

    protected MediaPlayer playSong;

    protected int shortestPath;

    protected boolean showMap = false;
    protected boolean showCorrectPath = false;
    protected boolean showWalls = false;
    protected int zoom = 100;

    protected StatePlaying statePlaying;
    protected Intent stateWinning;

    protected RobotDriver driver = null;

    /**
     * Intent for final state
     * @param context for the intent
     * @param result whether maze was solved or not
     * @param distance total distance traveled
     */
    public void switchToWinning(Context context, int distance, boolean result) {
        stateWinning = result ? new Intent(context, WinningActivity.class):
            new Intent(context, LosingActivity.class);
        Log.v("Distance Traveled", ""+distance);
        stateWinning.putExtra("Path Length", distance);
        stateWinning.putExtra("Shortest Path", shortestPath);
    }

    /**
     * Set up listener for a menu button
     * @param context activity for popup
     */
    protected void setMenu(final Context context) {
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            // create a menu according to the inputted context
            PopupMenu popup = new PopupMenu(context, v);
            setPopup(context, popup);
            //Inflate it
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.menu, popup.getMenu());
            popup.show();
            //Update the menu
            Menu menu = popup.getMenu();
            menu.findItem(R.id.showMap).setChecked(showMap);
            menu.findItem(R.id.showWalls).setChecked(showWalls);
            menu.findItem(R.id.showCorrectPath).setChecked(showCorrectPath);
        });
    }

    /**
     * Set up listener for popup
     * @param context context in which the popup will be created
     * @param pop PopUpMenu object for listener
     */
    private void setPopup(final Context context, PopupMenu pop) {
        pop.setOnMenuItemClickListener(item -> {
            Toast toast;
            switch (item.getItemId()) {
                case R.id.showMap:
                    showMap = !showMap;
                    item.setChecked(showMap);
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 0);
                    Log.v("Show map", ""+showMap);
                    toast = Toast.makeText(context, "Toggling map", Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                case R.id.showWalls:
                    showWalls = !showWalls;
                    item.setChecked(showWalls);
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLEFULLMAP, 0);
                    Log.v("Show all walls", ""+showWalls);
                    toast = Toast.makeText(context, "Toggling walls", Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                case R.id.showCorrectPath:
                    showCorrectPath = !showCorrectPath;
                    item.setChecked(showCorrectPath);
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLESOLUTION, 0);
                    Log.v("Show solution", ""+showCorrectPath);
                    toast = Toast.makeText(context, "Toggling solution", Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                default:
                    return false;
            }
        });
    }

    /**
     * Update the path length text on screen
     */
    protected void setPathLength(int pathLength) {
        Resources resource = getResources();
        String pathString = resource.getString(R.string.pathLengthText);
        TextView pathText = findViewById(R.id.pathLengthText);
        pathText.setText(String.format(pathString, pathLength));
    }

    /**
     * Set up listeners for zoom buttons
     */
    protected void setZoom() {
        ImageView in = findViewById(R.id.zoomInButton);
        in.setOnClickListener(v -> {
            zoom += 3;
            Log.v("zoom", ""+zoom);
            statePlaying.handleUserInput(Constants.UserInput.ZOOMIN, 0);
        });
        ImageView out = findViewById(R.id.zoomOutButton);
        out.setOnClickListener(v -> {
            zoom -= 3;
            Log.v("zoom", ""+zoom);
            statePlaying.handleUserInput(Constants.UserInput.ZOOMOUT, 0);
        });
    }

    /**
     * Get the shortest path to solve the given maze
     * @param maze the current maze
     */
    protected void getShortestPath(Maze maze) {
        int[] startingPositionPos = maze.getStartingPosition();
        shortestPath = maze.getDistanceToExit(startingPositionPos[0], startingPositionPos[1]);
    }

}
