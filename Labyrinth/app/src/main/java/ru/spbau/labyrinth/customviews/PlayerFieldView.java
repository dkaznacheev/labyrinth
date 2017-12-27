package ru.spbau.labyrinth.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.Model.Direction;
import ru.spbau.labyrinth.model.Model.Player;

public class PlayerFieldView extends FieldView {

    protected static final int MAZE_OFFSET_X = 5;
    protected static final int MAZE_OFFSET_Y = 5;
    private Paint paint;
    private Direction moveDir = Direction.NONE;
    private Direction shootDir = Direction.NONE;
    public float scrolledY = 0;

    private final static int playerColors[] = new int[]{Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW};

    protected Player myPlayer;

    public void scrollY (float y) {
        scrolledY = y;
    }

    private void init() {
        paint = new Paint();
    }

    public PlayerFieldView(Context context) {
        super(context);
        init();
    }

    public PlayerFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMove(canvas);
        drawShoot(canvas);
        drawPlayer(canvas);
    }

    private void drawShoot(Canvas canvas) {
        float startX, startY, endX, endY;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(7);
        switch (shootDir) {
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

        int dashes = 8;
        for (int i = 0; i < dashes; i++) {
            if (i % 2 == 0) {
                canvas.drawLine(
                        startX + (endX - startX) * (float)i / (float)dashes,
                        startY + (endY - startY) * (float)i / (float)dashes,
                        startX + (endX - startX) * (float)(i + 1) / (float)dashes,
                        startY + (endY - startY) * (float)(i + 1) / (float)dashes, paint);
            }
        }

        float ax, ay, bx, by, cx, cy;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        float d = 10;
        switch (shootDir) {
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
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
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
        field = myPlayer.getField();
        updateTreasure(field.getTreasureX(), field.getTreasureY());
        this.moveDir = moveDir;
        this.shootDir = shootDir;
        offsetX = MAZE_OFFSET_X - myPlayer.getInitialX();
        offsetY = MAZE_OFFSET_Y - myPlayer.getInitialY();
        invalidate();
    }

}
