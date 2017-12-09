package ru.spbau.labyrinth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.Model.Direction;
import ru.spbau.labyrinth.model.Model.Player;

public class FieldView extends View {
    private static final int FIELD_WIDTH = 2000;
    private static final int FIELD_HEIGHT = 2000;
    private static final int CELL_SIZE = 200;
    private static final int MAZE_OFFSET_X = 5;
    private static final int MAZE_OFFSET_Y = 5;
    private int offsetX;
    private int offsetY;
    private Paint paint;
    private Direction moveDir = Direction.NONE;
    private Direction shootDir = Direction.NONE;

    public float scrolledY = 0;

    private final static int playerColors[] = new int[]{Color.RED,
                                                        Color.BLUE,
                                                        Color.GREEN,
                                                        Color.YELLOW};

    Player myPlayer;

    public void scrollY (float y) {
        scrolledY = y;
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
                        //processClick(event.getX(), event.getY() + scrolledY);
                        invalidate();
                    }
                    break;
                }
            }
            return true;
        }
    };

    /*private void initGame() {
        model.demoInit();
        myPlayer = model.processTurn(Direction.NONE, Direction.NONE);
    }*/

    {
        paint = new Paint();
        setOnTouchListener(touchListener);
        //initGame();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(FIELD_WIDTH, FIELD_HEIGHT);
    }

    private void drawCells(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        int fieldSize = 5;
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j <  fieldSize; j++) {
                int cellX = j + offsetX;
                int cellY = i + offsetY;
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

    private void drawWalls(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(6);
        
        int fieldSize = 5;
        
        for (int i = 0; i < fieldSize + 1; i++) {
            for (int j = 0; j < fieldSize; j++) {

                int cellX = j + offsetX;
                int cellY = i + offsetY;
                if (myPlayer.getFieldBorderX(i, j)) {
                    canvas.drawLine(CELL_SIZE * cellX,
                            CELL_SIZE * cellY,
                            CELL_SIZE * (cellX + 1),
                            CELL_SIZE * cellY,
                            paint);
                }
            }
        }

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize + 1; j++) {

                int cellX = j + offsetX;
                int cellY = i + offsetY;
                if (myPlayer.getFieldBorderY(i, j)) {
                    canvas.drawLine(CELL_SIZE * cellX,
                            CELL_SIZE * cellY,
                            CELL_SIZE * cellX,
                            CELL_SIZE * (cellY + 1),
                            paint);
                }
            }
        }

    }

    private void drawBackground(Canvas canvas) {
        paint.setColor(Color.rgb(230, 230, 230)); // light-gray
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    private void drawPlayer(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(playerColors[myPlayer.getId()]);

        int playerX = myPlayer.getX();
        int playerY = myPlayer.getY();
        canvas.drawCircle(CELL_SIZE * (offsetX + playerX) + CELL_SIZE / 2,
                          CELL_SIZE * (offsetY + playerY) + CELL_SIZE / 2,
                          CELL_SIZE/3, paint);
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
        drawWalls(canvas);
        drawMove(canvas);
        drawPlayer(canvas);

    }

    private void drawShoot(Canvas canvas) {
    }

    private void drawMove(Canvas canvas) {
        float startX, startY, endX, endY;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(7);
        switch (moveDir) {
            case LEFT: {
                startX = CELL_SIZE * (offsetX + myPlayer.getX()) + CELL_SIZE / 2;
                startY = CELL_SIZE * (offsetY + myPlayer.getY()) + CELL_SIZE / 2;
                endX = startX - CELL_SIZE;
                endY = startY;
                break;
            }
            case RIGHT: {
                startX = CELL_SIZE * (offsetX + myPlayer.getX()) + CELL_SIZE / 2;
                startY = CELL_SIZE * (offsetY + myPlayer.getY()) + CELL_SIZE / 2;
                endX = startX + CELL_SIZE;
                endY = startY;
                break;
            }
            case UP: {
                startX = CELL_SIZE * (offsetX + myPlayer.getX()) + CELL_SIZE / 2;
                startY = CELL_SIZE * (offsetY + myPlayer.getY()) + CELL_SIZE / 2;
                endX = startX;
                endY = startY - CELL_SIZE;
                break;
            }
            case DOWN: {
                startX = CELL_SIZE * (offsetX + myPlayer.getX()) + CELL_SIZE / 2;
                startY = CELL_SIZE * (offsetY + myPlayer.getY()) + CELL_SIZE / 2;
                endX = startX;
                endY = startY + CELL_SIZE;
                break;
            }
            default: {
                startX = CELL_SIZE * (offsetX + myPlayer.getX()) + CELL_SIZE / 2;
                startY = CELL_SIZE * (offsetY + myPlayer.getY()) + CELL_SIZE / 2;
                endX = startX;
                endY = startY;
            }
        }
        canvas.drawLine(startX, startY, endX, endY, paint);
        float ax, ay, bx, by, cx, cy;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        float d = 10;
        switch (moveDir) {
            case LEFT: {
                ax = endX - CELL_SIZE / d; ay = endY;
                bx = endX + CELL_SIZE / d; by = endY - CELL_SIZE / d;
                cx = endX + CELL_SIZE / d; cy = endY + CELL_SIZE / d;
                break;
            }
            case RIGHT: {
                ax = endX + CELL_SIZE / d; ay = endY;
                bx = endX - CELL_SIZE / d; by = endY - CELL_SIZE / d;
                cx = endX - CELL_SIZE / d; cy = endY + CELL_SIZE / d;
                break;
            }
            case UP: {
                ax = endX; ay = endY - CELL_SIZE / d;
                bx = endX - CELL_SIZE / d; by = endY + CELL_SIZE / d;
                cx = endX + CELL_SIZE / d; cy = endY + CELL_SIZE / d;
                break;
            }
            case DOWN: {
                ax = endX; ay = endY + CELL_SIZE / d;
                bx = endX - CELL_SIZE / d; by = endY - CELL_SIZE / d;
                cx = endX + CELL_SIZE / d; cy = endY - CELL_SIZE / d;
                break;
            }
            default: {
                ax = 0; ay = 0; bx = 0; by = 0; cx = 0; cy = 0;
            }
        }
        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        path.lineTo(cx, cy);
        path.close();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, paint);

    }

    public void updatePlayer(Player newPlayer, Model.Direction moveDir, Model.Direction shootDir) {
        myPlayer = newPlayer;
        this.moveDir = moveDir;
        this.shootDir = shootDir;
        offsetX = MAZE_OFFSET_X - myPlayer.getInitialX();
        offsetY = MAZE_OFFSET_Y - myPlayer.getInitialY();
        invalidate();
    }

}
