package ru.spbau.labyrinth.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import ru.spbau.labyrinth.R;

public class OuterScrollView extends ScrollView {
    public HorizontalScrollView horizontalScrollView;

    public void init() {
        post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView = (HorizontalScrollView) getChildAt(0);
            }
        });
    }

    public OuterScrollView(Context context) {
        super(context);
        init();
    }

    public OuterScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OuterScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        FieldView fieldView = findViewById(R.id.fieldView);
        fieldView.scrollY(this.getScrollY());
        horizontalScrollView.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        horizontalScrollView.dispatchTouchEvent(ev);
        return true;
    }
}
