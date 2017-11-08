package ru.spbau.labyrinth.model;

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
        demoPlayer = new Player(2, 2, "Mr. Smith");
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
        private int x, y, hp, cartridgesCnt;
        private String name;
        private Field fieldView;

        /**
         * Player constructor.
         * @param posx is players x-coordinate.
         * @param posy is players y-coordinate.
         * @param name is players name.
         */
        Player(int posx, int posy, String name){
            this.x = posx;
            this.y = posy;
            this.name = name;
            fieldView = new Field(Model.this.field.getSize());
            cartridgesCnt = 3;
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
            if (cartridgesCnt > 0) {
                cartridgesCnt--;
            }
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

        public int getCartridgesCnt() {
            return cartridgesCnt;
        }

        public boolean getFieldBorderX(int row, int column) {
            return fieldView.hasBorderX(row, column);
        }

        public boolean getFieldBorderY(int row, int column) {
            return fieldView.hasBorderY(row, column);
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


    /**
     * findPlayerByPosition method, returns player whose position is (x, y) or null if such player
     * doesn't exist.
     * @param x is x-coordinate of field on which we are looking for a Player.
     * @param y is y-coordinate of field on which we are looking for a Player.
     * @return player located by given coordinates or null if such player doesn't exist.
     */
    /*
    public Player findPlayerByPosition(int x, int y){
        for(Player player: players) {
            if (player.x == x && player.y == y){
                return player;
            }
        }
        return null;
    }*/
}
