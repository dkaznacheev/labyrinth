package ru.spbau.labyrinth.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.spbau.labyrinth.model.field.Field;


public class LevelsDataBaseHelper extends SQLiteOpenHelper {
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE levels (\n" +
            "name text PRIMARY_KEY,\n" +
            "data text NOT NULL";
    private final static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS levels";

    public LevelsDataBaseHelper(Context context) {
        super(context, "levels.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public Field loadLevel(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT data FROM levels WHERE name=?", new String[] {name});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex("data"));
                return Field.deserialize(data);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public void saveLevel(String name, Field field) {
        String data = Field.serialize(field);

    }
}
