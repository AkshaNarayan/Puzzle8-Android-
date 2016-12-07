package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();
    private Comparator<PuzzleBoard> comp = new Comparator<PuzzleBoard>() {
        @Override
        public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
            return lhs.priority() - rhs.priority();
        }
    };

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Job Done Homie! :P", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Wow You're smart, congrats!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            for(int i = 0;i<NUM_SHUFFLE_STEPS;i++){
                ArrayList<PuzzleBoard> neighbours = puzzleBoard.neighbours();
                int randomInt = random.nextInt(neighbours.size());
                puzzleBoard = neighbours.get(randomInt);
            }
            invalidate();
        }
    }

    public void solve() {
        PriorityQueue<PuzzleBoard> Q = new PriorityQueue<>(1,comp); // new queue
        PuzzleBoard current = new PuzzleBoard(puzzleBoard, -1); // extracting current state
        current.setPreviousBoard(null); // setting previous state of the current state to null
        Q.add(current); // adding the current stat to the queue
        while (!Q.isEmpty()){ //till Q is empty
            PuzzleBoard State = Q.poll(); // removing the top element
            if(State.resolved()){
                ArrayList<PuzzleBoard> steps = new ArrayList<>(); // creating an array to store each state
                while (State.getPreviousBoard() != null){
                    steps.add(State);
                    State = State.getPreviousBoard();
                }
                Collections.reverse(steps); //gets the steps in the right order
                animation = steps;
                invalidate();
                break;
            }
            else {
                Q.addAll(State.neighbours());
            }
        }
    }



}
