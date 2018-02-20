package ru.spbau.labyrinth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.spbau.labyrinth.customviews.DirectionChooseView;
import ru.spbau.labyrinth.customviews.OuterScrollView;
import ru.spbau.labyrinth.customviews.PlayerFieldView;
import ru.spbau.labyrinth.model.GameState;
import ru.spbau.labyrinth.model.Log;
import ru.spbau.labyrinth.model.Model;

public abstract class GameActivity extends AppCompatActivity {
    protected GameState state;

    protected int currentDrawnPlayerNum;
    protected final static int[] backgrounds = {
            R.drawable.labyrinth_red,
            R.drawable.labyrinth_blue,
            R.drawable.labyrinth_green,
            R.drawable.labyrinth_yellow};

    protected void updatePlayerView(boolean scroll) {
        printTreasureOwner();
        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final PlayerFieldView fieldView = findViewById(R.id.fieldView);
        final TextView cartridgesTextView = findViewById(R.id.cartridgesTextView);
        final TextView currentPlayerNameTextView = findViewById(R.id.currentPlayerName);

        Model.Player player = state.getPlayers()[currentDrawnPlayerNum];
        cartridgesTextView.setText(Integer.toString(player.getCartridgesCnt()));
        currentPlayerNameTextView.setText(player.getName());

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

    protected void finishGame(int winner) {
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("winnerName", state.getPlayers()[winner].getName());
        intent.putExtra("winnerId", winner);
        finish();
        startActivity(intent);
    }

    private void printTreasureOwner() {
        final TextView treasureTextView = findViewById(R.id.treasureTextView);
        treasureTextView.setText(state.getTreasureOwner() + " has the treasure");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        //outerScrollView.horizontalScrollView = horizontalScrollView;
        final DirectionChooseView moveDirectionChooseView = findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = findViewById(R.id.shootDirView);
        final Button nextTurnButton = findViewById(R.id.nextTurnButton);

        initializeGameState();

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

                processNextTurn(turn);

                updatePlayerView(true);
            }
        });

        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (GameActivity.this instanceof OnlineGameActivity) {
                Toast.makeText(GameActivity.this, "Online", Toast.LENGTH_LONG).show();
                OnlineGameActivity onlineGameActivity = (OnlineGameActivity) GameActivity.this;
                onlineGameActivity.updateMatch();
            }
            Intent intent = new Intent(GameActivity.this, LogActivity.class);
            intent.putExtra("log", Log.serialize(state.log));
            startActivity(intent);
            }
        });

        Button prevPlayerButton = findViewById(R.id.prevPlayerButton);
        prevPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum--;
                if (currentDrawnPlayerNum < 0) {
                    currentDrawnPlayerNum = state.playerNum - 1;
                }

                updatePlayerView(true);
            }
        });

        Button nextPlayerButton = findViewById(R.id.nextPlayerButton);
        nextPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum++;
                if (currentDrawnPlayerNum >= state.playerNum) {
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

    protected abstract void initializeGameState();
    protected abstract void processNextTurn(Model.Turn turn);
}
