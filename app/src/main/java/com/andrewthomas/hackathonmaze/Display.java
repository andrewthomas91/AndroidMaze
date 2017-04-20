package com.andrewthomas.hackathonmaze;

public class Display {
    public Display(){
    }

//    public Graphics drawMaze(MazeGenerator maze, Graphics2D g2){
//        int tileSize = maze.getMazeTileWidthAndHeight();
//        for(int x = 0; x < maze.getSizeX(); x++) {
//            for (int y = 0; y < maze.getSizeY(); y++) {
//                drawMazeTile(maze.getMazeTileAt(x,y), tileSize, g2);
//            }
//        }
//        return g2;
//    }
//
//    public void drawMazeTile(MazeTile mazeTile, int tileWidthAndHeight, Graphics2D g2) {
//        int wallThickness = 1;
//        int x = mazeTile.getLocationX() + 2;
//        int y = mazeTile.getLocationY() + 2;
//
//        int topLeftX = (x * tileWidthAndHeight) - (tileWidthAndHeight / 2);
//        int topLeftY = (y * tileWidthAndHeight) - (tileWidthAndHeight / 2);
//
//        int topRightX = (x * tileWidthAndHeight) + (tileWidthAndHeight / 2);
//        int topRightY = (y * tileWidthAndHeight) - (tileWidthAndHeight / 2);
//
//        int bottomLeftX = (x * tileWidthAndHeight) - (tileWidthAndHeight / 2);
//        int bottomLeftY = (y * tileWidthAndHeight) + (tileWidthAndHeight / 2);
//
//        g2.setColor(Color.BLACK);
//        if(mazeTile.getIsWallPresent(Directions.NORTH)) {
//            g2.fillRect(topLeftX, topLeftY, tileWidthAndHeight, wallThickness);
//        }
//        if(mazeTile.getIsWallPresent(Directions.EAST)) {
//            g2.fillRect(topRightX, topRightY, wallThickness, tileWidthAndHeight);
//        }
//        if(mazeTile.getIsWallPresent(Directions.SOUTH)) {
//            g2.fillRect(bottomLeftX, bottomLeftY, tileWidthAndHeight, wallThickness);
//        }
//        if(mazeTile.getIsWallPresent(Directions.WEST)) {
//            g2.fillRect(topLeftX, topLeftY, wallThickness, tileWidthAndHeight);
//        }
//    }
//
//    public void drawRunner(double x, double y, Graphics2D g2) {
//        int centerX = (int) x;
//        int centerY = (int) y;
//
//        g2.setColor(Color.RED);
//        g2.fillRect(centerX - 3, centerY - 3, 6, 6);
//    }
//
//    public void drawFinish(double locationX, double locationY, int tileWidthAndHeight, Graphics2D g2) {
//        double x = locationX + 2;
//        double y = locationY + 2;
//
//        int centerX = (int) (x * tileWidthAndHeight);
//        int centerY = (int) (y * tileWidthAndHeight);
//
//        g2.setColor(Color.BLUE);
//        g2.fillRect(centerX - 5, centerY - 5, 10, 10);
//    }
//
//    public Graphics clear(Graphics2D g2){
//        g2.setColor(Color.WHITE);
//        g2.fillRect(0, 0, 750, 750);
//        return g2;
//    }
}
