package ru.spbau.labyrinth;

import org.junit.Test;

import java.util.Random;

import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.field.Field;

import static org.junit.Assert.assertEquals;

public class FieldTest {
    @Test
    public void exitWallCorrect() {
        Model model = new Model();
        Random r = new Random();
        int side, pos;
        int fieldSize = 3;
        for (int i = 0; i < 100; i++) {
            side = Math.abs(r.nextInt()) % 4;
            pos = Math.abs(r.nextInt()) % (fieldSize + 1);
            //System.out.println(Integer.toString(side) + " " + Integer.toString(pos));
            model.generateRandomField(new Random(), 3);
        }
    }
}