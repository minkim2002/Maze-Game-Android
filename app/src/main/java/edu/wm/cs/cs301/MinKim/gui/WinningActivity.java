package edu.wm.cs.cs301.MinKim.gui;

import android.os.Bundle;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: WinningActivity
 * Responsibilities: displays the winning screen containing energy consumption and path length,
 * and navigate back to the main screen (title)
 * Collaborators: PlayManuallyActivity, PlayAnimationActivity, AMazeActivity,
 */
public class WinningActivity extends FinalActivity {

    /**
     * set up the user interface components (true means winning)
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winning);
        setComponents(this, true);
    }
}