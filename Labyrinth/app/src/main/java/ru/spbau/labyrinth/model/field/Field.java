package ru.spbau.labyrinth.model.field;

import java.util.Arrays;

/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {
    public enum State {UNKNOWN, NOTHING, MINOTAUR, HOSPITAL} // nothing, hospital etc.

    private int size;
    private State[][] field;
    private boolean[][] borderX;
    private boolean[][] borderY;
    private int treasureX;
    private int treasureY;
    private int treasureOwner;
    private int hospitalX;
    private int hospitalY;
    private int exitBorderType; // 0 = X, 1 = Y
    private int exitBorderX;
    private int exitBorderY;

    /**
     * Field constructor, creates new empty field.
     *
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
        borderX[row][column] = true;
    }

    public void addBorderY(int row, int column) {
        borderY[row][column] = true;
    }

    public boolean hasBorderX(int row, int column) {
        return borderX[row][column];
    }

    public boolean hasBorderY(int row, int column) {
        return borderY[row][column];
    }

    public void setTreasurePos(int row, int column) {
        treasureX = row;
        treasureY = column;
    }

    public boolean isOnTreasure(int row, int coluum) {
        return row == treasureX && coluum == treasureY;
    }

    public int getTreasureOwner() {
        return treasureOwner;
    }

    public void setTreasureOwner(int ownerId) {
        treasureOwner = ownerId;
    }

    public int getTreasureX() {
        return treasureX;
    }

    public int getTreasureY() {
        return treasureY;
    }

    public int getHospitalX() {
        return hospitalX;
    }

    public int getHospitalY() {
        return hospitalY;
    }

    public void setHospitalPos(int row, int column) {
        hospitalX = row;
        hospitalY = column;
    }

    public boolean cellIsInField(int row, int column) {
        return row >= 0 && column >= 0 && row < size && column < size;
    }

    public void setBorderPos(int type, int row, int column) {
        exitBorderType = type;
        exitBorderX = row;
        exitBorderY = column;
    }

    public boolean isExitBorderX(int row, int column) {
        return (exitBorderType == 0) && (row == exitBorderX) && (column == exitBorderY);
    }

    public boolean isExitBorderY(int row, int column) {
        return (exitBorderType == 1) && (row == exitBorderX) && (column == exitBorderY);
    }
}
