package ru.spbau.labyrinth.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;


import com.google.gson.Gson;

import ru.spbau.labyrinth.R;
import ru.spbau.labyrinth.model.field.Field;
import ru.spbau.labyrinth.model.field.Field.State;


public class EditFieldView extends FieldView {
    private static final int PRECISION = 35;
    private State cellState = State.NOTHING;
    private Paint paint;

    public EditFieldView(Context context) {
        super(context);
        init();
    }

    private void init() {
        field = new Field(10);
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


    void chooseState(int x, int y) {
        final int fx = x;
        final int fy = y;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View dialogView = inflater.inflate(R.layout.dialog_choosecell, null);
        dialogBuilder.setView(dialogView);

        ImageButton bullButton = (ImageButton)dialogView.findViewById(R.id.bullButton);
        ImageButton nothingButton = (ImageButton)dialogView.findViewById(R.id.nothingButton);
        ImageButton hospitalButton = (ImageButton)dialogView.findViewById(R.id.hospitalButton);
        ImageButton treasureButton = (ImageButton)dialogView.findViewById(R.id.treasureButton);

        final AlertDialog alertDialog = dialogBuilder.create();

        bullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.setState(fx, fy, State.MINOTAUR);
                invalidate();
                alertDialog.dismiss();
            }
        });

        nothingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.setState(fx, fy, State.NOTHING);
                invalidate();
                alertDialog.dismiss();
            }
        });

        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < field.getSize(); i++)
                    for (int j = 0; j < field.getSize(); j++) {
                        if (field.getState(i, j) == State.HOSPITAL)
                            field.setState(i, j, State.NOTHING);
                    }
                field.setState(fx, fy, State.HOSPITAL);
                invalidate();
                alertDialog.dismiss();
            }
        });

        treasureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field.setTreasurePos(fx, fy);
                updateTreasure(fx, fy);
                invalidate();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    void processClick(float x, float y) {
        if (touchedCell(x, y)) {
            int fx = (int) x / CELL_SIZE;
            int fy = (int) y / CELL_SIZE;
            chooseState(fx, fy);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public static String serialize(EditFieldView editFieldView) {
        return Field.serialize(editFieldView.getField());
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        invalidate();
    }
}
