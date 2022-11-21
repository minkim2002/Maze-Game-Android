package edu.wm.cs.cs301.MinKim.gui;

import android.os.Bundle;

import edu.wm.cs.cs301.MinKim.R;

public class WinningActivity extends FinalActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winning);
        setUpComponents(this, true);
    }
}