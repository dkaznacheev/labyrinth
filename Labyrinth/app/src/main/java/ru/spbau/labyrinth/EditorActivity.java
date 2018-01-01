package ru.spbau.labyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.labyrinth.customviews.EditFieldView;
import ru.spbau.labyrinth.customviews.FieldView;
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
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.saveButton:
                contentValues.put("object", Field.serialize(editFieldView.getField()));
                long rowID = database.insert("mytable", null, contentValues);
                break;
                /*Cursor c = db.query("mytable", null, null, null, null, null, null);

                if (c.moveToFirst()) {
                    while (!c.isLast()) {
                        c.moveToNext();
                    }
                    int ind = c.getColumnIndex("id");
                    int str = c.getColumnIndex("object");
                    String json = c.getString(str);
                    editFieldView.setField(Field.deserialize(json));
                } else {
                    Toast toast = Toast.makeText(this, "No one level is saved.", Toast.LENGTH_LONG);
                    toast.show();
                }*/
        }

        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "Mazes DB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table mytable (id integer primary key autoincrement," +
            "object text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public static List<String> getAllSavedMazesNames() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<String> mazesNames = new ArrayList<>();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            while (true) {
                int ind = c.getInt(c.getColumnIndex("id"));
                mazesNames.add(Integer.toString(ind));
                if (!c.moveToNext()) {
                    break;
                }
            }
        }

        dbHelper.close();
        return mazesNames;
    }

    public static void setEditFieldView(String json) {
        editFieldView.setField(Field.deserialize(json));
    }

    public static String findMazeById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String json = null;
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            while (true) {
                int ind = c.getInt(c.getColumnIndex("id"));
                if (ind == id) {
                    json = c.getString(c.getColumnIndex("object"));
                }
                if (!c.moveToNext()) {
                    break;
                }
            }
        }

        dbHelper.close();
        return json;
    }
}
