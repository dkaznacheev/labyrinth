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
    
    private GameState state;

    private int currentDrawnPlayerNum;
    private final int[] backgrounds = {
            R.drawable.labyrinth_red,
            R.drawable.labyrinth_blue,
            R.drawable.labyrinth_green,
            R.drawable.labyrinth_yellow};

    private void updatePlayerView(boolean scroll) {

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final PlayerFieldView fieldView = findViewById(R.id.fieldView);
        final TextView cartridgesTextView = findViewById(R.id.cartridgesTextView);
        final TextView currentPlayerNameTextView = findViewById(R.id.currentPlayerName);

        Model.Player player = state.getPlayers()[currentDrawnPlayerNum];
        cartridgesTextView.setText(Integer.toString(player.getCartridgesCnt()));
        currentPlayerNameTextView.setText(state.getNames()[currentDrawnPlayerNum]);

        moveDirectionChooseView.setPlayerNum(state.getCurrentPlayerNum());
        shootDirectionChooseView.setPlayerNum(state.getCurrentPlayerNum());
        
        if (currentDrawnPlayerNum == state.getCurrentPlayerNum()) {
            cartridgesTextView.setTypeface(null, Typeface.BOLD);
            currentPlayerNameTextView.setTypeface(null, Typeface.BOLD);
            fieldView.updatePlayer(player, moveDirectionChooseView.getDirection(), shootDirectionChooseView.getDirection());
        } else {
            cartridgesTextView.setTypeface(null, Typeface.NORMAL);
            currentPlayerNameTextView.setTypeface(null, Typeface.NORMAL);
            fieldView.updatePlayer(player, Model.Direction.NONE, Model.Direction.NONE);
        }
        int toScrollX = 3 + (player.getX() - player.getInitialX());
        int toScrollY = 3 + (player.getY() - player.getInitialY());
        if (scroll) {
            outerScrollView.scrollTo(0, toScrollY * 200 + 50);
            horizontalScrollView.scrollTo(toScrollX * 200 + 50, 0);
        }
    }

    private void finishGame(int winner) {
        clearSave();

        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("winnerName", state.getNames()[winner]);
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
        treasureTextView.setText(state.getTreasureOwner() + " has the treasure");
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
        final Button nextTurnButton = findViewById(R.id.nextTurnButton);
        final ImageView backgroundImageView = findViewById(R.id.imageView);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("isNewGame", true)) {
            state = new GameState(intent);
        } else {
            state = GameState.deserialize(
                    getSharedPreferences(PREFS_NAME, 0)
                    .getString("gameState", null));
        }
        if (state == null) {
            finish();
            return;
        }
        currentDrawnPlayerNum = state.getCurrentPlayerNum();
        
        backgroundImageView.setImageResource(
                backgrounds[state.getCurrentPlayerNum()]);//-
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
                        shootDirectionChooseView.getDirection(), state.getCurrentPlayerNum());

                moveDirectionChooseView.resetDirection();
                shootDirectionChooseView.resetDirection();

                int turnResult = state.updateTurn(turn);
                if (turnResult != -1) {
                    finishGame(turnResult);
                }

                printTreasureOwner();
                backgroundImageView.setImageResource(backgrounds[state.getCurrentPlayerNum()]);
                currentDrawnPlayerNum = state.getCurrentPlayerNum();
                updatePlayerView(true);
            }
        });

        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            intent.putExtra("log", Log.serialize(state.getLog()));
            startActivity(intent);
            }
        });

        Button prevPlayerButton = findViewById(R.id.prevPlayerButton);
        prevPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum--;
                if (currentDrawnPlayerNum < 0) {
                    currentDrawnPlayerNum = state.getPlayerNum() - 1;
                }

                updatePlayerView(true);
            }
        });

        Button nextPlayerButton = findViewById(R.id.nextPlayerButton);
        nextPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum++;
                if (currentDrawnPlayerNum >= state.getPlayerNum()) {
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
