package ru.spbau.labyrinth.networkMultiplayer;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MultiplayerMatch {
    private static MultiplayerMatch instance;

    public static MultiplayerMatch getInstance() {
        if (instance == null) {
            instance = new MultiplayerMatch();
        }
        return instance;
    }

    public int playersCount;
    public String playerId;
    public GoogleSignInAccount googleSignInAccount;
    public GoogleSignInClient googleSignInClient;
    public TurnBasedMultiplayerClient turnBasedMultiplayerClient;
    public InvitationsClient invitationsClient;
    public TurnBasedMatch turnBasedMatch;

    private boolean needToUpdateRound = false;
    private boolean sendingData = false;
    private boolean receivingData = false;

    public void onInitiateMatch(TurnBasedMatch match) {
        if (turnBasedMatch.getData() != null && !sendingData) {
            updateMatch(match);
        } else {
            startMatch(match);
        }
    }

    public int getPlayersNumber() {
        String myParticipantId = turnBasedMatch.getParticipantId(playerId);
        ArrayList<String> ids = turnBasedMatch.getParticipantIds();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals(myParticipantId)) {
                return i;
            }
        }
        return -1;
    }

    private String getNextParticipantId() {
        String myParticipantId = turnBasedMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = turnBasedMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (turnBasedMatch.getAvailableAutoMatchSlots() <= 0) {
            needToUpdateRound = true;
            return participantIds.get(0);
        } else {
            return null;
        }
    }

    public boolean isPlayersTurn() {
        int turnStatus = turnBasedMatch.getTurnStatus();
        return turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
    }

    private void startMatch(TurnBasedMatch match) {
        turnBasedMatch = match;
        playersCount = match.getParticipantIds().size();
    }

    //package private
    void updateMatch(TurnBasedMatch match) {
        turnBasedMatch = match;
        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    break;
                }
        }

        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                if (!sendingData){

                }
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                if (!sendingData)
                    //controller.applyData(curMatch.getData());
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
        }
    }

    public void sendData(byte[] data) {
        String nextParticipantId = getNextParticipantId();
        if (sendingData) {
            return;
        }
        sendingData = true;
        turnBasedMultiplayerClient.takeTurn(turnBasedMatch.getMatchId(), data, nextParticipantId).addOnCompleteListener(new OnCompleteListener<TurnBasedMatch>() {
            @Override
            public void onComplete(@NonNull Task<TurnBasedMatch> task) {
                if (task.isSuccessful()) {
                    turnBasedMatch = task.getResult();
                    updateMatch(turnBasedMatch);
                }
                sendingData = false;
            }
        });
        if (needToUpdateRound) {
            update();
            needToUpdateRound = false;
        }
    }

    public void update() {
        if (receivingData) {
            return;
        }
        receivingData = true;
        turnBasedMultiplayerClient.loadMatch(turnBasedMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<TurnBasedMatch>>() {
                    @Override
                    public void onSuccess(AnnotatedData<TurnBasedMatch> turnBasedMatchAnnotatedData) {
                        receivingData = false;
                        turnBasedMatch = turnBasedMatchAnnotatedData.get();
                        updateMatch(turnBasedMatch);
                    }
                });
    }

    public void finish() {
        turnBasedMultiplayerClient.finishMatch(turnBasedMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        updateMatch(turnBasedMatch);
                    }
                });
    }
}
