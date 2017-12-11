package ru.spbau.labyrinth.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ru.spbau.labyrinth.model.field.Field;


public class EditFieldView extends FieldView {
    private static final int PRECISION = 35;

    private static class Point {
        float x;
        float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<Point> points;
    private Paint paint;

    public EditFieldView(Context context) {
        super(context);
        init();
    }

    private void init() {
        field = new Field(10);
        points = new ArrayList<>();
        paint = new Paint();
        setOnTouchListener(touchListener);
    }

    public EditFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    boolean touchedCell(float x, float y) {
        int dx = (int) x % CELL_SIZE;
        int dy = (int) y % CELL_SIZE;
        return (dx > PRECISION  && dx < CELL_SIZE - PRECISION) && (dy > PRECISION  && dy < CELL_SIZE - PRECISION);
    }

    boolean touchedVerticalWall(float x, float y) {
        int dx = (int) x % CELL_SIZE;
        int dy = (int) y % CELL_SIZE;
        return (dx < PRECISION  || dx > CELL_SIZE - PRECISION) && (dy > PRECISION  && dy < CELL_SIZE - PRECISION);
    }

    private boolean touchedHorizontalWall(float x, float y) {
        int dx = (int) x % CELL_SIZE;
        int dy = (int) y % CELL_SIZE;
        return (dx > PRECISION  && dx < CELL_SIZE - PRECISION) && (dy < PRECISION  || dy > CELL_SIZE - PRECISION);
    }


    void processClick(float x, float y) {

        points.add(new Point(x, y));
        if (touchedCell(x, y)) {
            int fx = (int) x / CELL_SIZE;
            int fy = (int) y / CELL_SIZE;
            if (field.getState(fx, fy) == Field.State.NOTHING)
                field.setState(fx, fy, Field.State.MINOTAUR);
            else
                field.setState(fx, fy, Field.State.NOTHING);
            //Toast.makeText(getContext(), Integer.toString(fx) + " " + Integer.toString(fy), Toast.LENGTH_SHORT).show();

        }
        else if (touchedVerticalWall(x, y)){
            int wx = ((int) x + PRECISION + 1) / CELL_SIZE;
            int wy = ((int) y + PRECISION + 1)/ CELL_SIZE;
            field.setBorderY(wy, wx, !field.hasBorderY(wy, wx));
        }
        else if (touchedHorizontalWall(x, y)){
            int wx = ((int) x + PRECISION + 1) / CELL_SIZE;
            int wy = ((int) y + PRECISION + 1)/ CELL_SIZE;
            field.setBorderX(wy, wx, !field.hasBorderX(wy, wx));

        }

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

    private void drawDots(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        for (int i = 0; i < points.size(); i++)
            canvas.drawCircle(points.get(i).x, points.get(i).y, 10, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDots(canvas);
    }
}
