package com.andrewthomas.hackathonmaze

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import java.util.ArrayList
import java.util.Random
import java.util.Stack

class CanvasView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var mazeSizeWidth: Int = 0
    private var mazeSizeHeight: Int = 0
    private var mazeTileWidthAndHeight: Int = 0
    private var oldCoordinateX: Int = 0
    private var oldCoordinateY: Int = 0
    private var mazeSolved = false
    private var runnerInMotion = false
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private val paint: Paint = Paint()
    private lateinit var maze: MazeGenerator
    private lateinit var finish: MazePiece
    private lateinit var runner: MazePiece
    private var solverAlgorithm = Algorithms.MANUAL
    private var runnerThread: Thread? = null
    private var running = false
    private var wasHere: Array<BooleanArray>? = null
    private val solution = Stack<MazeTile>()

    init {
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 5f
        paint.textSize = 200f
    }

    override fun onSizeChanged(canvasWidth: Int, canvasHeight: Int, oldcanvasWidth: Int, oldcanvasHeight: Int) {
        this.canvasWidth = canvasWidth
        this.mazeTileWidthAndHeight = canvasWidth / (10 + 1)
        this.canvasHeight = canvasHeight
        this.centerX = (canvasWidth / 2).toFloat()
        this.centerY = (canvasHeight / 2).toFloat()
        super.onSizeChanged(canvasWidth, canvasHeight, oldcanvasWidth, oldcanvasHeight)
        bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        resetMaze()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawMaze(canvas)
        drawRunner(canvas)
        drawFinish(canvas)
        if (mazeSolved) {
            paint.color = Color.RED
            canvas.drawText("Maze Solved", centerX - 500, centerY, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (runnerInMotion) {
            return false
        }
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (solverAlgorithm === Algorithms.MANUAL) {
                if (runner!!.move(maze, directionOfClickLocation(x, y))) {
                    moveToNewLocation()
                }
                if (runner!!.isOnSameLocationAs(finish)) {
                    mazeSolved = true
                }
                invalidate()
            } else {
                running = true
                when (solverAlgorithm) {
                    Algorithms.RANDOM_MOUSE -> createRandomMouseThread().start()
                    Algorithms.WALL_FOLLOWER -> createWallFollowerThread().start()
                    Algorithms.RECURSIVE -> {
                        wasHere = Array(maze!!.sizeX) { BooleanArray(maze!!.sizeY) }
                        createRecursiveThread().start()
                    }
                    else -> {
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    private fun createRandomMouseThread(): Thread {
        return Thread(Runnable {
            val random = Random()
            while (!runner!!.isOnSameLocationAs(finish) && running) {
                val currentTile = maze!!.getMazeTileAt(runner!!.locationX, runner!!.locationY)

                val openDirections = ArrayList<Directions>()
                when (runner!!.direction) {
                    Directions.NORTH -> {
                        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                            openDirections.add(Directions.NORTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.EAST)) {
                            openDirections.add(Directions.EAST)
                        }
                        if (!currentTile.getIsWallPresent(Directions.WEST)) {
                            openDirections.add(Directions.WEST)
                        }
                        if (openDirections.size == 0) {
                            openDirections.add(Directions.SOUTH)
                        }
                    }
                    Directions.SOUTH -> {
                        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                            openDirections.add(Directions.SOUTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.EAST)) {
                            openDirections.add(Directions.EAST)
                        }
                        if (!currentTile.getIsWallPresent(Directions.WEST)) {
                            openDirections.add(Directions.WEST)
                        }
                        if (openDirections.size == 0) {
                            openDirections.add(Directions.NORTH)
                        }
                    }
                    Directions.WEST -> {
                        if (!currentTile.getIsWallPresent(Directions.WEST)) {
                            openDirections.add(Directions.WEST)
                        }
                        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                            openDirections.add(Directions.NORTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                            openDirections.add(Directions.SOUTH)
                        }
                        if (openDirections.size == 0) {
                            openDirections.add(Directions.EAST)
                        }
                    }
                    Directions.EAST -> {
                        if (!currentTile.getIsWallPresent(Directions.EAST)) {
                            openDirections.add(Directions.EAST)
                        }
                        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                            openDirections.add(Directions.NORTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                            openDirections.add(Directions.SOUTH)
                        }
                        if (openDirections.size == 0) {
                            openDirections.add(Directions.WEST)
                        }
                    }
                    else -> {
                        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                            openDirections.add(Directions.NORTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                            openDirections.add(Directions.SOUTH)
                        }
                        if (!currentTile.getIsWallPresent(Directions.EAST)) {
                            openDirections.add(Directions.EAST)
                        }
                        if (!currentTile.getIsWallPresent(Directions.WEST)) {
                            openDirections.add(Directions.WEST)
                        }
                    }
                }
                val directionToGo = random.nextInt(openDirections.size)
                runner!!.move(maze, openDirections[directionToGo])

                moveToNewLocation()
                if (runnerThread != null) {
                    try {
                        runnerThread!!.join()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
            if (runner!!.isOnSameLocationAs(finish)) {
                mazeSolved = true
            }
        })
    }

    private fun createWallFollowerThread(): Thread {
        return Thread(Runnable {
            while (!runner!!.isOnSameLocationAs(finish) && running) {
                val currentTile = maze!!.getMazeTileAt(runner!!.locationX, runner!!.locationY)
                var nextDirection: Directions?
                when (runner!!.direction) {
                    Directions.NORTH -> if (!currentTile.getIsWallPresent(Directions.EAST)) {
                        nextDirection = Directions.EAST
                    } else if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                        nextDirection = Directions.NORTH
                    } else if (!currentTile.getIsWallPresent(Directions.WEST)) {
                        nextDirection = Directions.WEST
                    } else {
                        nextDirection = Directions.SOUTH
                    }
                    Directions.SOUTH -> if (!currentTile.getIsWallPresent(Directions.WEST)) {
                        nextDirection = Directions.WEST
                    } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                        nextDirection = Directions.SOUTH
                    } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                        nextDirection = Directions.EAST
                    } else {
                        nextDirection = Directions.NORTH
                    }
                    Directions.WEST -> if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                        nextDirection = Directions.NORTH
                    } else if (!currentTile.getIsWallPresent(Directions.WEST)) {
                        nextDirection = Directions.WEST
                    } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                        nextDirection = Directions.SOUTH
                    } else {
                        nextDirection = Directions.EAST
                    }
                    Directions.EAST -> if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                        nextDirection = Directions.SOUTH
                    } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                        nextDirection = Directions.EAST
                    } else if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                        nextDirection = Directions.NORTH
                    } else {
                        nextDirection = Directions.WEST
                    }
                    else -> if (!currentTile.getIsWallPresent(Directions.WEST)) {
                        nextDirection = Directions.WEST
                    } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                        nextDirection = Directions.SOUTH
                    } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                        nextDirection = Directions.EAST
                    } else {
                        nextDirection = Directions.SOUTH
                    }
                }

                maze!!.getMazeTileAt(runner!!.locationX, runner!!.locationY).color = Color.GREEN
                runner!!.move(maze, nextDirection)
                moveToNewLocation()
                if (runnerThread != null) {
                    try {
                        runnerThread!!.join()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
            if (runner!!.isOnSameLocationAs(finish)) {
                mazeSolved = true
            }
        })
    }

    private fun createRecursiveThread(): Thread {
        return Thread(Runnable {
            if (mazeSolved) {
                return@Runnable
            }
            recursiveSolve(runner!!.locationX, runner!!.locationY)

            var currentTile: MazeTile
            while (!solution.empty() && running) {
                maze!!.getMazeTileAt(runner!!.locationX, runner!!.locationY).color = Color.GREEN
                currentTile = solution.pop()
                if (runner!!.isOnSameLocationAs(currentTile)) {
                    continue
                }
                runner!!.move(currentTile.locationX, currentTile.locationY)
                moveToNewLocation()
                if (runnerThread != null) {
                    try {
                        runnerThread!!.join()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
            if (runner!!.isOnSameLocationAs(finish)) {
                mazeSolved = true
            }
        })
    }

    private fun recursiveSolve(x: Int, y: Int): Boolean {
        if (x == finish!!.locationX && y == finish!!.locationY) {
            solution.push(MazeTile(x, y))
            return true
        }
        if (wasHere!![x][y]) {
            return false
        }
        wasHere!![x][y] = true
        val currentTile = maze!!.getMazeTileAt(x, y)
        if (!currentTile.getIsWallPresent(Directions.WEST)) {
            if (recursiveSolve(x - 1, y)) {
                solution.push(MazeTile(x, y))
                return true
            }
        }
        if (!currentTile.getIsWallPresent(Directions.EAST)) {
            if (recursiveSolve(x + 1, y)) {
                solution.push(MazeTile(x, y))
                return true
            }
        }
        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
            if (recursiveSolve(x, y - 1)) {
                solution.push(MazeTile(x, y))
                return true
            }
        }
        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
            if (recursiveSolve(x, y + 1)) {
                solution.push(MazeTile(x, y))
                return true
            }
        }
        return false
    }

    private fun moveToNewLocation() {
        runnerInMotion = true
        oldCoordinateX = runner!!.oldCoordinateX
        oldCoordinateY = runner!!.oldCoordinateY
        createNewRunnerThread()
        runnerThread!!.start()
    }

    private fun createNewRunnerThread() {
        runnerThread = Thread(Runnable {
            var count = 0
            while (count <= runner!!.tileSize) {
                try {
                    when (runner!!.direction) {
                        Directions.EAST -> runner!!.coordinateX = oldCoordinateX + count
                        Directions.WEST -> runner!!.coordinateX = oldCoordinateX - count
                        Directions.SOUTH -> runner!!.coordinateY = oldCoordinateY + count
                        Directions.NORTH -> runner!!.coordinateY = oldCoordinateY - count
                        else -> {
                        }
                    }
                    Thread.sleep(25)
                    count = count + runner!!.tileSize / 10
                    postInvalidate()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            runner!!.coordinateX = (runner!!.locationX + 1) * runner!!.tileSize
            runner!!.coordinateY = (runner!!.locationY + 1) * runner!!.tileSize
            runnerInMotion = false
        })
    }

    private fun directionOfClickLocation(x: Float, y: Float): Directions {
        var angleInDegrees = 0.0
        if (x > centerX) {
            angleInDegrees = Math.atan2((x - centerX).toDouble(), (centerY - y).toDouble()) * 180 / Math.PI
        } else if (x < centerX) {
            angleInDegrees = 360 - Math.atan2((centerX - x).toDouble(), (centerY - y).toDouble()) * 180 / Math.PI
        }

        if (angleInDegrees > 45 && angleInDegrees <= 135) {
            return Directions.EAST
        }
        if (angleInDegrees > 135 && angleInDegrees <= 225) {
            return Directions.SOUTH
        }
        if (angleInDegrees > 225 && angleInDegrees <= 315) {
            return Directions.WEST
        }
        return if (angleInDegrees > 315 || angleInDegrees <= 45) {
            Directions.NORTH
        } else Directions.NONE
    }

    fun setMazeSolverAlgorithm(algorithm: Algorithms) {
        solverAlgorithm = algorithm
    }

    fun resetMaze() {
        running = false
        solution.clear()
        clearCanvas()
        mazeSolved = false
        mazeSizeWidth = canvasWidth / mazeTileWidthAndHeight - 1
        mazeSizeHeight = canvasHeight / mazeTileWidthAndHeight - 1
        maze = MazeGenerator(mazeSizeWidth, mazeSizeHeight)
        runner = MazePiece(0, 0, mazeTileWidthAndHeight)
        finish = MazePiece(0, 0, mazeTileWidthAndHeight)
        finish!!.generateNewFinishLocation(mazeSizeWidth, mazeSizeHeight)
    }

    fun makeMazeTilesBigger() {
        while (mazeTileWidthAndHeight < canvasWidth / 8 || mazeTileWidthAndHeight < canvasHeight / 8) {
            mazeTileWidthAndHeight++
            val newmazeSizeWidth = canvasWidth / mazeTileWidthAndHeight - 1
            val newmazeSizeHeight = canvasHeight / mazeTileWidthAndHeight - 1
            if (newmazeSizeWidth != mazeSizeWidth || newmazeSizeHeight != mazeSizeHeight) {
                resetMaze()
                break
            }
        }
    }

    fun makeMazeTilesSmaller() {
        while (mazeTileWidthAndHeight > 9) {
            mazeTileWidthAndHeight--
            val newmazeSizeWidth = canvasWidth / mazeTileWidthAndHeight - 1
            val newmazeSizeHeight = canvasHeight / mazeTileWidthAndHeight - 1
            if (newmazeSizeWidth != mazeSizeWidth || newmazeSizeHeight != mazeSizeHeight) {
                resetMaze()
                break
            }
        }
    }

    private fun drawRunner(canvas: Canvas) {
        if (runner != null) {
            paint.color = Color.BLUE
            val left = (runner!!.coordinateX - mazeTileWidthAndHeight / 3).toFloat()
            val right = (runner!!.coordinateX + mazeTileWidthAndHeight / 3).toFloat()
            val top = (runner!!.coordinateY - mazeTileWidthAndHeight / 3).toFloat()
            val bottom = (runner!!.coordinateY + mazeTileWidthAndHeight / 3).toFloat()
            canvas.drawOval(left, top, right, bottom, paint)
        }
    }

    private fun drawFinish(canvas: Canvas) {
        if (finish != null) {
            paint.color = Color.RED
            val left = (finish!!.coordinateX - mazeTileWidthAndHeight / 3).toFloat()
            val right = (finish!!.coordinateX + mazeTileWidthAndHeight / 3).toFloat()
            val top = (finish!!.coordinateY - mazeTileWidthAndHeight / 3).toFloat()
            val bottom = (finish!!.coordinateY + mazeTileWidthAndHeight / 3).toFloat()
            canvas.drawOval(left, top, right, bottom, paint)
        }
    }

    private fun drawMaze(canvas: Canvas) {
        if (maze != null) {
            for (x in 0 until maze!!.sizeX) {
                for (y in 0 until maze!!.sizeY) {
                    drawMazeTile(maze!!.getMazeTileAt(x, y), canvas)
                }
            }
        }
    }

    private fun drawMazeTile(mazeTile: MazeTile, canvas: Canvas) {
        val x = mazeTile.locationX + 1
        val y = mazeTile.locationY + 1

        val left = (x * mazeTileWidthAndHeight - mazeTileWidthAndHeight / 2 - 1).toFloat()
        val top = (y * mazeTileWidthAndHeight - mazeTileWidthAndHeight / 2 - 1).toFloat()
        val right = (x * mazeTileWidthAndHeight + mazeTileWidthAndHeight / 2 + 1).toFloat()
        val bottom = (y * mazeTileWidthAndHeight + mazeTileWidthAndHeight / 2 + 1).toFloat()

        paint.color = mazeTile.color
        canvas.drawRect(left, top, right, bottom, paint)

        paint.color = Color.BLACK
        if (mazeTile.getIsWallPresent(Directions.NORTH)) {
            canvas.drawLine(left, top, right, top, paint)
        }
        if (mazeTile.getIsWallPresent(Directions.EAST)) {
            canvas.drawLine(right, top, right, bottom, paint)
        }
        if (mazeTile.getIsWallPresent(Directions.SOUTH)) {
            canvas.drawLine(left, bottom, right, bottom, paint)
        }
        if (mazeTile.getIsWallPresent(Directions.WEST)) {
            canvas.drawLine(left, top, left, bottom, paint)
        }
    }

    private fun clearCanvas() {
        invalidate()
    }
}
