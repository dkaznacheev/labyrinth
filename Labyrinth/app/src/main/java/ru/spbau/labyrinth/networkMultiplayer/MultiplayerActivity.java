package ru.spbau.labyrinth.networkMultiplayer;

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
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import ru.spbau.labyrinth.OnlineGameActivity;
import ru.spbau.labyrinth.R;
import ru.spbau.labyrinth.StartActivity;

public class MultiplayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SELECT_PLAYERS = 9010;
    private static final int RC_LOOK_AT_MATCHES = 10001;

    private TurnBasedMultiplayerClient turnBasedMultiplayerClient;
    private TurnBasedMatch turnBasedMatch;
    private InvitationsClient invitationsClient;

    private MultiplayerMatch multiplayerMatch = MultiplayerMatch.getInstance();

    private void startGame (int playersCount) {
        multiplayerMatch.playersCount = playersCount;
        Intent intent = new Intent(MultiplayerActivity.this, OnlineGameActivity.class);
        startActivity(intent);
    }

    private void startSignInIntent() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        multiplayerMatch.googleSignInAccount = account;

        if (account != null) {
            onConnected(account);
            return;
        }

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        multiplayerMatch.googleSignInClient = signInClient;
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        turnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);
        invitationsClient = Games.getInvitationsClient(this, googleSignInAccount);

        multiplayerMatch.turnBasedMultiplayerClient = turnBasedMultiplayerClient;
        multiplayerMatch.invitationsClient = invitationsClient;

        Games.getPlayersClient(this, googleSignInAccount)
                .getCurrentPlayer()
                .addOnSuccessListener(
                        new OnSuccessListener<Player>() {
                            @Override
                            public void onSuccess(Player player) {
                                multiplayerMatch.playerId = player.getPlayerId();
                            }
                        }
                ).addOnFailureListener(createFailureListener("There was a problem getting the player!"));

        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (match != null) {
                                multiplayerMatch.updateMatch(match);
                            }
                        }
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
                onDisconnected();
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            int minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            Bundle autoMatchCriteria = null;
            if (minAutoPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0);
            }
            TurnBasedMatchConfig turnBasedMatchConfig = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS))
                    .setAutoMatchCriteria(autoMatchCriteria)
                    .build();

            turnBasedMultiplayerClient.createMatch(turnBasedMatchConfig).addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                @Override
                public void onSuccess(TurnBasedMatch newTurnBasedMatch) {
                    turnBasedMatch = newTurnBasedMatch;
                    multiplayerMatch.turnBasedMatch = turnBasedMatch;
                    startGame(turnBasedMatch.getParticipantIds().size());
                }
            });

        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
            if (match != null) {
                turnBasedMatch = match;
                multiplayerMatch.turnBasedMatch = turnBasedMatch;
                int playerCount = match.getParticipantIds().size();
                startGame(playerCount);
            }
        }
    }

    private void onDisconnected() {
        multiplayerMatch.turnBasedMultiplayerClient = null;
        multiplayerMatch.invitationsClient = null;
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
        Button startMatchButton = findViewById(R.id.startMatchButton);
        Button checkGamesButton = findViewById(R.id.checkGamesButton);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        startMatchButton.setOnClickListener(this);
        checkGamesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startMatchButton:
                onStartMatchCLicked();
                break;
            case R.id.signInButton:
                startSignInIntent();
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.checkGamesButton:
                onCheckGamesClicked();
                break;
        }
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.signOutButton).setVisibility(View.GONE);
                        onDisconnected();
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

    private String getNextParticipantId(String myPlayerId, TurnBasedMatch match) {
        String myParticipantId = match.getParticipantId(myPlayerId);
        ArrayList<String> participantIds = match.getParticipantIds();

        int desiredIndex = -1;
        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (match.getAvailableAutoMatchSlots() <= 0) {
            return participantIds.get(0);
        } else {
            return null;
        }
    }

    private void onCheckGamesClicked() {
        if (turnBasedMultiplayerClient == null) {
            return;
        }
        turnBasedMultiplayerClient.getInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
                    }
                });
    }

    private void onStartMatchCLicked() {
       if (turnBasedMultiplayerClient == null) {
            return;
       }
       turnBasedMultiplayerClient.getSelectOpponentsIntent(1, 1, true)
               .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                });
    }

}