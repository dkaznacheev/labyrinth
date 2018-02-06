package ru.spbau.labyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import ru.spbau.labyrinth.customviews.EditFieldView;
import ru.spbau.labyrinth.db.DBHelper;
import ru.spbau.labyrinth.customviews.OuterScrollView;
import ru.spbau.labyrinth.model.field.Field;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    static DBHelper dbHelper;
    static EditFieldView editFieldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        outerScrollView.horizontalScrollView = horizontalScrollView;
        editFieldView = (EditFieldView) findViewById(R.id.fieldView);

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

        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button loadButton = (Button) findViewById(R.id.loadButton);

        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditorActivity.this, LevelSelectActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton: {
                long rowID = dbHelper.saveField(editFieldView.getField());
                break;
            }
        }
        dbHelper.close();
    }

    public static void setEditFieldView(String json) {
        editFieldView.setField(Field.deserialize(json));
    }
}
