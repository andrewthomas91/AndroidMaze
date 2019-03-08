package com.andrewthomas.hackathonmaze

import java.util.ArrayList
import java.util.Random

class MazeGenerator(val sizeX: Int, val sizeY: Int) {
    private val maze: Array<Array<MazeTile>> = Array(sizeX) { x -> Array(sizeY) { y -> MazeTile(x, y) } }
    private val random = Random()

    init {
        generateMaze()
    }

    private fun generateMaze() {
        val currentX = random.nextInt(sizeX)
        val currentY = random.nextInt(sizeY)
        visitTile(currentX, currentY, Directions.NONE)
    }

    private fun visitTile(x: Int, y: Int, wallToRemove: Directions) {
        maze[x][y].visited = true
        maze[x][y].setIsWallPresent(wallToRemove, false)

        val directionsLeft = ArrayList<Directions>()
        directionsLeft.add(Directions.NORTH)
        directionsLeft.add(Directions.EAST)
        directionsLeft.add(Directions.SOUTH)
        directionsLeft.add(Directions.WEST)

        var nextDirectionToVisit: Int
        while (!directionsLeft.isEmpty()) {
            nextDirectionToVisit = random.nextInt(directionsLeft.size)
            when (directionsLeft[nextDirectionToVisit]) {
                Directions.NORTH -> {
                    if (y != 0 && !maze[x][y - 1].visited) {
                        maze[x][y].setIsWallPresent(Directions.NORTH, false)
                        visitTile(x, y - 1, Directions.SOUTH)
                    }
                    directionsLeft.removeAt(nextDirectionToVisit)
                }
                Directions.EAST -> {
                    if (x != sizeX - 1 && !maze[x + 1][y].visited) {
                        maze[x][y].setIsWallPresent(Directions.EAST, false)
                        visitTile(x + 1, y, Directions.WEST)
                    }
                    directionsLeft.removeAt(nextDirectionToVisit)
                }
                Directions.SOUTH -> {
                    if (y != sizeY - 1 && !maze[x][y + 1].visited) {
                        maze[x][y].setIsWallPresent(Directions.SOUTH, false)
                        visitTile(x, y + 1, Directions.NORTH)
                    }
                    directionsLeft.removeAt(nextDirectionToVisit)
                }
                Directions.WEST -> {
                    if (x != 0 && !maze[x - 1][y].visited) {
                        maze[x][y].setIsWallPresent(Directions.WEST, false)
                        visitTile(x - 1, y, Directions.EAST)
                    }
                    directionsLeft.removeAt(nextDirectionToVisit)
                }
                Directions.NONE -> {
                }
            }
        }
    }

    fun getMazeTileAt(x: Int, y: Int): MazeTile {
        return maze[x][y]
    }
}
