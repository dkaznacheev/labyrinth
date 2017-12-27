package ru.spbau.labyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import ru.spbau.labyrinth.customviews.EditFieldView;
import ru.spbau.labyrinth.customviews.OuterScrollView;
import ru.spbau.labyrinth.model.field.Field;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    EditFieldView editFieldView;

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
        loadButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.saveButton:
                cv.put("object", Field.serialize(editFieldView.getField()));
                long rowID = db.insert("mytable", null, cv);
                break;
            case R.id.loadButton:
                Cursor c = db.query("mytable", null, null, null, null, null, null);

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
                }
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
}
