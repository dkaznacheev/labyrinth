package ru.spbau.labyrinth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MultiplayerActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SELECT_PLAYERS = 9010;
    private TurnBasedMultiplayerClient turnBasedMultiplayerClient;
    private TurnBasedMatch turnBasedMatch;

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        turnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);
        turnBasedMultiplayerClient.getSelectOpponentsIntent(1, 3, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                onConnected(result.getSignInAccount());
                findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            int minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            TurnBasedMatchConfig.Builder builder = TurnBasedMatchConfig.builder().addInvitedPlayers(data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS));

            Bundle autoMatchCriteria = null;
            if (minAutoPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0);
            }
            builder.setAutoMatchCriteria(autoMatchCriteria);


            turnBasedMultiplayerClient.createMatch(builder.build()).addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                        @Override
                        public void onSuccess(TurnBasedMatch turnBasedMatch) {
                            onInitiateMatch(turnBasedMatch);
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem creating a match!"));

        }
    }

    private void onInitiateMatch(TurnBasedMatch match) {
        if (match.getData() == null) {
            //TODO:????
            //initializeGameData(match);
            return;
        } else {
            updateMatch(match);
        }

        startMatch(match);
    }

    private void startMatch(TurnBasedMatch match) {

    }

    private void updateMatch(TurnBasedMatch match) {
        turnBasedMatch = match;

        int matchStatus = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (matchStatus) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled.", "This match was canceled.");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired.", "This match is expired.");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                showWarning("Completed.", "This match is completed.");
                return;
        }

        if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {

        }
    }

    private void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title).setMessage(message);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MultiplayerActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        alertDialogBuilder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        Button signInButton = findViewById(R.id.signInButton);
        Button signOutButton = findViewById(R.id.signOutButton);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signInButton) {
            startSignInIntent();
        } else if (view.getId() == R.id.signOutButton) {
            signOut();
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutButton).setVisibility(View.GONE);
        }
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }

    private void handleException(Exception exception, String details) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(exception.getMessage() + "\n" + details)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }
}