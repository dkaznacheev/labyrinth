package ru.spbau.labyrinth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import ru.spbau.labyrinth.model.field.Field.*;
import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.Model.Direction;
import ru.spbau.labyrinth.model.Model.Player;

public class FieldView extends View {
    private static final int FIELD_WIDTH = 2000;
    private static final int FIELD_HEIGHT = 2000;
    private static final int CELL_SIZE = 200;
    private static final int MAZE_OFFSET_X = 3;
    private static final int MAZE_OFFSET_Y = 3;
    private Paint paint;
    public float scrolledY = 0;

    //Maze maze;

    Model model;
    Player myPlayer;

    /*lass Maze {
        State[][] cells;
        boolean[][] horizontalWalls;
        boolean[][] verticalWalls;
        static final int MAZE_SIZE = 3;

        public Maze() {
            cells = new State[MAZE_SIZE][MAZE_SIZE];

            for (int i = 0; i < MAZE_SIZE; i++)
                for (int j = 0; j < MAZE_SIZE; j++)
                    cells[i][j] = State.NOTHING;
            cells[1][1] = State.MINOTAUR;

            horizontalWalls = new boolean[MAZE_SIZE + 1][MAZE_SIZE];
            verticalWalls = new boolean[MAZE_SIZE][MAZE_SIZE + 1];

            horizontalWalls[0][0] = true;
            horizontalWalls[0][1] = true;
            horizontalWalls[0][2] = true;
            horizontalWalls[1][1] = true;
            horizontalWalls[2][2] = true;
            horizontalWalls[3][0] = true;
            horizontalWalls[3][1] = true;
            horizontalWalls[3][2] = true;

            verticalWalls[0][0] = true;
            verticalWalls[1][0] = true;
            verticalWalls[2][0] = true;
            verticalWalls[1][1] = true;
            verticalWalls[2][1] = true;
            verticalWalls[0][3] = true;
            verticalWalls[1][3] = true;
        }
    }
    */
/*
    class Point {
        float x;
        float y;
        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<Point> points;

    public void clearDots() {
        points.clear();
        invalidate();
    }
*/
    public void scrollY (float y) {
        scrolledY = y;
    }

    /*void processClick(float x, float y) {
        addDot(x, y);
    }*/

    OnTouchListener touchListener = new OnTouchListener() {
        float startX = 0, startY = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    startX = event.getX();
                    startY = event.getY();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if ((startX - event.getX()) < 5 &&
                            (startY - event.getY()) < 5) {
                        //processClick(event.getX(), event.getY() + scrolledY);
                        invalidate();
                    }
                    break;
                }
            }
            return true;
        }
    };

    private void initGame() {
        model.demoInit();
        myPlayer = model.processTurn(Direction.NONE, Direction.NONE);
    }

    {
        paint = new Paint();
        setOnTouchListener(touchListener);
        //points = new ArrayList<>();
        //maze = new Maze();
        model = new Model();
        initGame();
    }

    public FieldView(Context context) {
        super(context);
    }

    public FieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*public void addDot(float x, float y) {
        points.add(new Point(x, y));
    }
*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(FIELD_WIDTH, FIELD_HEIGHT);
    }

    private void drawCells(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        int fieldSize = 5;
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j <  fieldSize; j++) {
                int cellX = j + MAZE_OFFSET_X;
                int cellY = i + MAZE_OFFSET_Y;
                switch (myPlayer.getFieldState(j, i)) {
                    case NOTHING: {
                        paint.setColor(Color.WHITE);

                        canvas.drawRect(CELL_SIZE * cellX,
                                CELL_SIZE * cellY,
                                CELL_SIZE * (cellX + 1),
                                CELL_SIZE * (cellY + 1),
                                paint);
                        break;
                    }
                    case UNKNOWN: {
                        paint.setColor(Color.rgb(230, 230, 230));

                        canvas.drawRect(CELL_SIZE * cellX,
                                CELL_SIZE * cellY,
                                CELL_SIZE * (cellX + 1),
                                CELL_SIZE * (cellY + 1),
                                paint);
                        break;
                    }
                    case MINOTAUR: {
                        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.bull);
                        Bitmap.createScaledBitmap(bitmapSource, CELL_SIZE, CELL_SIZE, true);
                        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapSource, CELL_SIZE, CELL_SIZE, true),
                                CELL_SIZE * cellX, CELL_SIZE * cellY, paint);
                        break;
                    }
                }

            }
    }

    /*private void drawWalls(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(6);

        for (int i = 0; i < Maze.MAZE_SIZE + 1; i++) {
            for (int j = 0; j < Maze.MAZE_SIZE; j++) {

                int cellX = j + MAZE_OFFSET_X;
                int cellY = i + MAZE_OFFSET_Y;
                if (maze.horizontalWalls[i][j]) {
                    canvas.drawLine(CELL_SIZE * cellX,
                            CELL_SIZE * cellY,
                            CELL_SIZE * (cellX + 1),
                            CELL_SIZE * cellY,
                            paint);
                }
            }
        }

        for (int i = 0; i < Maze.MAZE_SIZE; i++) {
            for (int j = 0; j < Maze.MAZE_SIZE + 1; j++) {

                int cellX = j + MAZE_OFFSET_X;
                int cellY = i + MAZE_OFFSET_Y;
                if (maze.verticalWalls[i][j]) {
                    canvas.drawLine(CELL_SIZE * cellX,
                            CELL_SIZE * cellY,
                            CELL_SIZE * cellX,
                            CELL_SIZE * (cellY + 1),
                            paint);
                }
            }
        }

    }
*/
    private void drawBackground(Canvas canvas) {
        paint.setColor(Color.rgb(230, 230, 230)); // light-gray
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    private void drawPlayer(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        int playerX = myPlayer.getX();
        int playerY = myPlayer.getY();
        canvas.drawCircle(CELL_SIZE * (MAZE_OFFSET_X + playerX) + CELL_SIZE / 2,
                          CELL_SIZE * (MAZE_OFFSET_Y + playerY) + CELL_SIZE / 2, CELL_SIZE/3, paint);
    }

    private void drawGrid(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(200, 200, 200));

        for (int i = 0; i * CELL_SIZE < FIELD_HEIGHT; i++) {
            canvas.drawLine(0, i * CELL_SIZE, getWidth(), i * CELL_SIZE, paint);
        }
        for (int i = 0; i * CELL_SIZE < FIELD_WIDTH; i++) {
            canvas.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getHeight(), paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        /*for (int i = 0; i < points.size(); i++) {
            canvas.drawCircle(points.get(i).x, points.get(i).y, 10, paint);
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCells(canvas);
        drawGrid(canvas);
        //drawWalls(canvas);
        drawPlayer(canvas);
    }

    public void nextTurn(Direction moveDirection, Direction shootDirection) {
        myPlayer = model.processTurn(moveDirection, shootDirection);
        invalidate();
    }

}
