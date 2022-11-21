package edu.wm.cs.cs301.MinKim.gui;

import android.os.Bundle;

import edu.wm.cs.cs301.MinKim.R;

public class LosingActivity extends FinalActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losing);
        setUpComponents(this, false);
    }
}
