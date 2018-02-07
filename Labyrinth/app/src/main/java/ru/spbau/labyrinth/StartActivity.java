package ru.spbau.labyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private static final int PLAYERNAMES_REQUEST = 1;
    private final static String PREFS_NAME = "LocalSave";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAYERNAMES_REQUEST) {
            if (data == null || resultCode != RESULT_OK) {
                return;
            }
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtras(data);
            intent.putExtra("isNewGame", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button launchButton = findViewById(R.id.startButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InitActivity.class);
                startActivityForResult(intent, PLAYERNAMES_REQUEST);
            }
        });
        Button loadButton = findViewById(R.id.loadButton);

        loadButton.setEnabled(isSavedGame());

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("isNewGame", false);
                startActivity(intent);
            }
        });
        Button editorButton = findViewById(R.id.editorButton);
        editorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        Button multiplayerButton = findViewById(R.id.multiplayerButton);
        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MultiplayerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Button loadButton = findViewById(R.id.loadButton);
        loadButton.setEnabled(isSavedGame());
    }

    private boolean isSavedGame() {
        SharedPreferences savedGame = getSharedPreferences(PREFS_NAME, 0);
        return savedGame.getBoolean("saved", false);
    }

}