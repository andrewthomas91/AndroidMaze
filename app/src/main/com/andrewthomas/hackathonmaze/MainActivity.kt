package com.andrewthomas.hackathonmaze

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.leftDrawer
import kotlinx.android.synthetic.main.activity_main.canvas

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mazeSolvingAlgorithms = resources.getStringArray(R.array.maze_solver_algorithms)
        leftDrawer.adapter = ArrayAdapter(this, R.layout.drawer_list_item, mazeSolvingAlgorithms)
        leftDrawer.onItemClickListener = DrawerItemClickListener()
    }

    private inner class DrawerItemClickListener : AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            selectItem(position)
        }
    }

    private fun selectItem(position: Int) {
        when (position) {
            0 -> canvas.setMazeSolverAlgorithm(Algorithms.MANUAL)
            1 -> canvas.setMazeSolverAlgorithm(Algorithms.RANDOM_MOUSE)
            2 -> canvas.setMazeSolverAlgorithm(Algorithms.WALL_FOLLOWER)
            3 -> canvas.setMazeSolverAlgorithm(Algorithms.RECURSIVE)
            else -> canvas.setMazeSolverAlgorithm(Algorithms.MANUAL)
        }
        drawerLayout.closeDrawer(leftDrawer)
    }

    fun setupNewMaze(v: View) {
        canvas.resetMaze()
    }

    fun makeMazeTilesBigger(v: View) {
        canvas.makeMazeTilesBigger()
    }

    fun makeMazeTilesSmaller(v: View) {
        canvas.makeMazeTilesSmaller()
    }
}
