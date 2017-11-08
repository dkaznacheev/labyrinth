package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import ru.spbau.labyrinth.model.Model;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final OuterScrollView outerScrollView = (OuterScrollView) findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        final DirectionChooseView moveDirectionChooseView = (DirectionChooseView) findViewById(R.id.moveDirView);
        final DirectionChooseView shootDirectionChooseView = (DirectionChooseView) findViewById(R.id.shootDirView);
        outerScrollView.horizontalScrollView = horizontalScrollView;

        Button nextTurnButton = (Button) findViewById(R.id.nextTurnButton);
        final FieldView fieldView = (FieldView) findViewById(R.id.fieldView);
        final TextView textView = (TextView) findViewById(R.id.textView2);
        final Model model = new Model();
        fieldView.updatePlayer(model.demoInit());

        nextTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.Player player = model.processTurn(moveDirectionChooseView.getDirection(), shootDirectionChooseView.getDirection());
                fieldView.updatePlayer(player);
                textView.setText(Integer.toString(player.getCartridgesCnt()));
                moveDirectionChooseView.resetDirection();
                shootDirectionChooseView.resetDirection();
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
