package ru.spbau.labyrinth.networkMultiplayer;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

public class MultiplayerMatch {
    public MultiplayerMatch() {}

    public String playerId;
    public GoogleSignInAccount googleSignInAccount;
    public GoogleSignInClient googleSignInClient;
    public TurnBasedMultiplayerClient turnBasedMultiplayerClient;
    public InvitationsClient invitationsClient;
    public TurnBasedMatch turnBasedMatch;

    public void updateMatch(TurnBasedMatch match) {

    }
}
