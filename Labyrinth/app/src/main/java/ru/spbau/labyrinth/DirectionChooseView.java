package ru.spbau.labyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.EnumMap;
import java.util.Map;

public class DirectionChooseView extends View {
    public enum Direction {UP, DOWN, LEFT, RIGHT, NONE}
    private Map<Direction, Integer> positionX;
    private Map<Direction, Integer> positionY;

    private Direction chosen;
    private Paint paint;

    {
        positionX = new EnumMap<>(Direction.class);
        positionY = new EnumMap<>(Direction.class);
        positionX.put(Direction.UP, 1);
        positionY.put(Direction.UP, 0);
        positionX.put(Direction.LEFT, 0);
        positionY.put(Direction.LEFT, 1);
        positionX.put(Direction.NONE, 1);
        positionY.put(Direction.NONE, 1);
        positionX.put(Direction.RIGHT, 2);
        positionY.put(Direction.RIGHT, 1);
        positionX.put(Direction.DOWN, 1);
        positionY.put(Direction.DOWN, 2);
        paint = new Paint();
        chosen = Direction.NONE;
    }

    public DirectionChooseView(Context context) {
        super(context);
    }

    public DirectionChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void resetDirection() {
        chosen = Direction.NONE;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float HEIGHT = getHeight();
        float WIDTH = getWidth();

        for (Direction dir : Direction.values()) {
            if (dir == chosen) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.GRAY);
            }
            int x = positionX.get(dir);
            int y = positionY.get(dir);
            canvas.drawRect(x * WIDTH / 3, y * HEIGHT / 3, (x + 1) * WIDTH / 3, (y + 1) * HEIGHT / 3, paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float HEIGHT = getHeight();
        float WIDTH = getWidth();
        float x = event.getX();
        float y = event.getY();
        boolean wasChosen = false;
        Direction dirChosen = Direction.NONE;
        for (Direction dir : Direction.values()) {
            int dx = positionX.get(dir);
            int dy = positionY.get(dir);
            if  (   x >= dx * WIDTH / 3 &&
                    y >= dy * HEIGHT / 3 &&
                    x <= (dx + 1)* WIDTH / 3 &&
                    y <= (dy + 1)* HEIGHT / 3) {
                wasChosen = true;
                dirChosen = dir;
            }
        }
        if (wasChosen) {
            chosen = dirChosen;
        }
        invalidate();
        return super.onTouchEvent(event);
    }
}
