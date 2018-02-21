package ru.spbau.labyrinth.model;

import android.content.Intent;

import com.google.gson.Gson;

import ru.spbau.labyrinth.model.Model.Player;
import ru.spbau.labyrinth.model.Model.Turn;
import ru.spbau.labyrinth.model.field.Field;

public class GameState {
    public Player[] getPlayers() {
        return players;
    }

    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }

    private final Model model;
    private Player[] players;
    private Turn[] turns;
    public final Log log;
    private int currentPlayerNum;
    public final int playerNum;

    public GameState(String[] names) {
        playerNum = names.length;
        model = new Model();
        players = model.init(names, 3);
        turns = new Model.Turn[playerNum];
        log = new Log(playerNum);
        currentPlayerNum = 0;
    }

    public GameState(String[] names, Field field) {
        playerNum = names.length;
        model = new Model();
        players = model.init(names, field);
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
        return model.getWinnerId();
    }

    public int updateRound(Turn[] turns) {
        log.addRound(turns);
        players = model.processTurnMultiplayer(turns);
        return model.getWinnerId();
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public String getTreasureOwner() {
        int treasureOwner = model.getTreasureOwnerId();
        String owner = "Nobody";
        if (treasureOwner != -1) {
            owner = players[treasureOwner].getName();
        }
        return owner;
    }
}
