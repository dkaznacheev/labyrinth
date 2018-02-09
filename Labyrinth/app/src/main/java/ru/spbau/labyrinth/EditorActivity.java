package ru.spbau.labyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

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

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        editFieldView = findViewById(R.id.fieldView);

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

        Button saveButton = findViewById(R.id.saveButton);
        Button loadButton = findViewById(R.id.loadButton);

        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    private void saveMaze(final Field field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setHint("Maze name...");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input.getText().toString();
                if (!name.equals("")) {
                    long rowID = dbHelper.saveField(field, name);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton: {
                saveMaze(editFieldView.getField());
                break;
            }
            case R.id.loadButton: {
                Intent intent = new Intent(EditorActivity.this, LevelSelectActivity.class);
                startActivity(intent);
                break;
            }
        }
        dbHelper.close();
    }

    public static void setEditFieldView(String json) {
        editFieldView.setField(Field.deserialize(json));
    }
}
