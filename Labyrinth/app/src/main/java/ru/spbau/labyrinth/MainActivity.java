package ru.spbau.labyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import ru.spbau.labyrinth.customviews.DirectionChooseView;
import ru.spbau.labyrinth.customviews.OuterScrollView;
import ru.spbau.labyrinth.customviews.PlayerFieldView;
import ru.spbau.labyrinth.model.GameState;
import ru.spbau.labyrinth.model.Log;
import ru.spbau.labyrinth.model.Model;

public class MainActivity extends AppCompatActivity {
    private final static String PREFS_NAME = "LocalSave";

    private int playerNum;
    private Model.Player[] players;
    private Model.Turn[] turns;
    private String[] names;
    private int currentPlayerNum;
    private int currentDrawnPlayerNum;
    private Model model;
    private final int[] backgrounds = {
            R.drawable.labyrinth_red,
            R.drawable.labyrinth_blue,
            R.drawable.labyrinth_green,
            R.drawable.labyrinth_yellow};
    private Log log;

    private void updatePlayerView(boolean scroll) {

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final PlayerFieldView fieldView = findViewById(R.id.fieldView);
        final TextView cartridgesTextView = findViewById(R.id.cartridgesTextView);
        final TextView currentPlayerNameTextView = findViewById(R.id.currentPlayerName);

        cartridgesTextView.setText(Integer.toString(players[currentDrawnPlayerNum].getCartridgesCnt()));
        currentPlayerNameTextView.setText(names[currentDrawnPlayerNum]);

        if (currentDrawnPlayerNum == currentPlayerNum) {
            cartridgesTextView.setTypeface(null, Typeface.BOLD);
            currentPlayerNameTextView.setTypeface(null, Typeface.BOLD);
            fieldView.updatePlayer(players[currentDrawnPlayerNum], moveDirectionChooseView.getDirection(), shootDirectionChooseView.getDirection());
        } else {
            cartridgesTextView.setTypeface(null, Typeface.NORMAL);
            currentPlayerNameTextView.setTypeface(null, Typeface.NORMAL);
            fieldView.updatePlayer(players[currentDrawnPlayerNum], Model.Direction.NONE, Model.Direction.NONE);
        }
        int toScrollX = 3 + (players[currentDrawnPlayerNum].getX() - players[currentDrawnPlayerNum].getInitialX());
        int toScrollY = 3 + (players[currentDrawnPlayerNum].getY() - players[currentDrawnPlayerNum].getInitialY());
        if (scroll) {
            outerScrollView.scrollTo(0, toScrollY * 200 + 50);
            horizontalScrollView.scrollTo(toScrollX * 200 + 50, 0);
        }
    }

    private void finishGame(int winner) {
        clearSave();

        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("winnerName", names[winner]);
        intent.putExtra("winnerId", winner);
        finish();
        startActivity(intent);
    }

    private void clearSave() {
        SharedPreferences savedGame = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = savedGame.edit();
        editor.clear();
        editor.putBoolean("saved", false);
        editor.commit();
    }

    private void printTreasureOwner() {
        final TextView treasureTextView = findViewById(R.id.treasureTextView);
        int treasureOwner = model.getTreasureOwner();
        String owner = "Nobody";
        if (treasureOwner != -1) {
            owner = names[treasureOwner];
        }
        treasureTextView.setText(owner + " has the treasure");
    }

    private void updateTurn(Model.Turn turn) {
        turns[currentPlayerNum] = turn;

        currentPlayerNum++;
        if (currentPlayerNum == playerNum) {
            log.addRound(turns);
            players = model.processTurnMultiplayer(turns);
            if (model.getWinner() != -1) {
                finishGame(model.getWinner());
            }
            turns = new Model.Turn[playerNum];
            currentPlayerNum = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        outerScrollView.horizontalScrollView = horizontalScrollView;

        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);

        Button nextTurnButton = findViewById(R.id.nextTurnButton);

        final ImageView backgroundImageView = findViewById(R.id.imageView);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("isNewGame", true)) {
            initializeGameState();
        } else {
            SharedPreferences savedGame = getSharedPreferences(PREFS_NAME, 0);
            String save = savedGame.getString("gameState", null);
            GameState state = GameState.deserialize(save);
            if (state == null) {
                finish();
                return;
            }
            loadGameFromState(state);
        }
        currentDrawnPlayerNum = currentPlayerNum;
        
        backgroundImageView.setImageResource(backgrounds[currentPlayerNum]);
        updatePlayerView(true);

        moveDirectionChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlayerView(false);
            }
        });

        shootDirectionChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlayerView(false);
            }
        });

        nextTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.Turn turn = new Model.Turn(moveDirectionChooseView.getDirection(),
                        shootDirectionChooseView.getDirection(), currentPlayerNum);

                moveDirectionChooseView.resetDirection();
                shootDirectionChooseView.resetDirection();

                updateTurn(turn);

                printTreasureOwner();
                backgroundImageView.setImageResource(backgrounds[currentPlayerNum]);
                moveDirectionChooseView.setPlayerNum(currentPlayerNum);
                shootDirectionChooseView.setPlayerNum(currentPlayerNum);
                currentDrawnPlayerNum = currentPlayerNum;
                updatePlayerView(true);
            }
        });

        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            intent.putExtra("log", Log.serialize(log));
            startActivity(intent);
            }
        });

        Button prevPlayerButton = findViewById(R.id.prevPlayerButton);
        prevPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum--;
                if (currentDrawnPlayerNum < 0) {
                    currentDrawnPlayerNum = playerNum - 1;
                }

                updatePlayerView(true);
            }
        });

        Button nextPlayerButton = findViewById(R.id.nextPlayerButton);
        nextPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum++;
                if (currentDrawnPlayerNum >= playerNum) {
                    currentDrawnPlayerNum = 0;
                }

                updatePlayerView(true);
            }
        });

        outerScrollView.post(new Runnable() {
            public void run() {
                outerScrollView.scrollTo(0, 650);
            }
        });
        horizontalScrollView.post(new Runnable() {
            public void run() {
                horizontalScrollView.scrollTo(650, 0);
            }
        });

    }

    private void loadGameFromState(GameState state) {
        model = state.getModel();
        players = state.getPlayers();
        playerNum = players.length;
        names = state.getNames();
        turns = state.getTurns();
        log = state.getLog();
        currentPlayerNum = state.getCurrentPlayerNum();
    }

    private void initializeGameState() {
        loadPlayersFromIntent(getIntent());
        model = new Model();
        players = model.init(names, 3);
        turns = new Model.Turn[playerNum];
        log = new Log(playerNum);
        currentPlayerNum = 0;
    }

    private void loadPlayersFromIntent(Intent data) {
        playerNum = data.getIntExtra("playerNum", 0);

        names = new String[playerNum];
        for (int i = 0; i < playerNum; i++) {
            names[i] = data.getStringExtra("player"+Integer.toString(i));
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences savedGame = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = savedGame.edit();

        GameState save = new GameState(model, players, names, turns, log, currentPlayerNum);
        editor.putString("gameState", save.toString());
        editor.putBoolean("saved", true);

        editor.commit();
    }
}
