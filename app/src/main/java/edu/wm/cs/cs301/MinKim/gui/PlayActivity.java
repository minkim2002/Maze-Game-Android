package edu.wm.cs.cs301.MinKim.gui;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: PlayActivity
 * Responsibilities: parent activity of two play activities (Manual, Animation)
 * Collaborators: N/A
 */
public class PlayActivity extends AppCompatActivity {

    protected int zoom = 100;
    protected int pathLength = 0;
    protected int shortestPath = 10;

    protected boolean showMap = false;
    protected boolean showCorrectPath = false;
    protected boolean showWalls = false;

    /**
     * Set up listener for a menu button
     * @param context activity in which the popUp will be generated
     */
    protected void setMenu(final Context context) {
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            // create a menu according to the inputted context
            PopupMenu popup = new PopupMenu(context, v);
            // Listener for the popup
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
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast;
                switch (item.getItemId()) {
                    case R.id.showMap:
                        showMap = !showMap;
                        item.setChecked(showMap);
                        Log.v("Show map", ""+showMap);
                        toast = Toast.makeText(context, "Toggling map", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showWalls:
                        showWalls = !showWalls;
                        item.setChecked(showWalls);
                        Log.v("Show all walls", ""+showWalls);
                        toast = Toast.makeText(context, "Toggling walls", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showCorrectPath:
                        showCorrectPath = !showCorrectPath;
                        item.setChecked(showCorrectPath);
                        Log.v("Show solution", ""+showCorrectPath);
                        toast = Toast.makeText(context, "Toggling solution", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    /**
     * Update the path length text on screen
     */
    protected void setPathLength() {
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
        });
        ImageView out = findViewById(R.id.zoomOutButton);
        out.setOnClickListener(v -> {
            zoom -= 3;
            Log.v("zoom", ""+zoom);
        });
    }
}
