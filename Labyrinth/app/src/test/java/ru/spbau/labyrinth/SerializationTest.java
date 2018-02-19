package ru.spbau.labyrinth;

import org.junit.Test;

import ru.spbau.labyrinth.model.Model;
import ru.spbau.labyrinth.model.field.Field;

import static org.junit.Assert.assertEquals;

public class SerializationTest {
    @Test
    public void turnSerializeCorrect() {
        Model.Turn turn1 = new Model.Turn(Model.Direction.UP, Model.Direction.UP, 0);
        String json = Model.Turn.serialize(turn1);
        Model.Turn turn2 = Model.Turn.deserialize(json);
        assertEquals(turn1.getMoveDir(), turn2.getMoveDir());
        assertEquals(turn1.getShootDir(), turn2.getShootDir());
        assertEquals(turn1.getId(), turn2.getId());
    }

    @Test
    public void fieldSerializeCorrect() {
        Model model = new Model();
        Field field1 = model.generateRandomField(null, 3);
        String json = Field.serialize(field1);
        Field field2 = Field.deserialize(json);
        assertEquals(field1.getSize(), field2.getSize());
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(field1.getState(i, j), field2.getState(i, j));
    }
}