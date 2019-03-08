package com.andrewthomas.hackathonmaze

import java.util.Random

class MazePiece(var locationX: Int, var locationY: Int, val tileSize: Int) {
    private val random = Random()
    var coordinateX: Int = 0
    var coordinateY: Int = 0
    var oldCoordinateX: Int = 0
        private set
    var oldCoordinateY: Int = 0
        private set
    var direction = Directions.NONE

    init {
        this.coordinateX = (locationX + 1) * tileSize
        this.coordinateY = (locationY + 1) * tileSize
    }

    fun isOnSameLocationAs(otherPiece: MazePiece): Boolean {
        return this.locationX == otherPiece.locationX && this.locationY == otherPiece.locationY
    }

    fun isOnSameLocationAs(mazeTile: MazeTile): Boolean {
        return this.locationX == mazeTile.locationX && this.locationY == mazeTile.locationY
    }

    fun generateNewFinishLocation(mazeSizeX: Int, mazeSizeY: Int) {
        locationX = random.nextInt(mazeSizeX / 2) + mazeSizeX / 4
        locationY = random.nextInt(mazeSizeY / 2) + mazeSizeY / 4
        coordinateX = (locationX + 1) * tileSize
        coordinateY = (locationY + 1) * tileSize
    }

    fun move(maze: MazeGenerator, direction: Directions): Boolean {
        oldCoordinateX = coordinateX
        oldCoordinateY = coordinateY
        if (direction === Directions.WEST) {
            if (locationX != 0 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.WEST)) {
                locationX -= 1
                return true
            }
        }
        if (direction === Directions.EAST) {
            if (locationX != maze.sizeX - 1 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.EAST)) {
                locationX += 1
                return true
            }
        }
        if (direction === Directions.NORTH) {
            if (locationY != 0 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.NORTH)) {
                locationY -= 1
                return true
            }
        }
        if (direction === Directions.SOUTH) {
            if (locationY != maze.sizeY - 1 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.SOUTH)) {
                locationY += 1
                return true
            }
        }
        return false
    }

    fun move(newLocationX: Int, newLocationY: Int): Boolean {
        oldCoordinateX = coordinateX
        oldCoordinateY = coordinateY
        if (locationX == newLocationX) {
            if (locationY < newLocationY) {
                direction = Directions.SOUTH
            } else {
                direction = Directions.NORTH
            }
        } else {
            if (locationX < newLocationX) {
                direction = Directions.EAST
            } else {
                direction = Directions.WEST
            }
        }
        locationX = newLocationX
        locationY = newLocationY
        return true
    }
}
