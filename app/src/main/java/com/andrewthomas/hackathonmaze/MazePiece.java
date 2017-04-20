package com.andrewthomas.hackathonmaze;

import java.util.Random;

public class MazePiece {
    private Random random = new Random();
    private int locationX, locationY;
    private int tileSize;
    private Directions direction = Directions.NONE;

    public MazePiece(int locationX, int locationY, int tileSize) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.tileSize = tileSize;
    }

    public boolean isOnSameLocationAs(MazePiece otherPiece) {
        return this.locationX == otherPiece.getLocationX() && this.locationY == otherPiece.getLocationY();
    }

    public void generateNewFinishLocation(int mazeSizeX, int mazeSizeY) {
        locationX = random.nextInt(mazeSizeX / 2) + mazeSizeX / 4;
        locationY = random.nextInt(mazeSizeY / 2) + mazeSizeY / 4;
    }

    public boolean move(MazeGenerator maze, Directions direction) {
        if(direction == Directions.WEST) {
            if(locationX != 0 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.WEST)) {
                setLocationX(getLocationX() - 1);
                setDirection(Directions.WEST);
                return true;
            }
        }
        if(direction == Directions.EAST) {
            if(locationX != maze.getSizeX() - 1 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.EAST)) {
                setLocationX(getLocationX() + 1);
                setDirection(Directions.EAST);
                return true;
            }
        }
        if(direction == Directions.NORTH) {
            if(locationY != 0 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.NORTH)) {
                setLocationY(getLocationY() - 1);
                setDirection(Directions.NORTH);
                return true;
            }
        }
        if(direction == Directions.SOUTH) {
            if(locationY != maze.getSizeY() - 1 && !maze.getMazeTileAt(locationX, locationY).getIsWallPresent(Directions.SOUTH)) {
                setLocationY(getLocationY() + 1);
                setDirection(Directions.SOUTH);
                return true;
            }
        }
        return false;
    }

    public int convertCoordinateToLocation(double coordinate, int tileSize) {
        return (int) ((coordinate + tileSize / 2) / tileSize) - 1;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public int getCoordinateX() {
        return (locationX + 1) * tileSize;
    }

    public int getCoordinateY() {
        return (locationY + 1) * tileSize;
    }

    public Directions getDirection() {
        return direction;
    }
}
