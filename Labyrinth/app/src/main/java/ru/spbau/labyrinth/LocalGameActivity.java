package ru.spbau.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;

import ru.spbau.labyrinth.customviews.DirectionChooseView;
import ru.spbau.labyrinth.model.GameState;
import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.field.Field;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LocalGameActivity extends GameActivity {
    private final static String PREFS_NAME = "LocalSave";

    @Override
    protected void finishGame(int winner) {
        super.finishGame(winner);
        clearSave();
    }

    private void clearSave() {
        SharedPreferences savedGame = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = savedGame.edit();
        editor.putBoolean("saved", false);
        editor.commit();
    }

    @Override
    protected void initializeGameState() {
        Intent intent = getIntent();

        if (intent.getBooleanExtra("isNewGame", true)) {
            int playerNum = intent.getIntExtra("playerNum", 0);
            String[] names = new String[playerNum];
            for (int i = 0; i < playerNum; i++) {
                names[i] = intent.getStringExtra("player" + Integer.toString(i));
            }
            String maze = intent.getStringExtra("maze");
            if (maze == null) {
                state = new GameState(names);
            } else {
                state = new GameState(names, Field.deserialize(maze));
            }
        } else {
            state = GameState.deserialize(
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString("gameState", null));
        }
        if (state == null) {
            finish();
            return;
        }
        currentDrawnPlayerNum = state.getCurrentPlayerNum();

        final ImageView backgroundImageView = findViewById(R.id.background);
        backgroundImageView.setImageResource(
                backgrounds[state.getCurrentPlayerNum()]);
    }

    @Override
    protected void processNextTurn(Model.Turn turn) {
        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final ImageView backgroundImageView = findViewById(R.id.background);

        moveDirectionChooseView.resetDirection();
        shootDirectionChooseView.resetDirection();

        int turnResult = state.updateTurn(turn);
        if (turnResult != -1) {
            finishGame(turnResult);
        }

        backgroundImageView.setImageResource(backgrounds[state.getCurrentPlayerNum()]);
        currentDrawnPlayerNum = state.getCurrentPlayerNum();

    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences savedGame = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = savedGame.edit();

        editor.putString("gameState", state.serialize());
        editor.putBoolean("saved", true);

        editor.commit();
    }
}
