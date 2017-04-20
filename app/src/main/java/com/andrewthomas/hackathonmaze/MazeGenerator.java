package com.andrewthomas.hackathonmaze;

import java.util.ArrayList;
import java.util.Random;

public class MazeGenerator {
    private final int sizeX;
    private final int sizeY;
    private final int mazeTileWidthAndHeight;
    private final MazeTile[][] maze;
    private Random random = new Random();

    public MazeGenerator(int x, int y, int widthAndHeight) {
        sizeX = x;
        sizeY = y;
        maze = new MazeTile[sizeX][sizeY];
        mazeTileWidthAndHeight = widthAndHeight;
        generateMaze();
    }

    private void generateMaze() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                maze[x][y] = new MazeTile(x, y);
            }
        }
        int currentX = random.nextInt(sizeX);
        int currentY = random.nextInt(sizeY);
        visitTile(currentX, currentY, Directions.NONE);
    }

    private void visitTile(int x, int y, Directions wallToRemove) {
        maze[x][y].setVisited(true);
        maze[x][y].setIsWallPresent(wallToRemove, false);

        ArrayList<Directions> directionsLeft = new ArrayList<>();
        directionsLeft.add(Directions.NORTH);
        directionsLeft.add(Directions.EAST);
        directionsLeft.add(Directions.SOUTH);
        directionsLeft.add(Directions.WEST);

        int nextDirectionToVisit;
        while(!directionsLeft.isEmpty()) {
            nextDirectionToVisit = random.nextInt(directionsLeft.size());
            switch(directionsLeft.get(nextDirectionToVisit)) {
                case NORTH:
                    if(y != 0 && !maze[x][y-1].getVisited()) {
                        maze[x][y].setIsWallPresent(Directions.NORTH, false);
                        visitTile(x, y-1, Directions.SOUTH);
                    }
                    directionsLeft.remove(nextDirectionToVisit);
                    break;
                case EAST:
                    if(x != (sizeX - 1) && !maze[x+1][y].getVisited()) {
                        maze[x][y].setIsWallPresent(Directions.EAST, false);
                        visitTile(x+1, y, Directions.WEST);
                    }
                    directionsLeft.remove(nextDirectionToVisit);
                    break;
                case SOUTH:
                    if(y != (sizeY - 1) && !maze[x][y+1].getVisited()) {
                        maze[x][y].setIsWallPresent(Directions.SOUTH, false);
                        visitTile(x, y+1, Directions.NORTH);
                    }
                    directionsLeft.remove(nextDirectionToVisit);
                    break;
                case WEST:
                    if(x != 0 && !maze[x-1][y].getVisited()) {
                        maze[x][y].setIsWallPresent(Directions.WEST, false);
                        visitTile(x-1, y, Directions.EAST);
                    }
                    directionsLeft.remove(nextDirectionToVisit);
                    break;
                case NONE: break;
                default: break;
            }
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getMazeTileWidthAndHeight() {
        return mazeTileWidthAndHeight;
    }

    public MazeTile getMazeTileAt(int x, int y) {
        return maze[x][y];
    }

    public MazeTile getMazeTileAt(double x, double y) {
        int xInt = (int) x;
        int yInt = (int) y;
        return maze[xInt][yInt];
    }

    public MazeTile[][] getMaze() {
        return maze;
    }
}
