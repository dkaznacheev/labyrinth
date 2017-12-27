package ru.spbau.labyrinth;

import android.content.Intent;
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
import ru.spbau.labyrinth.model.Model;

public class MainActivity extends AppCompatActivity {

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

    private void setPlayerView(boolean scroll) {

        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        final DirectionChooseView moveDirectionChooseView = (DirectionChooseView) findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = (DirectionChooseView) findViewById(R.id.shootDirView);
        final PlayerFieldView fieldView = (PlayerFieldView) findViewById(R.id.fieldView);
        final TextView cartridgesTextView = (TextView) findViewById(R.id.cartridgesTextView);
        final TextView currentPlayerNameTextView = (TextView) findViewById(R.id.currentPlayerName);

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

    private void killActivity() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            killActivity();
            return;
        }

        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        outerScrollView.horizontalScrollView = horizontalScrollView;

        final DirectionChooseView moveDirectionChooseView = (DirectionChooseView) findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = (DirectionChooseView) findViewById(R.id.shootDirView);

        Button nextTurnButton = (Button) findViewById(R.id.nextTurnButton);

        final ImageView backgroundImageView = (ImageView) findViewById(R.id.imageView);

        playerNum = data.getIntExtra("playerNum", 0);

        String name = data.getStringExtra("player"+Integer.toString(0));
        names = new String[playerNum];
        for (int i = 0; i < playerNum; i++) {
            names[i] = data.getStringExtra("player"+Integer.toString(i));
        }

        model = new Model();
        players = model.init(names, 3);

        turns = new Model.Turn[playerNum];

        currentPlayerNum = 0;
        currentDrawnPlayerNum = 0;
        backgroundImageView.setImageResource(backgrounds[currentPlayerNum]);
        setPlayerView(true);

        moveDirectionChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayerView(false);
            }
        });


        shootDirectionChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayerView(false);
            }
        });

        nextTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.Turn turn = new Model.Turn(moveDirectionChooseView.getDirection(),
                        shootDirectionChooseView.getDirection(), currentPlayerNum);

                moveDirectionChooseView.resetDirection();
                shootDirectionChooseView.resetDirection();

                turns[currentPlayerNum] = turn;

                currentPlayerNum++;
                if (currentPlayerNum == playerNum) {
                    players = model.processTurnMultiplayer(turns);
                    if (model.getWinner() != -1) {
                        finishGame(model.getWinner());
                    }
                    turns = new Model.Turn[playerNum];
                    currentPlayerNum = 0;

                }

                printTreasureOwner();
                backgroundImageView.setImageResource(backgrounds[currentPlayerNum]);
                moveDirectionChooseView.setPlayerNum(currentPlayerNum);
                shootDirectionChooseView.setPlayerNum(currentPlayerNum);
                currentDrawnPlayerNum = currentPlayerNum;
                setPlayerView(true);
            }
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
                currentDrawnPlayerNum = playerNum - 1;
            }

            setPlayerView(true);
            }
        });

        Button nextPlayerButton = (Button) findViewById(R.id.nextPlayerButton);
        nextPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            currentDrawnPlayerNum++;
            if (currentDrawnPlayerNum >= playerNum) {
                currentDrawnPlayerNum = 0;
            }

            setPlayerView(true);
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

    private void finishGame(int winner) {
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("winnerName", names[winner]);
        intent.putExtra("winnerId", winner);
        finish();
        startActivity(intent);
    }

    private void printTreasureOwner() {
        final TextView treasureTextView = (TextView) findViewById(R.id.treasureTextView);
        int treasureOwner = model.getTreasureOwner();
        String owner = "Nobody";
        if (treasureOwner != -1) {
            owner = names[treasureOwner];
        }
        treasureTextView.setText(owner + " has the treasure");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, InitActivity.class);
            startActivityForResult(intent, 1);
        }

    }
}
