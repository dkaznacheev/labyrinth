package ru.spbau.labyrinth.model;

import android.content.Intent;

import com.google.gson.Gson;

import ru.spbau.labyrinth.model.Model.*;

public class GameState {
    public Model getModel() {
        return model;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Log getLog() {
        return log;
    }

    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    private Model model;
    private Player[] players;
    private Turn[] turns;
    private Log log;
    private int currentPlayerNum;
    private final int playerNum;

    public GameState(Intent data) {
        playerNum = data.getIntExtra("playerNum", 0);
        String[] names = new String[playerNum];
        for (int i = 0; i < playerNum; i++) {
            names[i] = data.getStringExtra("player" + Integer.toString(i));
        }

        model = new Model();
        players = model.init(names, 3);
        turns = new Model.Turn[playerNum];
        log = new Log(playerNum);
        currentPlayerNum = 0;
    }

    public static GameState deserialize(String save) {
        return new Gson().fromJson(save, GameState.class);
    }

    public int updateTurn(Turn turn) {
        turns[currentPlayerNum] = turn;
        currentPlayerNum++;
        if (currentPlayerNum == playerNum) {
            updateRound(turns);
            currentPlayerNum = 0;
            turns = new Model.Turn[playerNum];
        }
        return model.getWinner();
    }

    public int updateRound(Turn[] turns) {
        log.addRound(turns);
        players = model.processTurnMultiplayer(turns);
        return model.getWinner();
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public String getTreasureOwner() {
        int treasureOwner = model.getTreasureOwner();
        String owner = "Nobody";
        if (treasureOwner != -1) {
            owner = players[treasureOwner].getName();
        }
        return owner;
    }
}
