package ru.spbau.labyrinth.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.spbau.labyrinth.model.field.*;

public class Model {
    private Player[] players;
    private Field field;
    private Set<Integer> killed = new HashSet<>();

    public enum Direction {UP, DOWN, LEFT, RIGHT, NONE}

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

        if (newx == players[index].getX() && newy == players[index].getY()) {
            return;
        }

        if (!isBorder(players[index].getX(), players[index].getY(), newx, newy)) {
            players[index].setX(newx);
            players[index].setY(newy);
            if (players[index].getFieldState(newx, newy) == Field.State.UNKNOWN) {
                if (field.getState(newx, newy) == Field.State.HOSPITAL) {
                    players[index].setFieldState(newx, newy, Field.State.HOSPITAL);
                } else if (field.getState(newx, newy) == Field.State.MINOTAUR) {
                    players[index].setFieldState(newx, newy, Field.State.MINOTAUR);
                }
            }

            if (field.getTreasureOwner() == index) {
                field.setTreasurePos(newx, newy);
            }

            //TODO what if several number of players stepped on the cell with treasure in the same time
            if (newx == field.getTreasureX() && newy == field.getTreasureY() && field.getTreasureOwner() == -1) {
                field.setTreasureOwner(index);
            }

            if (field.getState(newx, newy) == Field.State.MINOTAUR) {
                field.setTreasureOwner(-1);
                players[index].setX(field.getHospitalX());
                players[index].setY(field.getHospitalY());
            }
        } else {
            int ind[] = getBorderInd(players[index].getX(), players[index].getY(), newx, newy);
            if (index == field.getTreasureOwner()) {
                if (ind[0] == 0) {
                    if (field.isExitBorderX(ind[1], ind[2])) {
                        players[index].markThatPlayerWin();
                    }
                } else {
                    if (field.isExitBorderY(ind[1], ind[2])) {
                        players[index].markThatPlayerWin();
                    }
                }
            }

            if (!players[index].playerWin) {
                if (ind[0] == 0) {
                    players[index].setFieldBorderX(ind[1], ind[2]);
                } else {
                    players[index].setFieldBorderY(ind[1], ind[2]);
                }
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
        killed.clear();
        for (Turn turn : turns) {
            checkShoots(turn);
        }

        for (Integer killedId : killed) {
            if (killedId == field.getTreasureOwner()) {
                field.setTreasurePos(players[killedId].getX(), players[killedId].getY());
                field.setTreasureOwner(-1);
            }
            players[killedId].setX(field.getHospitalX());
            players[killedId].setY(field.getHospitalY());
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
        int n = players.length;

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

        while (st.contains(pos[0] * fieldSize + pos[1])) {
            pos = generateRandomPosition(rnd);
        }
        field.setState(pos[0], pos[1], Field.State.HOSPITAL);
        field.setHospitalPos(pos[0], pos[1]);

        while (st.contains(pos[0] * fieldSize + pos[1])) {
            pos = generateRandomPosition(rnd);
        }
        field.setTreasurePos(pos[0], pos[1]);
        field.setTreasureOwner(-1);

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

        int side = random.nextInt() % 4;
        int pos = random.nextInt() % fieldSize;
        if (side == 0) {
            f.setBorderPos(0, 0, pos);
        } else if (side == 2) {
            f.setBorderPos(0, fieldSize, pos);
        } else if (side == 1) {
            f.setBorderPos(1, pos, 0);
        } else {
            f.setBorderPos(1, pos, fieldSize);
        }

        return f;
    }

    private int[] generateRandomPosition(Random random) {
        int[] pos = new int[2];
        pos[0] = random.nextInt() % field.getSize();
        pos[1] = random.nextInt() % field.getSize();
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
        private boolean hasTreasure;
        private boolean playerWin;

        Player(int posx, int posy, String name, int id) {
            this.x = posx;
            this.y = posy;
            this.initialX = x;
            this.initialY = y;
            this.name = name;
            fieldView = new Field(Model.this.field.getSize());
            cartridgesCnt = 3;
            hasTreasure = false;

            for (int i = 0; i < fieldView.getSize(); i++) {
                for (int j = 0; j < fieldView.getSize(); j++) {
                    fieldView.setState(i, j, Field.State.UNKNOWN);
                }
            }

            fieldView.setState(x, y, Field.State.NOTHING);
        }

        public boolean hasTreasure() {
            return hasTreasure;
        }

        public void changeTreasureOwningState(boolean isTreasureOwner) {
            hasTreasure = isTreasureOwner;
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

        public boolean hasCatridge() {
            return cartridgesCnt > 0;
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

        public void markThatPlayerWin() {
            playerWin = true;
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
    public class Turn {
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
        Turn(Model.Direction moveDirection, Model.Direction shootDirection, int id) {
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
    }
}
