package ru.spbau.labyrinth.model;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.spbau.labyrinth.model.field.*;

public class Model {
    private Player[] players;
    private Field field;

    public enum Direction {UP, DOWN, LEFT, RIGHT, NONE}

    ;

    //One player demo.

    private Player demoPlayer;
    private Minotaur demoMinotaur;

    public Player demoInit() {
        field = new Field(5);
        demoPlayer = new Player(2, 2, "Mr. Smith", 0);
        demoMinotaur = new Minotaur(0, 0, "Deadline");
        demoPlayer.setFieldState(0, 0, Field.State.MINOTAUR);
        field.setState(0, 0, Field.State.MINOTAUR);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i != 2 || j != 2)
                    demoPlayer.setFieldState(i, j, Field.State.UNKNOWN);
            }
        }

        for (int i = 0; i < 5; i++) {
            field.addBorderX(0, i);
            field.addBorderX(5, i);
            field.addBorderY(i, 0);
            field.addBorderY(i, 5);
        }
        field.addBorderX(1, 2);
        field.addBorderX(2, 2);
        field.addBorderX(2, 1);
        field.addBorderX(3, 1);
        field.addBorderY(1, 2);
        field.addBorderY(2, 2);
        field.addBorderY(2, 1);
        field.addBorderY(3, 1);
        return demoPlayer;
    }

    public Player demoProcessTurn(Model.Direction moveDir, Model.Direction shootDir) {
        int[] d = getPosChange(moveDir);
        int newx = demoPlayer.getX() + d[0];
        int newy = demoPlayer.getY() + d[1];

        if (shootDir != Direction.NONE) {
            demoPlayer.spendCartridge();
        }

        if (newx == demoPlayer.getX() && newy == demoPlayer.getY()) {
            return demoPlayer;
        }

        if (!isBorder(demoPlayer.getX(), demoPlayer.getY(), newx, newy)) {
            demoPlayer.setX(newx);
            demoPlayer.setY(newy);
            if (demoPlayer.getFieldState(newx, newy) == Field.State.UNKNOWN) {
                if (newx == 0 && newy == 0) {
                    demoPlayer.setFieldState(newx, newy, Field.State.MINOTAUR);
                } else {
                    demoPlayer.setFieldState(newx, newy, Field.State.NOTHING);
                }
            }
        } else {
            int ind[] = getBorderInd(demoPlayer.getX(), demoPlayer.getY(), newx, newy);
            if (ind[0] == 0) {
                demoPlayer.setFieldBorderX(ind[1], ind[2]);
            } else {
                demoPlayer.setFieldBorderY(ind[1], ind[2]);
            }
        }

        return demoPlayer;
    }

    private void processTurn(Turn turn) {
        int index = turn.getId();
        int[] d = getPosChange(turn.getMoveDir());
        int newx = players[index].getX() + d[0];
        int newy = players[index].getY() + d[1];

        if (turn.getShootDir() != Direction.NONE) {
            players[index].spendCartridge();
        }

        if (newx == players[index].getX() && newy == players[index].getY()) {
            return;
        }

        if (!isBorder(players[index].getX(), players[index].getY(), newx, newy)) {
            players[index].setX(newx);
            players[index].setY(newy);
            if (players[index].getFieldState(newx, newy) == Field.State.UNKNOWN) {
                players[index].setFieldState(newx, newy, Field.State.NOTHING);
            }
        } else {
            int ind[] = getBorderInd(players[index].getX(), players[index].getY(), newx, newy);
            if (ind[0] == 0) {
                players[index].setFieldBorderX(ind[1], ind[2]);
            } else {
                players[index].setFieldBorderY(ind[1], ind[2]);
            }
        }
    }

    /**
     * processTurnMuliplayer method is intended for analysis one game turn.
     *
     * @param turns is array which describes each player turn.
     * @return players array, which contains updated information.
     */
    public Player[] processTurnMuliplayer(Turn[] turns) {
        for (Turn turn : turns) {
            processTurn(turn);
        }
        return players;
    }


    /**
     * game model initial method.
     *
     * @param names     is array of players names.
     * @param fieldSize is size of playing field.
     * @return array of players, which contains basic information about players.
     * Note: id for players will be in the same order as names are given.
     */
    public Player[] init(String[] names, int fieldSize) {
        int n = names.length;

        Random rnd = new Random();

        field = generateRandomField(rnd, fieldSize);
        players = new Player[n];

        Set<Integer> st = new HashSet<>();
        int pos[] = generateRandomPosition(rnd);
        for (int i = 0; i < n; i++) {
            while (st.contains(pos[0] * fieldSize + pos[1])) {
                pos = generateRandomPosition(rnd);
            }

            st.add(pos[0] * fieldSize + pos[1]);

            players[i] = new Player(pos[0], pos[1], names[i], i);
        }

        while (st.contains(pos[0] * fieldSize + pos[1])) {
            pos = generateRandomPosition(rnd);
        }

        field.setState(pos[0], pos[1], Field.State.MINOTAUR);
        return players;
    }

    // TODO something less genius :)
    public Field generateRandomField(Random random, int fieldSize) {
        Field f = new Field(fieldSize);

        for (int i = 0; i < fieldSize; i++) {
            f.addBorderX(0, i);
            f.addBorderX(fieldSize, i);
            f.addBorderY(i, 0);
            f.addBorderY(i, fieldSize);
        }

        return f;
    }

    private int[] generateRandomPosition(Random random) {
        int[] pos = new int[2];
        pos[0] = Math.abs(random.nextInt()) % field.getSize();
        pos[1] = Math.abs(random.nextInt()) % field.getSize();
        return pos;
    }

    private boolean isBorder(int curx, int cury, int newx, int newy) {
        int ind[] = getBorderInd(curx, cury, newx, newy);
        if (ind[0] == 0) {
            return field.hasBorderX(ind[1], ind[2]);
        } else {
            return field.hasBorderY(ind[1], ind[2]);
        }
    }

    private int[] getBorderInd(int curx, int cury, int newx, int newy) {
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

    private int[] getPosChange(Direction direction) {
        if (direction == Direction.DOWN) {
            return new int[]{0, 1};
        } else if (direction == Direction.UP) {
            return new int[]{0, -1};
        } else if (direction == Direction.LEFT) {
            return new int[]{-1, 0};
        } else if (direction == Direction.RIGHT) {
            return new int[]{1, 0};
        }
        return new int[]{0, 0};
    }

    public class Player {
        private int x, y, hp, cartridgesCnt, id;
        private int initialX, initialY;
        private String name;
        private Field fieldView;

        Player(int posx, int posy, String name, int id) {
            this.x = posx;
            this.y = posy;
            this.initialX = x;
            this.initialY = y;
            this.name = name;
            this.id = id;
            fieldView = new Field(Model.this.field.getSize());
            cartridgesCnt = 3;

            for (int i = 0; i < fieldView.getSize(); i++) {
                for (int j = 0; j < fieldView.getSize(); j++) {
                    fieldView.setState(i, j, Field.State.UNKNOWN);
                }
            }

            fieldView.setState(x, y, Field.State.NOTHING);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void spendCartridge() {
            cartridgesCnt--;
        }

        public void setFieldState(int x, int y, Field.State state) {
            fieldView.setState(x, y, state);
        }

        public Field.State getFieldState(int x, int y) {
            return fieldView.getState(x, y);
        }

        public void setFieldBorderX(int row, int column) {
            fieldView.addBorderX(row, column);
        }

        public void setFieldBorderY(int row, int column) {
            fieldView.addBorderY(row, column);
        }

        public int getCartridgesCnt() {
            return cartridgesCnt;
        }

        public boolean getFieldBorderX(int row, int column) {
            return fieldView.hasBorderX(row, column);
        }

        public boolean getFieldBorderY(int row, int column) {
            return fieldView.hasBorderY(row, column);
        }

        public int getId() {
            return id;
        }

        public int getInitialX() {
            return initialX;
        }

        public int getInitialY() {
            return initialY;
        }

        public Field getField() {
            return fieldView;
        }
    }

    public class Minotaur {
        private int x, y;
        private String name;

        Minotaur(int posx, int posy, String name) {
            x = posx;
            y = posy;
            this.name = name;
        }
    }

    /**
     * Turn class is intended for storing information about game turn of one player.
     */
    public static class Turn {
        private Model.Direction moveDir;
        private Model.Direction shootDir;
        private int id;

        /**
         * Turn class constructor.
         *
         * @param moveDirection  is enum description of players shot.
         * @param shootDirection is enum description of players move.
         * @param id             is id of this player.
         */
        public Turn(Model.Direction moveDirection, Model.Direction shootDirection, int id) {
            this.moveDir = moveDirection;
            this.shootDir = shootDirection;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Model.Direction getMoveDir() {
            return moveDir;
        }

        public Model.Direction getShootDir() {
            return shootDir;
        }

        public static String serialize(Turn turn) {
            Gson gson = new Gson();
            String json = gson.toJson(turn);
            return json;
        }

        public static Turn deserialize(String json) {
            Gson gson = new Gson();
            Turn turn = gson.fromJson(json, Turn.class);
            return turn;
        }
    }
}
