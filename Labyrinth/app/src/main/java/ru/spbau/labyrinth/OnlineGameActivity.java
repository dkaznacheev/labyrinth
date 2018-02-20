package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import ru.spbau.labyrinth.customviews.DirectionChooseView;
import ru.spbau.labyrinth.model.GameState;
import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.networkMultiplayer.MultiplayerMatch;

public class OnlineGameActivity extends GameActivity {
    private final static String PREFS_NAME = "LocalSave";
    private MultiplayerMatch match = MultiplayerMatch.getInstance();
    private Thread checkingThread;

    @Override
    protected void finishGame(int winner) {
        super.finishGame(winner);
    }

    @Override
    protected void initializeGameState() {
        match.onInitiateMatch(match.turnBasedMatch);
        if (match.turnBasedMatch.getData() == null) {
            state = new GameState(new String[match.turnBasedMatch.getParticipantIds().size()]);
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

        final Handler myHandler = new Handler();
        final Button nextTurnButton = findViewById(R.id.nextTurnButton);
        checkingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OnlineGameActivity.this, "Starting thread.", Toast.LENGTH_LONG).show();
                try {
                    while (true) {
                        if (!Thread.interrupted()) {
                            synchronized (OnlineGameActivity.this) {
                                final boolean isPlayersTurn = OnlineGameActivity.this.match.isPlayersTurn();
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isPlayersTurn) {
                                            nextTurnButton.setEnabled(true);
                                        } else {
                                            nextTurnButton.setEnabled(false);
                                        }
                                        if (OnlineGameActivity.this.match.turnBasedMatch.getData() != null) {
                                            updateMatch();
                                        }

                                        //Toast.makeText(OnlineGameActivity.this, needToUpdateField ? "Up to date" : "Need to update", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Thread.sleep(1000);
                            }
                        } else {
                            break;
                        }
                    }
                } catch (InterruptedException e){
                }
            }
        });
        Toast.makeText(OnlineGameActivity.this, "Starting thread.", Toast.LENGTH_LONG).show();

        checkingThread.start();
    }

    @Override
    protected void processNextTurn(Model.Turn turn) {
        if (!match.isPlayersTurn()) {
            return;
        }

        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);

        moveDirectionChooseView.resetDirection();
        shootDirectionChooseView.resetDirection();

        int turnResult = state.updateTurn(turn);
        if (turnResult != -1) {
            match.finish();
            return;
        }
        match.sendData(state.serialize().getBytes());
        findViewById(R.id.nextTurnButton).setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        checkingThread.interrupt();
        try {
            checkingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        checkingThread.interrupt();
    }

    public void updateMatch() {
        match.update();
        state = GameState.deserialize(new String(match.turnBasedMatch.getData()));
        updatePlayerView(true);
    }
}
