package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {
    private final int[] backgrounds = {
            R.drawable.labyrinth_red,
            R.drawable.labyrinth_blue,
            R.drawable.labyrinth_green,
            R.drawable.labyrinth_yellow};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);
        Intent intent = getIntent();
        int winner = intent.getIntExtra("winnerId", 0);
        String name = intent.getStringExtra("winnerName");

        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageResource(backgrounds[winner]);
        TextView endTextView = (TextView) findViewById(R.id.endTextView);

        endTextView.setText("GAME OVER\nWinner:\n" + name);

        Button menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
