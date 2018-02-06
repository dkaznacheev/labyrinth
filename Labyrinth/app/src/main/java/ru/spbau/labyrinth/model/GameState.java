package ru.spbau.labyrinth.model;

import com.google.gson.Gson;

import ru.spbau.labyrinth.model.Model.*;

public class GameState implements Cloneable{
    public Model getModel() {
        return model;
    }

    public Player[] getPlayers() {
        return players;
    }

    public String[] getNames() {
        return names;
    }

    public Turn[] getTurns() {
        return turns;
    }

    public Log getLog() {
        return log;
    }

    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }

    private Model model;
    private Player[] players;
    private String[] names;
    private Turn[] turns;
    private Log log;
    private int currentPlayerNum;
    
    public GameState(
            Model model,
            Player[] players,
            String[] names,
            Turn[] turns,
            Log log,
            int currentPlayerNum
    ) {
        this.model = model;
        this.players = players;
        this.names = names;
        this.turns = turns;
        this.log = log;
        this.currentPlayerNum = currentPlayerNum;
    }

    public static GameState deserialize(String save) {
        return new Gson().fromJson(save, GameState.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
