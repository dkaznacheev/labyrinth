package ru.spbau.labyrinth.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.spbau.labyrinth.model.field.*;

/**
 * class Model. Contains game model.
 */
public class Model {
    private Player[] players;
    private Field field;
    public enum Direction{UP, DOWN, LEFT, RIGHT, NONE};

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

        for (int i = 0; i < 5; i++){
            field.addBorderX(0, i);
            field.addBorderX(5, i);
            field.addBorderY(i, 0);
            field.addBorderY(i, 5);
        }

        return demoPlayer;
    }

    public Player processTurn(Model.Direction moveDir, Model.Direction shootDir) {
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
                if (newx == 0 && newy == 0){
                    demoPlayer.setFieldState(newx, newy, Field.State.MINOTAUR);
                } else {
                    demoPlayer.setFieldState(newx, newy, Field.State.NOTHING);
                }
            }
        } else {
            int ind[] = getBorderInd(demoPlayer.getX(), demoPlayer.getY(), newx, newy);
            if (ind[0] == 0)
                demoPlayer.setFieldBorderX(ind[1], ind[2]);
            else
                demoPlayer.setFieldBorderY(ind[1], ind[2]);
        }

        return demoPlayer;
    }

    public Player[] init(String[] names, int fieldSize) {
        int n = players.length;

        Random rnd = new Random();

        field = generateRandomField(rnd, fieldSize);
        players = new Player[n];

        Set<Integer> st = new HashSet<>();
        int pos[] = generateRandomPosition(rnd);
        for(int i = 0; i < n; i++) {
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

    // TODO something less genious :)
    public Field generateRandomField(Random random, int fieldSize) {
        Field f = new Field(fieldSize);

        for(int i = 0; i < fieldSize; i++) {
            f.addBorderX(0, i);
            f.addBorderX(fieldSize, i);
            f.addBorderY(i, 0);
            f.addBorderY(i, fieldSize);
        }

        return f;
    }

    private int[] generateRandomPosition(Random random) {
        int[] pos = new int[2];
        pos[0] = random.nextInt() % field.getSize();
        pos[1] = random.nextInt() % field.getSize();
        return pos;
    }

    public Player[] processTurnMuliplayer(Turn[] turns) {

        return players;
    }

    private boolean isBorder(int curx, int cury, int newx, int newy){
        int ind[] = getBorderInd(curx, cury, newx, newy);
        if (ind[0] == 0)
            return field.hasBorderX(ind[1], ind[2]);
        else
            return field.hasBorderY(ind[1], ind[2]);
    }

    private int[] getBorderInd(int curx, int cury, int newx, int newy) {
        if (curx == newx) {
            if (cury < newy) {
                return new int[] {0, newy, curx};
            } else {
                return new int[] {0, cury, curx};
            }
        } else {
            if (curx < newx) {
                return new int[] {1, cury, newx};
            } else {
                return new int[] {1, cury, curx};
            }
        }
    }

    private int[] getPosChange(Direction direction){
        if (direction == Direction.DOWN){
            return new int[] {0, 1};
        } else if (direction == Direction.UP){
            return new int[] {0, -1};
        } else if (direction == Direction.LEFT){
            return new int[] {-1, 0};
        } else if (direction == Direction.RIGHT){
            return new int[] {1, 0};
        }
        return new int[] {0, 0};
    }

    /**
     * Inner class Player. Contains information about player and his filed view.
     */
    public class Player{
        private int x, y, hp, cartridgesCnt, id;
        private String name;
        private Field fieldView;

        /**
         * Player constructor.
         * @param posx is players x-coordinate.
         * @param posy is players y-coordinate.
         * @param name is players name.
         * @param id is players id.
         */
        Player(int posx, int posy, String name, int id){
            this.x = posx;
            this.y = posy;
            this.name = name;
            fieldView = new Field(Model.this.field.getSize());
            cartridgesCnt = 3;

            for(int i = 0; i < fieldView.getSize(); i++) {
                for(int j = 0; j < fieldView.getSize(); j++) {
                    fieldView.setState(i, j, Field.State.UNKNOWN);
                }
            }

            fieldView.setState(x, y, Field.State.NOTHING);
        }

        /**
         * getX method, returns players x-coordinate.
         * @return players x-coordinate.
         */
        public int getX() {
            return x;
        }

        /**
         * getY method, returns players x-coordinate.
         * @return players y-coordinate.
         */
        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void spendCartridge(){
            cartridgesCnt--;
        }

        public void setFieldState(int x, int y, Field.State state){
            fieldView.setState(x, y, state);
        }

        public Field.State getFieldState(int x, int y){
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
    }

    public class Minotaur{
        private int x, y;
        private String name;

        Minotaur(int posx, int posy, String name){
            x = posx;
            y = posy;
            this.name = name;
        }
    }

    public class Turn{
        private Model.Direction moveDir;
        private Model.Direction shootDir;

        Turn(Model.Direction moveDirection, Model.Direction shootDirection) {
            moveDir = moveDirection;
            shootDir = shootDirection;
        }
    }


    /**
     * findPlayerByPosition method, returns player whose position is (x, y) or null if such player
     * doesn't exist.
     * @param x is x-coordinate of field on which we are looking for a Player.
     * @param y is y-coordinate of field on which we are looking for a Player.
     * @return player located by given coordinates or null if such player doesn't exist.
     */
}
