package edu.wm.cs.cs301.MinKim.gui;

import android.os.Bundle;

import edu.wm.cs.cs301.MinKim.R;

/**
 * @author Min Kim
 * Class: LosingActivity
 * Responsibilities: displays the losing screen containing energy consumption and path length,
 * and navigate back to the main screen (title)
 * Collaborators: PlayManuallyActivity, PlayAnimationActivity, AMazeActivity
 */
public class LosingActivity extends FinalActivity {

    /**
     * set up the user interface components (false means losing)
     * @param savedInstanceState a reference to the bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losing);
        setComponents(this, false);
    }
}
