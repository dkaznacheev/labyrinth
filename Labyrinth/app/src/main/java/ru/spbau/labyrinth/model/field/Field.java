package ru.spbau.labyrinth.model.field;

import com.google.gson.Gson;

import java.util.Arrays;

/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {
    public enum State {UNKNOWN, NOTHING, MINOTAUR, HOSPITAL}
    public enum BorderType {HORIZONTAL, VERTICAL}

    private final int size;
    private final State[][] field;
    private final boolean[][] borderX; //an array of horizontal walls, from topmost to downmost
    private final boolean[][] borderY; //an array of vertical walls, from leftmost to rightmost
    private final Point treasure;
    private final Point hospital;
    private final Point exitBorder;
    private int treasureOwnerId;
    private BorderType exitBorderType;

    /**
     * Field constructor, creates new empty field.
     *
     * @param fieldSize is size of field.
     */
    public Field(int fieldSize) {
        size = fieldSize;
        field = new State[size][size];
        borderX = new boolean[size + 1][size];
        borderY = new boolean[size][size + 1];
        treasure = new Point();
        hospital = new Point();
        exitBorder = new Point();
        for (int i = 0; i < size; i++) {
            Arrays.fill(field[i], State.NOTHING);
        }
    }

    /**
     * getSize method returns field size.
     *
     * @return size of the field.
     */
    public int getSize() {
        return size;
    }

    public void setState(int x, int y, State state) {
        field[x][y] = state;
    }

    public State getState(int x, int y) {
        return field[x][y];
    }

    public void addBorderX(int row, int column) {
        setBorderX(row, column, true);
    }

    public void addBorderY(int row, int column) {
        setBorderY(row, column, true);
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

    public void setTreasurePos(int row, int column) {
        treasure.x = row;
        treasure.y = column;
    }

    public int getTreasureOwnerId() {
        return treasureOwnerId;
    }

    public void setTreasureOwnerId(int ownerId) {
        treasureOwnerId = ownerId;
    }

    public int getTreasureX() {
        return treasure.x;
    }

    public int getTreasureY() {
        return treasure.y;
    }

    public int getHospitalX() {
        return hospital.x;
    }

    public int getHospitalY() {
        return hospital.y;
    }

    public void setHospitalPos(int row, int column) {
        hospital.x = row;
        hospital.y = column;
    }

    public boolean cellIsInField(int row, int column) {
        return row >= 0 && column >= 0 && row < size && column < size;
    }

    public void setExitBorderPos(BorderType type, int row, int column) {
        exitBorderType = type;
        exitBorder.x = row;
        exitBorder.y = column;
    }

    public boolean isExitBorderX(int row, int column) {
        return (exitBorderType == BorderType.HORIZONTAL) && (row == exitBorder.x) && (column == exitBorder.y);
    }

    public boolean isExitBorderY(int row, int column) {
        return (exitBorderType == BorderType.VERTICAL) && (row == exitBorder.x) && (column == exitBorder.y);
    }

    public static String serialize(Field field) {
        return new Gson().toJson(field);
    }

    public static Field deserialize(String json) {
        return new Gson().fromJson(json, Field.class);
    }

    private class Point {
        int x;
        int y;
    }
}
