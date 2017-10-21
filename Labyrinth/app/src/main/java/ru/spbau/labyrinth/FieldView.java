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

public class FieldView extends View {
    private static final int FIELD_WIDTH = 2000;
    private static final int FIELD_HEIGHT = 2000;
    private static final int CELL_SIZE = 200;
    private static final int MAZE_OFFSET_X = 3;
    private static final int MAZE_OFFSET_Y = 3;
    private Paint paint;
    public float scrolledY = 0;

    enum Cell {FREE, RED};
    Maze maze;

    class Maze {
        Cell[][] cells;
        boolean[][] horizontalWalls;
        boolean[][] verticalWalls;
        static final int MAZE_SIZE = 3;

        public Maze() {
            cells = new Cell[MAZE_SIZE][MAZE_SIZE];

            for (int i = 0; i < MAZE_SIZE; i++)
                for (int j = 0; j < MAZE_SIZE; j++)
                    cells[i][j] = Cell.FREE;
            cells[1][1] = Cell.RED;


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

    public void scrollY (float y) {
        scrolledY = y;
    }

    void processClick(float x, float y) {
        addDot(x, y);
    }

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
                        processClick(event.getX(), event.getY() + scrolledY);
                        invalidate();
                    }
                    break;
                }
            }
            return true;
        }
    };

    {
        paint = new Paint();
        setOnTouchListener(touchListener);
        points = new ArrayList<>();
        maze = new Maze();
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

    public void addDot(float x, float y) {
        points.add(new Point(x, y));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(FIELD_WIDTH, FIELD_HEIGHT);
    }

    private void drawCells(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < Maze.MAZE_SIZE; i++)
            for (int j = 0; j <  Maze.MAZE_SIZE; j++) {
                int cellX = j + MAZE_OFFSET_X;
                int cellY = i + MAZE_OFFSET_Y;
                switch (maze.cells[i][j]) {
                    case FREE: {
                        paint.setColor(Color.WHITE);

                        canvas.drawRect(CELL_SIZE * cellX,
                                CELL_SIZE * cellY,
                                CELL_SIZE * (cellX + 1),
                                CELL_SIZE * (cellY + 1),
                                paint);
                        break;
                    }
                    case RED: {
                        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.bull);
                        Bitmap.createScaledBitmap(bitmapSource, CELL_SIZE, CELL_SIZE, true);
                        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapSource, CELL_SIZE, CELL_SIZE, true),
                                CELL_SIZE * cellX, CELL_SIZE * cellY, paint);
                        break;
                    }
                }

            }
    }

    private void drawWalls(Canvas canvas) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.rgb(230, 230, 230)); // light-gray
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        drawCells(canvas);

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
        for (int i = 0; i < points.size(); i++) {
            canvas.drawCircle(points.get(i).x, points.get(i).y, 10, paint);
        }

        drawWalls(canvas);

    }


}
