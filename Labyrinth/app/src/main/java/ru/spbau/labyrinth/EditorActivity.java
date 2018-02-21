package ru.spbau.labyrinth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import ru.spbau.labyrinth.customviews.EditFieldView;
import ru.spbau.labyrinth.customviews.OuterScrollView;
import ru.spbau.labyrinth.db.DBHelper;
import ru.spbau.labyrinth.model.field.Field;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MAZE_REQUEST = 2;
    static DBHelper dbHelper;
    static EditFieldView editFieldView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAZE_REQUEST) {
            if (data == null || resultCode != RESULT_OK) {
                return;
            }
            String maze = data.getStringExtra("maze");
            if (maze != null) {
                setEditFieldView(maze);
                checkField();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        final OuterScrollView outerScrollView = findViewById(R.id.outerScroll);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScroll);
        editFieldView = findViewById(R.id.fieldView);

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

        Button saveButton = findViewById(R.id.saveButton);
        Button loadButton = findViewById(R.id.loadButton);
        Button checkButton = findViewById(R.id.checkButton);

        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    private void saveMaze(final Field field) {
        if (!checkField())
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setHint("Maze name...");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input.getText().toString();
                if (!name.equals("")) {
                    dbHelper.saveField(field, name);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton: {
                saveMaze(editFieldView.getField());
                break;
            }
            case R.id.loadButton: {
                Intent intent = new Intent(EditorActivity.this, LevelSelectActivity.class);
                startActivityForResult(intent, MAZE_REQUEST);
                break;
            }
            case R.id.checkButton: {
                checkField();
                break;
            }
        }
        dbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkField();
    }

    public boolean checkField() {
        editFieldView = findViewById(R.id.fieldView);
        final ImageView backgroundImageView = findViewById(R.id.background);

        Field.ErrorType error = editFieldView.getField().isCorrect();
        if (error == Field.ErrorType.NO_ERROR) {
            backgroundImageView.setImageResource(R.drawable.labyrinth_green);
            return true;
        } else {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            backgroundImageView.setImageResource(R.drawable.labyrinth_red);
            return false;
        }
    }

    public static void setEditFieldView(String json) {
        editFieldView.setField(Field.deserialize(json));
    }
}
