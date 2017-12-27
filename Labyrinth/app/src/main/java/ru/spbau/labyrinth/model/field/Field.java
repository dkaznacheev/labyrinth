package ru.spbau.labyrinth.model.field;

import com.google.gson.Gson;

import java.util.Arrays;

import ru.spbau.labyrinth.model.Model.Player;
/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {
    public enum State{UNKNOWN, NOTHING, MINOTAUR}; // nothing, hospital etc.
    private int size;
    private State[][] field;
    private boolean[][] borderX;
    private boolean[][] borderY;

    /**
     * Field constructor, creates new empty field.
     * @param fieledSize is size of field.
     */
    public Field(int fieledSize) {
        size = fieledSize;
        field = new State[size][fieledSize];
        borderX = new boolean[fieledSize + 1][fieledSize];
        borderY = new boolean[fieledSize][fieledSize + 1];

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

    public void addBorderX(int row, int column){
        borderX[row][column] = true;
    }

    public void addBorderY(int row, int column){
        borderY[row][column] = true;
    }

    public void setBorderX(int row, int column, boolean value){
        borderX[row][column] = value;
    }

    public void setBorderY(int row, int column, boolean value){
        borderY[row][column] = value;
    }

    public boolean hasBorderX(int row, int column) {
        return borderX[row][column];
    }

    public boolean hasBorderY(int row, int column) {
        return borderY[row][column];
    }

    /**
     * method addObject, adds information about generated Object, which is added to the field.
     */
    public void addObject(){
        /* TODO */
    }

    public static String serialize(Field field) {
        Gson gson = new Gson();
        String json = gson.toJson(field);
        return json;
    }

    public static Field deserialize(String json) {
        Gson gson = new Gson();
        Field field = gson.fromJson(json, Field.class);
        return field;
    }
}
