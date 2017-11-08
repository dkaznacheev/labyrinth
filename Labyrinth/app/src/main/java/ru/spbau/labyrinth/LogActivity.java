package ru.spbau.labyrinth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {
    class Move {
        DirectionChooseView.Direction moveDirection;
        DirectionChooseView.Direction shootDirection;

        public Move(DirectionChooseView.Direction moveDirection,
                    DirectionChooseView.Direction shootDirection) {
            this.moveDirection = moveDirection;
            this.shootDirection = shootDirection;
        }
    }

    class Log {

        public final int players;

        private List<Move[]> moves;

        public Log(int players) {
            this.players = players;
            moves = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Move[] turn = new Move[players];
                for (int j = 0; j < players; j++) {
                    turn[j] = new Move(DirectionChooseView.Direction.UP,
                            DirectionChooseView.Direction.UP);
                }
                moves.add(turn);
            }
        }

        public Move getMove(int turn, int player) {
            return moves.get(turn)[player];
        }

        public int getTurns() {
            return moves.size();
        }
    }

    Log gameLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TableLayout table = (TableLayout) findViewById(R.id.table);
        gameLog = new Log(3);
        for (int i = 0; i < gameLog.getTurns(); i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < gameLog.players; j++) {
                TextView textView = new TextView(this);
                Move move = gameLog.getMove(i, j);
                textView.setText(String.format("s:%s, m:%s", move.moveDirection.name(), move.shootDirection.name()));
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                row.addView(textView);
            }
            table.addView(row);
        }
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}