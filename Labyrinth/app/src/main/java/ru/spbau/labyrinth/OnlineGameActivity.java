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
    private MultiplayerMatch match = MultiplayerMatch.getInstance();

    @Override
    protected void finishGame(int winner) {
        super.finishGame(winner);
    }

    @Override
    protected void initializeGameState() {
        Intent intent = getIntent();
        intent.putExtra("playerNum", match.turnBasedMatch.getParticipantIds().size());
        match.onInitiateMatch(match.turnBasedMatch);
        if (match.turnBasedMatch.getData() == null) {
            state = new GameState(intent);
        } else {
            state = GameState.deserialize(new String(match.turnBasedMatch.getData()));
        }

        if (state == null) {
            finish();
            return;
        }

        currentDrawnPlayerNum = match.getPlayersNumber();
        if (currentDrawnPlayerNum == -1) {
            finish();
            return;
        }

        final ImageView backgroundImageView = findViewById(R.id.background);
        backgroundImageView.setImageResource(backgrounds[state.getCurrentPlayerNum()]);
    }

    @Override
    protected void processNextTurn(Model.Turn turn) {
        if (!match.isPlayersTurn()) {
            return;
        }

        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final ImageView backgroundImageView = findViewById(R.id.background);

        moveDirectionChooseView.resetDirection();
        shootDirectionChooseView.resetDirection();

        int turnResult = state.updateTurn(turn);
        if (turnResult != -1) {
            match.finish();
            return;
        }
        match.sendData(state.serialize().getBytes());
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

    public void applyData(byte[] data) {
        if (data == null) {
            match.onInitiateMatch(match.turnBasedMatch);
            return;
        }
        state = GameState.deserialize(new String(data));
    }
}
