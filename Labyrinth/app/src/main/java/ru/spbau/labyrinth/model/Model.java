package ru.spbau.labyrinth.model;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.spbau.labyrinth.model.field.Field;

public class Model {
    private Player[] players;
    private Field field;
    private Set<Integer> killed = new HashSet<>();

    public int getWinnerId() {
        return winnerId;
    }

    private int winnerId = -1;

    public enum Direction {UP, DOWN, LEFT, RIGHT, NONE}

    public int getTreasureOwnerId() {
        return field.getTreasureOwnerId();
    }

    private void processTurn(Turn turn) {
        int index = turn.getId();
        int[] d = getPosChange(turn.getMoveDir());
        int newx = players[index].getX() + d[0];
        int newy = players[index].getY() + d[1];

        if (newx == field.getTreasureX() && newy == field.getTreasureY() && field.getTreasureOwnerId() == -1) {
            field.setTreasureOwnerId(index);
        }

        if (newx == players[index].getX() && newy == players[index].getY()) {
            return;
        }

        if (!isBorder(players[index].getX(), players[index].getY(), newx, newy)) {
            players[index].setX(newx);
            players[index].setY(newy);
            if (players[index].getFieldState(newx, newy) == Field.State.UNKNOWN) {
                players[index].setFieldState(newx, newy, field.getState(newx, newy));
            }

            if (field.getTreasureOwnerId() == index) {
                field.setTreasurePos(newx, newy);
                players[index].setTreasurePos(newx, newy);
            }

            if (field.getState(newx, newy) == Field.State.MINOTAUR) {
                if (field.getTreasureOwnerId() == index) {
                    field.setTreasureOwnerId(-1);
                    field.setTreasurePos(newx, newy);
                    players[index].setTreasurePos(newx, newy);
                }
                players[index].setX(field.getHospitalX());
                players[index].setY(field.getHospitalY());
                players[index].setFieldState(field.getHospitalX(), field.getHospitalY(), Field.State.HOSPITAL);
            }

            //TODO what if several number of players stepped on the cell with treasure in the same time
            if (newx == field.getTreasureX() && newy == field.getTreasureY() && field.getTreasureOwnerId() == -1) {
                field.setTreasureOwnerId(index);
            }


        } else {
            boolean playerWon = false;
            int ind[] = getBorderInd(players[index].getX(), players[index].getY(), newx, newy);
            if (index == field.getTreasureOwnerId()) {
                if    ((ind[0] == 0 && field.isExitBorderX(ind[1], ind[2]))
                    || (ind[0] != 0 && field.isExitBorderY(ind[1], ind[2]))) {
                        playerWon = true;
                        winnerId = index;

                }
            }

            if (playerWon) {
                if (ind[0] == 0) {
                    if (!field.isExitBorderX(ind[1], ind[2])) {
                        players[index].setFieldBorderX(ind[1], ind[2]);
                    }
                } else {
                    if (!field.isExitBorderY(ind[1], ind[2])) {
                        players[index].setFieldBorderY(ind[1], ind[2]);
                    }
                }
            }
        }
    }

    /**
     * processTurnMultiplayer method is intended for analysis one game turn.
     *
     * @param turns is array which describes each player turn.
     * @return players array, which contains updated information.
     */
    public Player[] processTurnMultiplayer(Turn[] turns) {
        killed.clear();
        for (Turn turn : turns) {
            checkShoots(turn);
        }

        for (Integer killedId : killed) {
            if (killedId == field.getTreasureOwnerId()) {
                field.setTreasurePos(players[killedId].getX(), players[killedId].getY());
                players[killedId].setTreasurePos(players[killedId].getX(), players[killedId].getY());
                field.setTreasureOwnerId(-1);
            }
            players[killedId].setX(field.getHospitalX());
            players[killedId].setY(field.getHospitalY());
            players[killedId].setFieldState(field.getHospitalX(), field.getHospitalY(), Field.State.HOSPITAL);
        }

        for (Turn turn : turns) {
            if (!killed.contains(turn.getId())) {
                processTurn(turn);
            }
        }
        return players;
    }

    private void checkShoots(Turn turn) {
        if (turn.getShootDir() == Direction.NONE || !players[turn.getId()].hasCatridge()) {
            return;
        }
        players[turn.getId()].spendCartridge();

        int curPosX = players[turn.getId()].getX();
        int curPosY = players[turn.getId()].getY();

        int[] d = getPosChange(turn.getShootDir());
        while (field.cellIsInField(curPosX, curPosY)) {
            int newX = curPosX + d[0];
            int newY = curPosY + d[1];
            if (field.cellIsInField(newX, newY) && !isBorder(curPosX, curPosY, newX, newY)) {
                curPosX = newX;
                curPosY = newY;
            } else {
                break;
            }

            boolean someoneKilled = false;
            for (Player player : players) {
                if (player.getX() == curPosX && player.getY() == curPosY) {
                    killed.add(player.getId());
                    someoneKilled = true;
                    break;
                }
            }

            if (field.getState(curPosX, curPosY) == Field.State.MINOTAUR) {
                field.setState(curPosX, curPosY, Field.State.NOTHING);
                for (Player player: players) {
                    player.setFieldState(curPosX, curPosY, Field.State.NOTHING);
                }
                someoneKilled = true;
            }

            if (someoneKilled) {
                break;
            }
        }
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
        st.add(pos[0] * fieldSize + pos[1]);
        field.setState(pos[0], pos[1], Field.State.MINOTAUR);

        while (st.contains(pos[0] * fieldSize + pos[1])) {
            pos = generateRandomPosition(rnd);
        }
        st.add(pos[0] * fieldSize + pos[1]);
        field.setState(pos[0], pos[1], Field.State.HOSPITAL);
        field.setHospitalPos(pos[0], pos[1]);

        while (st.contains(pos[0] * fieldSize + pos[1])) {
            pos = generateRandomPosition(rnd);
        }
        st.add(pos[0] * fieldSize + pos[1]);
        field.setTreasurePos(pos[0], pos[1]);
        field.setTreasureOwnerId(-1);

        return players;
    }

    // TODO something less genius :)
    private Field generateRandomField(Random random, int fieldSize) {
        Field f = new Field(fieldSize);

        for (int i = 0; i < fieldSize; i++) {
            f.addBorderX(0, i);
            f.addBorderX(fieldSize, i);
            f.addBorderY(i, 0);
            f.addBorderY(i, fieldSize);
        }

        int side = random.nextInt() % 4;
        int pos = random.nextInt() % (fieldSize - 1);
        if (side == 0) {
            f.setBorderPos(Field.BorderType.HORIZONTAL, 0, pos);
        } else if (side == 2) {
            f.setBorderPos(Field.BorderType.HORIZONTAL, fieldSize, pos);
        } else if (side == 1) {
            f.setBorderPos(Field.BorderType.VERTICAL, pos, 0);
        } else {
            f.setBorderPos(Field.BorderType.VERTICAL, pos, fieldSize);
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

    //Inner but not nested because players is linked to model
    public class Player {
        private int x, y, cartridgesCnt, id;
        private int initialX, initialY;
        private final String name;
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
            fieldView.setTreasurePos(-1, -1);
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

        private void setX(int x) {
            this.x = x;
        }

        private void setY(int y) {
            this.y = y;
        }

        private boolean hasCatridge() {
            return cartridgesCnt > 0;
        }

        private void spendCartridge() {
            cartridgesCnt--;
        }

        private void setFieldState(int x, int y, Field.State state) {
            fieldView.setState(x, y, state);
        }

        private Field.State getFieldState(int x, int y) {
            return fieldView.getState(x, y);
        }

        private void setFieldBorderX(int row, int column) {
            fieldView.addBorderX(row, column);
        }

        private void setFieldBorderY(int row, int column) {
            fieldView.addBorderY(row, column);
        }

        public int getCartridgesCnt() {
            return cartridgesCnt;
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

        private void setTreasurePos(int x, int y) {
            fieldView.setTreasurePos(x, y);
        }

        public String getName() {
            return name;
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
            return gson.toJson(turn);
        }

        public static Turn deserialize(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, Turn.class);
        }
    }
}
