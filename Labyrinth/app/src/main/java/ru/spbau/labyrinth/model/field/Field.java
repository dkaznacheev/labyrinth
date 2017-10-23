package ru.spbau.labyrinth.model.field;

import java.util.Arrays;

import ru.spbau.labyrinth.model.Model.Player;
/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {
    private enum State{player, unknown, nothing}; // nothing, hospital etc.
    private int size;
    private State[][] field;
    private boolean[] borderX[];
    private boolean[] borderY[];

    /**
     * Field constructor, creates new empty field.
     * @param fieledSize is size of field.
     */
    public Field(int fieledSize) {
        size = fieledSize;
        field = new State[size][fieledSize];

        for (int i = 0; i < size; i++) {
            Arrays.fill(field[i], State.nothing);
        }
    }

    /**
     * addPLayer method. Adds information about player to the field.
     * @param player is adding player.
     */
    public void addPlayer(Player player){
        int x = player.getX();
        int y = player.getY();
        field[x][y] = State.player;
    }

    /**
     * getSize method returns field size.
     * @return size of the field.
     */
    public int getSize(){
        return size;
    }

    /**
     * method addObject, adds information about generated Object, which is added to the field.
     */
    public void addObject(){
        /* TODO */
    }
}
