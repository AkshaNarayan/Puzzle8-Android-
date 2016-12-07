package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth)
    {
        tiles = new ArrayList<>();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        int baseWidth = parentWidth / NUM_TILES;
        int twiceBaseWidth = baseWidth * 2;

        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, 0, 0, baseWidth, baseWidth),0));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, baseWidth,0, baseWidth, baseWidth),1));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, twiceBaseWidth,0,baseWidth,baseWidth),2));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap,0, baseWidth, baseWidth, baseWidth),3));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, baseWidth, baseWidth, baseWidth, baseWidth),4));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, twiceBaseWidth, baseWidth, baseWidth,baseWidth),5));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, 0, twiceBaseWidth, baseWidth, baseWidth),6));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, baseWidth, twiceBaseWidth, baseWidth, baseWidth),7));
        tiles.add(null);
    }

    PuzzleBoard(PuzzleBoard otherBoard)
    {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    PuzzleBoard(PuzzleBoard otherBoard, int steps) {
        previousBoard = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        this.steps = steps + 1;
    }


    public void reset() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        return null;
    }

    public int priority()
    {
        int dis = 0;
        for(int i = 0;i<NUM_TILES * NUM_TILES;i++){
            PuzzleTile tile = tiles.get(i);
            if(tile != null){
                int correctPosition = tile.getNumber();
                int correctX = correctPosition%NUM_TILES;
                int correctY = correctPosition/NUM_TILES;
                int currentX = i%NUM_TILES;
                int currentY = i/NUM_TILES;
                dis = dis + (Math.abs(currentX - correctX)) + Math.abs(currentY - correctY);
            }
        }
        return dis + steps;

    }

    public PuzzleBoard getPreviousBoard() { return previousBoard;} // returns previous state

    public void setPreviousBoard(PuzzleBoard previousBoard) { this.previousBoard = previousBoard;}//update the state with the state of the argument passed

}
