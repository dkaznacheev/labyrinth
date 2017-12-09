package ru.spbau.labyrinth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import ru.spbau.labyrinth.model.Model;

public class MainActivity extends AppCompatActivity {
    final  int PLAYER_NUM = 2;

    private Model.Player[] players;
    private Model.Turn[] turns;
    private String[] names;
    private int currentPlayerNum = 0;
    private int currentDrawnPlayerNum = 0;

    private void setPlayerView(FieldView fieldView,
                               TextView cartridgesTextView,
                               TextView currentPlayerNameTextView,
                               OuterScrollView outerScrollView,
                               HorizontalScrollView horizontalScrollView) {
        fieldView.updatePlayer(players[currentDrawnPlayerNum]);

        cartridgesTextView.setText(Integer.toString(players[currentDrawnPlayerNum].getCartridgesCnt()));
        currentPlayerNameTextView.setText(names[currentDrawnPlayerNum]);
        if (currentDrawnPlayerNum == currentPlayerNum) {
            cartridgesTextView.setTypeface(null, Typeface.BOLD);
            currentPlayerNameTextView.setTypeface(null, Typeface.BOLD);
        } else {
            cartridgesTextView.setTypeface(null, Typeface.NORMAL);
            currentPlayerNameTextView.setTypeface(null, Typeface.NORMAL);
        }
        int toScrollX = 3 + (players[currentDrawnPlayerNum].getX() - players[currentDrawnPlayerNum].getInitialX());
        int toScrollY = 3 + (players[currentDrawnPlayerNum].getY() - players[currentDrawnPlayerNum].getInitialY());
        outerScrollView.scrollTo(0, toScrollY * 200 + 50);
        horizontalScrollView.scrollTo(toScrollX * 200 + 50, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        outerScrollView.horizontalScrollView = horizontalScrollView;

        final DirectionChooseView moveDirectionChooseView = (DirectionChooseView) findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = (DirectionChooseView) findViewById(R.id.shootDirView);

        Button nextTurnButton = (Button) findViewById(R.id.nextTurnButton);
        final FieldView fieldView = (FieldView) findViewById(R.id.fieldView);
        final TextView cartridgesTextView = (TextView) findViewById(R.id.cartridgesTextView);
        final TextView currentPlayerNameTextView = (TextView) findViewById(R.id.currentPlayerName);

        names = new String[PLAYER_NUM];
        names[0] = "Player 1";
        names[1] = "Player 2";

        final Model model = new Model();
        players = model.init(names, 5);

        setPlayerView(fieldView,
                cartridgesTextView,
                currentPlayerNameTextView,
                outerScrollView,
                horizontalScrollView);
        //fieldView.updatePlayer(model.demoInit());
        turns = new Model.Turn[PLAYER_NUM];

        nextTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.Turn turn = new Model.Turn(moveDirectionChooseView.getDirection(),
                        shootDirectionChooseView.getDirection(), currentPlayerNum);

                moveDirectionChooseView.resetDirection();
                shootDirectionChooseView.resetDirection();

                turns[currentPlayerNum] = turn;

                currentPlayerNum++;
                if (currentPlayerNum == PLAYER_NUM) {
                    players = model.processTurnMuliplayer(turns);
                    turns = new Model.Turn[PLAYER_NUM];
                    currentPlayerNum = 0;
                }
                //Model.Player player = model.demoProcessTurn(moveDirectionChooseView.getDirection(), shootDirectionChooseView.getDirection());
                //fieldView.updatePlayer(player);
                //textView.setText(Integer.toString(player.getCartridgesCnt()));

                currentDrawnPlayerNum = currentPlayerNum;
                setPlayerView(fieldView,
                        cartridgesTextView,
                        currentPlayerNameTextView,
                        outerScrollView,
                        horizontalScrollView);            }
        });

        Button logButton = (Button) findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });

        Button prevPlayerButton = (Button) findViewById(R.id.prevPlayerButton);
        prevPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum--;
                if (currentDrawnPlayerNum < 0) {
                    currentDrawnPlayerNum = PLAYER_NUM - 1;
                }

                setPlayerView(fieldView,
                        cartridgesTextView,
                        currentPlayerNameTextView,
                        outerScrollView,
                        horizontalScrollView);            }
        });

        Button nextPlayerButton = (Button) findViewById(R.id.nextPlayerButton);
        nextPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDrawnPlayerNum++;
                if (currentDrawnPlayerNum >= PLAYER_NUM) {
                    currentDrawnPlayerNum = 0;
                }

                setPlayerView(fieldView,
                              cartridgesTextView,
                              currentPlayerNameTextView,
                              outerScrollView,
                              horizontalScrollView);
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

}
