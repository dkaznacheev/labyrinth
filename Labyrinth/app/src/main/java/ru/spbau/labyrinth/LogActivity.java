package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.spbau.labyrinth.model.Log;
import ru.spbau.labyrinth.model.Model.Direction;
import ru.spbau.labyrinth.model.Model.Turn;

public class LogActivity extends AppCompatActivity {
    private final static int playerColors[] = new int[]{
            R.color.player_red,
            R.color.player_blue,
            R.color.player_green,
            R.color.player_yellow
    };

    private final static Map<Direction, Character> dirChars = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TableLayout table = findViewById(R.id.table);

        Intent intent = getIntent();
        String json = intent.getStringExtra("log");
        Log gameLog = Log.deserialize(json);

        dirChars.put(Direction.UP, '↑');
        dirChars.put(Direction.DOWN, '↓');
        dirChars.put(Direction.RIGHT, '→');
        dirChars.put(Direction.LEFT, '←');
        dirChars.put(Direction.NONE, '⋅');

        for (int i = 0; i < gameLog.getTurnsNum(); i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < gameLog.getPlayerNum(); j++) {
                TextView textView = new TextView(this);
                Turn turn = gameLog.getTurn(i, j);
                textView.setTextSize(20);
                textView.setText(String.format("M: %s, S:  %s", dirChars.get(turn.getMoveDir()), dirChars.get(turn.getShootDir())));
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setBackgroundResource(playerColors[j]);
                row.addView(textView);
            }
            table.addView(row);
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}