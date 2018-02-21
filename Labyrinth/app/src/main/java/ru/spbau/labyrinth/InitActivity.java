package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class InitActivity extends AppCompatActivity {
    private static final int MAZE_REQUEST = 2;
    private int playerNum = MIN_PLAYERS;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;
    private EditText[] playerEdits;
    private String[] names = new String[playerNum];

    private String[] getNames() {
        for (int i = 0; i < playerNum; i++) {
            names[i] = playerEdits[i].getText().toString();

            if (names[i].equals("")) {
                return null;
            }

            for (int j = 0; j < i; j++) {
                if (names[i].equals(names[j])) {
                    return null;
                }
            }
        }
        return names;
    }

    private EditText constructEditText(int num) {
        EditText editText = new EditText(this.getBaseContext());
        editText.setHint("Name...");
        editText.setText("Player " + Integer.toString(num));
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                               LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return editText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        final Button addButton = findViewById(R.id.addPlayerButton);
        final Button deleteButton = findViewById(R.id.deletePlayerButton);
        final Button startButton = findViewById(R.id.startButton);

        final LinearLayout playersLayout = findViewById(R.id.playersLayout);

        playerEdits = new EditText[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++) {
            playerEdits[i] = constructEditText(i + 1);
            if (i > MIN_PLAYERS - 1) {
                playerEdits[i].setVisibility(View.GONE);
            }
            playersLayout.addView(playerEdits[i]);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerNum < MAX_PLAYERS) {
                    playerEdits[playerNum].setVisibility(View.VISIBLE);
                    playerEdits[playerNum].setText("Player " + Integer.toString(playerNum + 1));
                    playerNum++;
                    deleteButton.setVisibility(View.VISIBLE);
                }
                if (playerNum == MAX_PLAYERS) {
                    addButton.setVisibility(View.GONE);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerNum > MIN_PLAYERS) {
                    playerNum--;
                    playerEdits[playerNum].setText("");
                    playerEdits[playerNum].setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                }
                if (playerNum == MIN_PLAYERS) {
                    deleteButton.setVisibility(View.GONE);
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] names = getNames();
                if (names == null) {
                    return;
                }
                Intent intent = new Intent(InitActivity.this, LevelSelectActivity.class);
                startActivityForResult(intent, MAZE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAZE_REQUEST) {
            if (data == null || resultCode != RESULT_OK) {
                return;
            }
            String maze = data.getStringExtra("maze");
            if (maze == null) {
                return;
            }

            data.putExtra("playerNum", playerNum);
            for (int i = 0; i < playerNum; i++) {
                data.putExtra("player"+Integer.toString(i), names[i]);
            }

            setResult(RESULT_OK, data);
            finish();
        }

    }
}
