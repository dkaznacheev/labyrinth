package ru.spbau.labyrinth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.spbau.labyrinth.db.DBHelper;

public class LevelSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);
        final DBHelper dbHelper = new DBHelper(this);

        ListView listView = findViewById(R.id.listView);
        final ArrayList<String> mazes = new ArrayList<>();
        mazes.addAll(dbHelper.getAllSavedMazesNames());
        final ArrayList<Integer> ids = new ArrayList<>();
        ids.addAll(dbHelper.getAllSavedMazesIds());
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mazes);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("maze", dbHelper.findMazeById(ids.get(i)));
                setResult(RESULT_OK, intent);
                LevelSelectActivity.this.finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LevelSelectActivity.this);
                builder.setMessage("Delete maze?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dbHelper.deleteMazeById(ids.get(i));
                        mazes.clear();
                        ids.clear();
                        mazes.addAll(dbHelper.getAllSavedMazesNames());
                        ids.addAll(dbHelper.getAllSavedMazesIds());
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            }
        });

    }
}
