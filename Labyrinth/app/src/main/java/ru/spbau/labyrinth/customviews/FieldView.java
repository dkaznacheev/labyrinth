package ru.spbau.labyrinth.customviews;

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

import ru.spbau.labyrinth.R;
import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.Model.Direction;
import ru.spbau.labyrinth.model.Model.Player;
import ru.spbau.labyrinth.model.field.Field;

public class FieldView extends View {
    private static final int FIELD_WIDTH = 2000;
    private static final int FIELD_HEIGHT = 2000;
    protected static final int CELL_SIZE = 200;
    protected int offsetX;
    protected int offsetY;
    private Paint paint;

    public float scrolledY = 0;

    protected Field field;

    public void scrollY (float y) {
        scrolledY = y;
    }

    private void init() {
        paint = new Paint();
    }

    public FieldView(Context context) {
        super(context);
        init();
    }

    public FieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(FIELD_WIDTH, FIELD_HEIGHT);
    }

    private void drawCells(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        int fieldSize = field.getSize();
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j <  fieldSize; j++) {
                int cellX = j + offsetX;
                int cellY = i + offsetY;
                switch (field.getState(j, i)) {
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
        
        int fieldSize = field.getSize();
        
        for (int i = 0; i < fieldSize + 1; i++) {
            for (int j = 0; j < fieldSize; j++) {

                int cellX = j + offsetX;
                int cellY = i + offsetY;
                if (field.hasBorderX(i, j)) {
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
                if (field.hasBorderY(i, j)) {
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCells(canvas);
        drawGrid(canvas);
        drawWalls(canvas);
    }

}
