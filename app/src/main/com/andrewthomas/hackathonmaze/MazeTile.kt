package com.andrewthomas.hackathonmaze

import android.graphics.Color

class MazeTile(val locationX: Int, val locationY: Int) {
    private var wallNorth: Boolean = false
    private var wallEast: Boolean = false
    private var wallSouth: Boolean = false
    private var wallWest: Boolean = false
    var visited: Boolean = false
    var color: Int = 0

    init {
        wallNorth = true
        wallEast = true
        wallSouth = true
        wallWest = true
        visited = false
        this.color = Color.WHITE
    }

    fun setIsWallPresent(direction: Directions, isWallPresent: Boolean) {
        when (direction) {
            Directions.NORTH -> wallNorth = isWallPresent
            Directions.EAST -> wallEast = isWallPresent
            Directions.SOUTH -> wallSouth = isWallPresent
            Directions.WEST -> wallWest = isWallPresent
            Directions.NONE -> {
            }
        }
    }

    fun getIsWallPresent(direction: Directions): Boolean {
        when (direction) {
            Directions.NORTH -> return wallNorth
            Directions.EAST -> return wallEast
            Directions.SOUTH -> return wallSouth
            Directions.WEST -> return wallWest
            else -> return true
        }
    }
}
