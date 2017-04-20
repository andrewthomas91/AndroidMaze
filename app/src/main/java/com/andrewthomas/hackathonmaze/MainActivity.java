package com.andrewthomas.hackathonmaze;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private CanvasView canvasView;
    private String[] mazeSolvingAlgorithms;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canvasView = (CanvasView) findViewById(R.id.canvas);
        mazeSolvingAlgorithms = getResources().getStringArray(R.array.maze_solver_algorithms);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mazeSolvingAlgorithms));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                canvasView.setMazeSolverAlgorithm(Algorithms.MANUAL);
                break;
            case 1:
                canvasView.setMazeSolverAlgorithm(Algorithms.RANDOM_MOUSE);
                break;
            case 2:
                canvasView.setMazeSolverAlgorithm(Algorithms.WALL_FOLLOWER);
                break;
            case 3:
                canvasView.setMazeSolverAlgorithm(Algorithms.RECURSIVE);
                break;
            case 4:
                canvasView.setMazeSolverAlgorithm(Algorithms.DEPTH_FIRST_SEARCH);
                break;
            default:
                canvasView.setMazeSolverAlgorithm(Algorithms.MANUAL);
                break;
        }
        drawerLayout.closeDrawer(drawerListView);
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
