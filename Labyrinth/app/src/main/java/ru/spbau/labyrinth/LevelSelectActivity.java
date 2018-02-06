package ru.spbau.labyrinth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LevelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);


        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> mazes = new ArrayList<>();
        mazes.addAll(EditorActivity.getAllSavedMazesNames());
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mazes);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditorActivity.setEditFieldView(EditorActivity.findMazeById(Integer.parseInt(mazes.get(i))));
                LevelSelectActivity.this.finish();
            }
        });
    }
}
