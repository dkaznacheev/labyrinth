package ru.spbau.labyrinth.model;

import com.google.gson.Gson;

import java.util.ArrayList;

import ru.spbau.labyrinth.model.Model.Turn;

public class Log {
    private final int players;

    private ArrayList<Turn[]> turns;

    public Log(int players) {
        this.players = players;
        turns = new ArrayList<>();
        /*
        for (int i = 0; i < 5; i++) {
            Turn[] turn = new Turn[players];
            for (int j = 0; j < players; j++) {
                turn[j] = new Turn(Model.Direction.UP,
                        Model.Direction.UP, j);
            }
            turns.add(turn);
        }
        */
    }

    public void addRound(Turn[] newTurns) {
        turns.add(newTurns);
    }

    public Turn getTurn(int turn, int player) {
        return turns.get(turn)[player];
    }

    public int getTurnsNum() {
        return turns.size();
    }

    public static String serialize(Log log) {
        Gson gson = new Gson();
        String json = gson.toJson(log);
        return json;
    }

    public static Log deserialize(String json) {
        Gson gson = new Gson();
        Log log = gson.fromJson(json, Log.class);
        return log;
    }

    public int getPlayerNum() {
        return players;
    }
}
