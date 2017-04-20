package com.andrewthomas.hackathonmaze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.zip.DeflaterInputStream;

public class CanvasView extends View {

    private int width, height, mazeSizeWidth, mazeSizeHeight, mazeTileWidthAndHeight, oldCoordinateX, oldCoordinateY;
    private boolean mazeSolved = false;
    private boolean runnerInMotion = false;
    private float centerX, centerY;
    private Bitmap bitmap;
    private Canvas canvas;
    Context context;
    private Paint paint;
    private MazeGenerator maze;
    private MazePiece finish;
    private MazePiece runner;
    private Algorithms solverAlgorithm = Algorithms.MANUAL;
    private Thread runnerThread;
    private boolean running = false;
    private boolean[][] wasHere;
    private Stack<MazeTile> solution = new Stack<>();

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        paint.setTextSize(200);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.mazeTileWidthAndHeight = width / (10 + 1);
        this.height = height;
        this.centerX = width / 2;
        this.centerY = height / 2;
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        resetMaze();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas);
        drawRunner(canvas);
        drawFinish(canvas);
        if (mazeSolved) {
            paint.setColor(Color.RED);
            canvas.drawText("Maze Solved", centerX - 500, centerY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (runnerInMotion) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (solverAlgorithm == Algorithms.MANUAL) {
                    if (runner.move(maze, directionOfClickLocation(x, y))) {
                        moveToNewLocation();
                    }
                    if (runner.isOnSameLocationAs(finish)) {
                        mazeSolved = true;
                    }
                    invalidate();
                } else {
                    running = true;
                    switch (solverAlgorithm) {
                        case RANDOM_MOUSE:
                            createRandomMouseThread().start();
                            break;
                        case WALL_FOLLOWER:
                            createWallFollowerThread().start();
                            break;
                        case RECURSIVE:
                            wasHere = new boolean[maze.getSizeX()][maze.getSizeY()];
                            createRecursiveThread().start();
                            break;
                        default:
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private Thread createRandomMouseThread() {
        return new Thread(new Runnable() {
            public void run() {
                Random random = new Random();
                while (!runner.isOnSameLocationAs(finish) && running) {
                    MazeTile currentTile = maze.getMazeTileAt(runner.getLocationX(), runner.getLocationY());

                    ArrayList<Directions> openDirections = new ArrayList<>();
                    switch (runner.getDirection()) {
                        case NORTH:
                            if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                openDirections.add(Directions.NORTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                openDirections.add(Directions.EAST);
                            }
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                openDirections.add(Directions.WEST);
                            }
                            if (openDirections.size() == 0) {
                                openDirections.add(Directions.SOUTH);
                            }
                            break;
                        case SOUTH:
                            if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                openDirections.add(Directions.SOUTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                openDirections.add(Directions.EAST);
                            }
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                openDirections.add(Directions.WEST);
                            }
                            if (openDirections.size() == 0) {
                                openDirections.add(Directions.NORTH);
                            }
                            break;
                        case WEST:
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                openDirections.add(Directions.WEST);
                            }
                            if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                openDirections.add(Directions.NORTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                openDirections.add(Directions.SOUTH);
                            }
                            if (openDirections.size() == 0) {
                                openDirections.add(Directions.EAST);
                            }
                            break;
                        case EAST:
                            if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                openDirections.add(Directions.EAST);
                            }
                            if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                openDirections.add(Directions.NORTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                openDirections.add(Directions.SOUTH);
                            }
                            if (openDirections.size() == 0) {
                                openDirections.add(Directions.WEST);
                            }
                            break;
                        default:
                            if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                openDirections.add(Directions.NORTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                openDirections.add(Directions.SOUTH);
                            }
                            if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                openDirections.add(Directions.EAST);
                            }
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                openDirections.add(Directions.WEST);
                            }
                            break;
                    }
                    int directionToGo = random.nextInt(openDirections.size());
                    runner.move(maze, openDirections.get(directionToGo));

                    moveToNewLocation();
                    if (runnerThread != null) {
                        try {
                            runnerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (runner.isOnSameLocationAs(finish)) {
                    mazeSolved = true;
                }
            }
        });
    }

    private Thread createWallFollowerThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (!runner.isOnSameLocationAs(finish) && running) {
                    MazeTile currentTile = maze.getMazeTileAt(runner.getLocationX(), runner.getLocationY());
                    Directions nextDirection = null;
                    switch (runner.getDirection()) {
                        case NORTH:
                            if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                nextDirection = Directions.EAST;
                            } else if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                nextDirection = Directions.NORTH;
                            } else if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                nextDirection = Directions.WEST;
                            } else {
                                nextDirection = Directions.SOUTH;
                            }
                            break;
                        case SOUTH:
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                nextDirection = Directions.WEST;
                            } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                nextDirection = Directions.SOUTH;
                            } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                nextDirection = Directions.EAST;
                            } else {
                                nextDirection = Directions.NORTH;
                            }
                            break;
                        case WEST:
                            if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                nextDirection = Directions.NORTH;
                            } else if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                nextDirection = Directions.WEST;
                            } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                nextDirection = Directions.SOUTH;
                            } else {
                                nextDirection = Directions.EAST;
                            }
                            break;
                        case EAST:
                            if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                nextDirection = Directions.SOUTH;
                            } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                nextDirection = Directions.EAST;
                            } else if (!currentTile.getIsWallPresent(Directions.NORTH)) {
                                nextDirection = Directions.NORTH;
                            } else {
                                nextDirection = Directions.WEST;
                            }
                            break;
                        default:
                            if (!currentTile.getIsWallPresent(Directions.WEST)) {
                                nextDirection = Directions.WEST;
                            } else if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
                                nextDirection = Directions.SOUTH;
                            } else if (!currentTile.getIsWallPresent(Directions.EAST)) {
                                nextDirection = Directions.EAST;
                            } else {
                                nextDirection = Directions.SOUTH;
                            }
                            break;
                    }

                    if (nextDirection != null) {
                        runner.move(maze, nextDirection);
                    }
                    moveToNewLocation();
                    if (runnerThread != null) {
                        try {
                            runnerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (runner.isOnSameLocationAs(finish))

                {
                    mazeSolved = true;
                }
            }
        });
    }

    private Thread createRecursiveThread() {
        return new Thread(new Runnable() {
            public void run() {

                recursiveSolve(runner.getLocationX(), runner.getLocationY());

                MazeTile currentTile;
                while(!solution.empty() && running) {
                    currentTile = solution.pop();
                    if(runner.isOnSameLocationAs(currentTile)) {
                        continue;
                    }
                    runner.move(currentTile.getLocationX(), currentTile.getLocationY());
                    moveToNewLocation();
                    if (runnerThread != null) {
                        try {
                            runnerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(running) {
                    runner.move(finish.getLocationX(), finish.getLocationY());
                    moveToNewLocation();
                    if (runnerThread != null) {
                        try {
                            runnerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (runner.isOnSameLocationAs(finish)) {
                    mazeSolved = true;
                }
            }
        });
    }

    public boolean recursiveSolve(int x, int y) {
        if (x == finish.getLocationX() && y == finish.getLocationY()) {
            return true;
        }
        if (wasHere[x][y]) {
            return false;
        }
        wasHere[x][y] = true;
        MazeTile currentTile = maze.getMazeTileAt(x, y);
        if (!currentTile.getIsWallPresent(Directions.WEST)) {
            if (recursiveSolve(x - 1, y)) {
                solution.push(new MazeTile(x, y));
                return true;
            }
        }
        if (!currentTile.getIsWallPresent(Directions.EAST)) {
            if (recursiveSolve(x + 1, y)) {
                solution.push(new MazeTile(x, y));
                return true;
            }
        }
        if (!currentTile.getIsWallPresent(Directions.NORTH)) {
            if (recursiveSolve(x, y - 1)) {
                solution.push(new MazeTile(x, y));
                return true;
            }
        }
        if (!currentTile.getIsWallPresent(Directions.SOUTH)) {
            if (recursiveSolve(x, y + 1)) {
                solution.push(new MazeTile(x, y));
                return true;
            }
        }
        return false;
    }

    private void moveToNewLocation() {
        runnerInMotion = true;
        oldCoordinateX = runner.getOldCoordinateX();
        oldCoordinateY = runner.getOldCoordinateY();
        createNewRunnerThread();
        runnerThread.start();
    }

    private void createNewRunnerThread() {
        runnerThread = new Thread(new Runnable() {
            public void run() {
                int count = 0;
                while (count <= runner.getTileSize()) {
                    try {
                        switch (runner.getDirection()) {
                            case EAST:
                                runner.setCoordinateX(oldCoordinateX + count);
                                break;
                            case WEST:
                                runner.setCoordinateX(oldCoordinateX - count);
                                break;
                            case SOUTH:
                                runner.setCoordinateY(oldCoordinateY + count);
                                break;
                            case NORTH:
                                runner.setCoordinateY(oldCoordinateY - count);
                                break;
                            default:
                                break;
                        }
                        Thread.sleep(25);
                        count = (count + runner.getTileSize() / 10);
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runner.setCoordinateX((runner.getLocationX() + 1) * runner.getTileSize());
                runner.setCoordinateY((runner.getLocationY() + 1) * runner.getTileSize());
                runnerInMotion = false;
            }
        });
    }

    private Directions directionOfClickLocation(float x, float y) {
        double angleInDegrees = 0;
        if ((x > centerX)) {
            angleInDegrees = Math.atan2((x - centerX), (centerY - y)) * 180 / Math.PI;
        } else if ((x < centerX)) {
            angleInDegrees = 360 - (Math.atan2((centerX - x), (centerY - y)) * 180 / Math.PI);
        }

        if (angleInDegrees > 45 && angleInDegrees <= 135) {
            return Directions.EAST;
        }
        if (angleInDegrees > 135 && angleInDegrees <= 225) {
            return Directions.SOUTH;
        }
        if (angleInDegrees > 225 && angleInDegrees <= 315) {
            return Directions.WEST;
        }
        if (angleInDegrees > 315 || angleInDegrees <= 45) {
            return Directions.NORTH;
        }
        return Directions.NONE;
    }

    public void setMazeSolverAlgorithm(Algorithms algorithm) {
        solverAlgorithm = algorithm;
    }

    public void resetMaze() {
        running = false;
        solution.clear();
        clearCanvas();
        mazeSolved = false;
        mazeSizeWidth = (width / mazeTileWidthAndHeight) - 1;
        mazeSizeHeight = (height / mazeTileWidthAndHeight - 1);
        maze = new MazeGenerator(mazeSizeWidth, mazeSizeHeight, mazeTileWidthAndHeight);
        runner = new MazePiece(0, 0, mazeTileWidthAndHeight);
        finish = new MazePiece(0, 0, mazeTileWidthAndHeight);
        finish.generateNewFinishLocation(mazeSizeWidth, mazeSizeHeight);
    }

    public void makeMazeTilesBigger() {
        while (mazeTileWidthAndHeight < width / 8 || mazeTileWidthAndHeight < height / 8) {
            mazeTileWidthAndHeight++;
            int newMazeSizeWidth = (width / mazeTileWidthAndHeight) - 1;
            int newMazeSizeHeight = (height / mazeTileWidthAndHeight - 1);
            if (newMazeSizeWidth != mazeSizeWidth || newMazeSizeHeight != mazeSizeHeight) {
                resetMaze();
                break;
            }
        }
    }

    public void makeMazeTilesSmaller() {
        while (mazeTileWidthAndHeight > 9) {
            mazeTileWidthAndHeight--;
            int newMazeSizeWidth = (width / mazeTileWidthAndHeight) - 1;
            int newMazeSizeHeight = (height / mazeTileWidthAndHeight - 1);
            if (newMazeSizeWidth != mazeSizeWidth || newMazeSizeHeight != mazeSizeHeight) {
                resetMaze();
                break;
            }
        }
    }

    public void drawRunner(Canvas canvas) {
        if (runner != null) {
            paint.setColor(Color.BLUE);
            float left = runner.getCoordinateX() - (mazeTileWidthAndHeight / 3);
            float right = runner.getCoordinateX() + (mazeTileWidthAndHeight / 3);
            float top = runner.getCoordinateY() - (mazeTileWidthAndHeight / 3);
            float bottom = runner.getCoordinateY() + (mazeTileWidthAndHeight / 3);
            canvas.drawOval(left, top, right, bottom, paint);
        }
    }

    public void drawFinish(Canvas canvas) {
        if (finish != null) {
            paint.setColor(Color.RED);
            float left = finish.getCoordinateX() - (mazeTileWidthAndHeight / 3);
            float right = finish.getCoordinateX() + (mazeTileWidthAndHeight / 3);
            float top = finish.getCoordinateY() - (mazeTileWidthAndHeight / 3);
            float bottom = finish.getCoordinateY() + (mazeTileWidthAndHeight / 3);
            canvas.drawOval(left, top, right, bottom, paint);
        }
    }

    public void drawMaze(Canvas canvas) {
        paint.setColor(Color.BLACK);
        if (maze != null) {
            for (int x = 0; x < maze.getSizeX(); x++) {
                for (int y = 0; y < maze.getSizeY(); y++) {
                    drawMazeTile(maze.getMazeTileAt(x, y), canvas);
                }
            }
        }
    }

    public void drawMazeTile(MazeTile mazeTile, Canvas canvas) {
        int x = mazeTile.getLocationX() + 1;
        int y = mazeTile.getLocationY() + 1;

        float left = (x * mazeTileWidthAndHeight) - (mazeTileWidthAndHeight / 2) - 1;
        float top = (y * mazeTileWidthAndHeight) - (mazeTileWidthAndHeight / 2) - 1;
        float right = (x * mazeTileWidthAndHeight) + (mazeTileWidthAndHeight / 2) + 1;
        float bottom = (y * mazeTileWidthAndHeight) + (mazeTileWidthAndHeight / 2) + 1;

        if (mazeTile.getIsWallPresent(Directions.NORTH)) {
            canvas.drawLine(left, top, right, top, paint);
        }
        if (mazeTile.getIsWallPresent(Directions.EAST)) {
            canvas.drawLine(right, top, right, bottom, paint);
        }
        if (mazeTile.getIsWallPresent(Directions.SOUTH)) {
            canvas.drawLine(left, bottom, right, bottom, paint);
        }
        if (mazeTile.getIsWallPresent(Directions.WEST)) {
            canvas.drawLine(left, top, left, bottom, paint);
        }
    }

    public void clearCanvas() {
        invalidate();
    }
}
