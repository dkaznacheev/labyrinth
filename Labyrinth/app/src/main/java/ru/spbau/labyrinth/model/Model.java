package ru.spbau.labyrinth.model;

import android.content.pm.FeatureInfo;

import java.io.File;
import java.security.PrivateKey;

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

    public void demoInit(){
        field = new Field(5);
        demoPlayer = new Player(2, 2, "Mr. Smith");
        demoPlayer = new Player(0, 0, "Deadline");
        demoPlayer.setFieldState(0, 0, Field.State.MINOTAUR);
        field.setState(0, 0, Field.State.MINOTAUR);

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if (i != 2 || j != 2)
                    demoPlayer.setFieldState(i, j, Field.State.UNKNOWN);
            }
        }
    }

    public Player processTurn(Model.Direction moveDir, Model.Direction shootDir) {
        int[] d = getPosChage(moveDir);
        int newx = demoPlayer.getX() + d[0];
        int newy = demoPlayer.getY() + d[1];
        demoPlayer.setX(newx);
        demoPlayer.setY(newy);
        if (shootDir != Direction.NONE) {
            demoPlayer.spendCartridge();
        }
        if (demoPlayer.getFieldState(newx, newy) == Field.State.UNKNOWN) {
            if (newx == 0 && newy == 0){
                demoPlayer.setFieldState(newx, newy, Field.State.MINOTAUR);
            } else {
                demoPlayer.setFieldState(newx, newy, Field.State.NOTHING);
            }
        }

        return demoPlayer;
    }

    private int[] getPosChage(Direction direction){
        if (direction == Direction.DOWN){
            return new int[] {0, -1};
        } else if (direction == Direction.UP){
            return new int[] {0, 1};
        } else if (direction == Direction.LEFT){
            return new int[] {-1, 0};
        } else if (direction == Direction.RIGHT){
            return new int[] {0, 1};
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
            cartridgesCnt--;
        }

        public void setFieldState(int x, int y, Field.State state){
            fieldView.setState(x, y, state);
        }

        public Field.State getFieldState(int x, int y){
            return fieldView.getState(x, y);
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
