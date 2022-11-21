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

public class PlayActivity extends AppCompatActivity {

    protected int zoom = 100;
    protected int pathLength = 0;
    protected int shortestPath = 10;

    protected boolean showMap = false;
    protected boolean showCorrectPath = false;
    protected boolean showWalls = false;

    protected void setMenu(final Context context) {
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, v);

            setPopup(context, popup);

            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu, popup.getMenu());
            popup.show();

            Menu menu = popup.getMenu();
            menu.findItem(R.id.showMap).setChecked(showMap);
            menu.findItem(R.id.showWalls).setChecked(showWalls);
            menu.findItem(R.id.showCorrectPath).setChecked(showCorrectPath);

        });
    }

    private void setPopup(final Context context, PopupMenu popup) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
    protected void setPathLength() {
        Resources res = getResources();
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, pathLength));
    }

    protected void setZoom() {
        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(v -> {
            zoom += 3;
            Log.v("zoom", ""+zoom);
        });
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(v -> {
            zoom -= 3;
            Log.v("zoom", ""+zoom);
        });
    }
}
