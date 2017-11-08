package ru.spbau.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;

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

        nextTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fieldView.clearDots();
                fieldView.nextTurn(moveDirectionChooseView.getDirection(), shootDirectionChooseView.getDirection());
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
                outerScrollView.scrollTo(0, 450);
            }
        });
        horizontalScrollView.post(new Runnable() {
            public void run() {
                horizontalScrollView.scrollTo(450, 0);
            }
        });

    }

}
