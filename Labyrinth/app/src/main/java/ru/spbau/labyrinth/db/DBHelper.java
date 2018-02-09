package ru.spbau.labyrinth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.labyrinth.model.field.Field;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Mazes DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table mytable (id integer primary key autoincrement," +
                "object text, name text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long saveField(Field field, String name) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase database = getWritableDatabase();

        contentValues.put("object", Field.serialize(field));
        contentValues.put("name", name);
        long rowID = database.insert(
                "mytable",
                null,
                contentValues);
        return rowID;
    }

    public List<String> getAllSavedMazesNames() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> mazesNames = new ArrayList<>();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            while (true) {
                String name = c.getString(c.getColumnIndex("name"));
                mazesNames.add(name);
                if (!c.moveToNext()) {
                    break;
                }
            }
        }

        close();
        return mazesNames;
    }

    public ArrayList<Integer> getAllSavedMazesIds() {
        SQLiteDatabase db = getWritableDatabase();

        ArrayList<Integer> ids = new ArrayList<>();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            while (true) {
                int id = c.getInt(c.getColumnIndex("id"));
                ids.add(id);
                if (!c.moveToNext()) {
                    break;
                }
            }
        }

        close();
        return ids;
    }

    public void deleteMazeById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("mytable", "id="+Integer.toString(id), null);
    }


    public String findMazeById(int id) {
        SQLiteDatabase db = getWritableDatabase();

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

        close();
        return json;
    }
}