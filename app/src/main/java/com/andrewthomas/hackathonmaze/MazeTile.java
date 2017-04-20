package com.andrewthomas.hackathonmaze;

public class MazeTile {
    private boolean wallNorth, wallEast, wallSouth, wallWest, visited;
    private int locationX, locationY;

    public MazeTile(int locationX, int locationY) {
        wallNorth = true;
        wallEast = true;
        wallSouth = true;
        wallWest = true;
        visited = false;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public void setIsWallPresent(Directions direction, boolean isWallPresent) {
        switch(direction) {
            case NORTH: wallNorth = isWallPresent; break;
            case EAST: wallEast = isWallPresent; break;
            case SOUTH: wallSouth = isWallPresent; break;
            case WEST: wallWest = isWallPresent; break;
            case NONE: break;
            default: break;
        }
    }

    public void setVisited(boolean newValue) {
        visited = newValue;
    }

    public boolean getIsWallPresent(Directions direction) {
        switch(direction) {
            case NORTH: return wallNorth;
            case EAST: return wallEast;
            case SOUTH: return wallSouth;
            case WEST: return wallWest;
            default: return true;
        }
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public boolean getVisited() {
        return visited;
    }
}
