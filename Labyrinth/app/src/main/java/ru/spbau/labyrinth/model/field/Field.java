package ru.spbau.labyrinth.model.field;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * class Field. Contains information about playing field. Note: field is square.
 */
public class Field {

    public enum State {UNKNOWN, NOTHING, MINOTAUR, HOSPITAL}
    public enum BorderType {HORIZONTAL, VERTICAL}
    public enum ErrorType {
        NO_ERROR("Everything is correct"),
        MULTIPLE_EXITS("Too many exits!"),
        NO_EXITS("No exits!"),
        NO_HOSPITAL("No hospital!"),
        NOT_LINKED("There are unreachable areas!"),
        NO_TREASURE("No treasure!"),
        TOO_DENSE("Not enough free space!");

        private String message;

        ErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
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
        setState(row, column, State.HOSPITAL);
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
        public Point() {
            x = 0;
            y = 0;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    public boolean isBorder(int curx, int cury, int newx, int newy) {
        int ind[] = getBorderInd(curx, cury, newx, newy);
        if (ind[0] == 0) {
            return hasBorderX(ind[1], ind[2]);
        } else {
            return hasBorderY(ind[1], ind[2]);
        }
    }

    public int[] getBorderInd(int curx, int cury, int newx, int newy) {
        if (curx == newx) {
            if (cury < newy) {
                return new int[]{0, newy, curx};
            } else {
                return new int[]{0, cury, curx};
            }
        } else {
            if (curx < newx) {
                return new int[]{1, cury, newx};
            } else {
                return new int[]{1, cury, curx};
            }
        }
    }

    private boolean isLinked() {
        Point start = new Point();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        int[][] distance = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distance[i][j] = -1;
            }
        }
        distance[0][0] = 0;
        int[] dx = new int[]{0, 1, 0, -1};
        int[] dy = new int[]{1, 0, -1, 0};
        while (!queue.isEmpty()) {
            Point point = queue.poll();
            for (int i = 0; i < 4; i++) {
                Point newPoint = new Point(point.x + dx[i], point.y + dy[i]);
                if (    newPoint.x >= 0
                     && newPoint.x < size
                     && newPoint.y >= 0
                     && newPoint.y < size
                     && !isBorder(point.x, point.y, newPoint.x, newPoint.y)
                     && distance[newPoint.x][newPoint.y] == -1) {
                    distance[newPoint.x][newPoint.y] = distance[point.x][point.y] + 1;
                    queue.add(newPoint);
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (distance[i][j] == -1)
                    return false;
            }
        }
        return true;
    }

    public ErrorType isCorrect() {
        int exitBorderNum = 0;
        for (int i = 0; i < size; i++) {
            if (!hasBorderX(0, i))
                exitBorderNum++;
            if (!hasBorderX(size, i))
                exitBorderNum++;
            if (!hasBorderY(i, 0))
                exitBorderNum++;
            if (!hasBorderY(i, size))
                exitBorderNum++;
        }
        if (exitBorderNum < 1)
            return ErrorType.NO_EXITS;
        if (exitBorderNum > 1)
            return ErrorType.MULTIPLE_EXITS;
        boolean hasHospital = false;
        int freeSpaces = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (getState(i, j) == State.HOSPITAL) {
                    hasHospital = true;
                }
                if (getState(i, j) == State.NOTHING) {
                    freeSpaces++;
                }
            }
        }
        if (!hasHospital)
            return ErrorType.NO_HOSPITAL;
        if (getTreasureX() < 0 || getTreasureY() < 0)
            return ErrorType.NO_TREASURE;

        if (!isLinked())
            return ErrorType.NOT_LINKED;
        if (freeSpaces < size*size / 2)
            return ErrorType.TOO_DENSE;
        return ErrorType.NO_ERROR;
    }
}
