package ru.spbau.labyrinth.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import ru.spbau.labyrinth.R;
import ru.spbau.labyrinth.customviews.FieldView;

public class OuterScrollView extends ScrollView {
    public HorizontalScrollView horizontalScrollView;

    public OuterScrollView(Context context) {
        super(context);
    }

    public OuterScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OuterScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        FieldView fieldView = (FieldView) findViewById(R.id.fieldView);
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
