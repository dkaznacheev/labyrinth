package ru.spbau.labyrinth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.HorizontalScrollView;

import ru.spbau.labyrinth.customviews.EditFieldView;
import ru.spbau.labyrinth.customviews.OuterScrollView;

public class EditorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        outerScrollView.horizontalScrollView = horizontalScrollView;
        final EditFieldView editFieldView = (EditFieldView) findViewById(R.id.fieldView);

        outerScrollView.post(new Runnable() {
            public void run() {
                outerScrollView.scrollTo(0, 650);
            }
        });
        horizontalScrollView.post(new Runnable() {
            public void run() {
                horizontalScrollView.scrollTo(650, 0);
            }
        });
    }
}
