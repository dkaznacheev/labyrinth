package ru.spbau.labyrinth.model.field;

import java.util.Arrays;

import ru.spbau.labyrinth.model.Model.Player;
/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {
    public enum State{UNKNOWN, NOTHING, MINOTAUR}; // nothing, hospital etc.
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
            Arrays.fill(field[i], State.NOTHING);
        }
    }

    /**
     * getSize method returns field size.
     * @return size of the field.
     */
    public int getSize(){
        return size;
    }

    public void setState(int x, int y, State state) {
        field[x][y] = state;
    }

    public State getState(int x, int y){
        return field[x][y];
    }

    /**
     * method addObject, adds information about generated Object, which is added to the field.
     */
    public void addObject(){
        /* TODO */
    }
}
