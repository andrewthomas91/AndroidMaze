package com.andrewthomas.hackathonmaze;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private CanvasView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canvasView = (CanvasView) findViewById(R.id.canvas);
    }

    public void setupNewMaze(View v) {
        canvasView.resetMaze();
    }

    public void makeMazeTilesBigger(View v) {
        canvasView.makeMazeTilesBigger();
    }

    public void makeMazeTilesSmaller(View v) {
        canvasView.makeMazeTilesSmaller();
    }
}
