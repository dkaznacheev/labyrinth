package ru.spbau.labyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;

import ru.spbau.labyrinth.customviews.DirectionChooseView;
import ru.spbau.labyrinth.model.GameState;
import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.networkMultiplayer.MultiplayerMatch;

public class OnlineGameActivity extends GameActivity {
    private final static String PREFS_NAME = "LocalSave";
    private MultiplayerMatch match;

    @Override
    protected void finishGame(int winner) {
        super.finishGame(winner);
    }

    @Override
    protected void initializeGameState() {

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

        SharedPreferences savedGame = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = savedGame.edit();

        editor.putString("gameState", state.serialize());
        editor.putBoolean("saved", true);

        editor.commit();
    }
}
