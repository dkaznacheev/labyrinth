package ru.spbau.labyrinth.model;

import ru.spbau.labyrinth.model.field.*;

/**
 * class Model. Contains game model.
 */
public class Model {
    private Player[] players;
    private Field field;

    /**
     * Nested class Player. Contains information about player and his filed view.
     */
    public class Player{
        private int x, y;
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
    }

    /**
     * findPlayerByPosition method, returns player whose position is (x, y) or null if such player
     * doesn't exist.
     * @param x is x-coordinate of field on which we are looking for a Player.
     * @param y is y-coordinate of field on which we are looking for a Player.
     * @return player located by given coordinates or null if such player doesn't exist.
     */
    public Player findPlayerByPosition(int x, int y){
        for(Player player: players) {
            if (player.x == x && player.y == y){
                return player;
            }
        }
        return null;
    }
}
