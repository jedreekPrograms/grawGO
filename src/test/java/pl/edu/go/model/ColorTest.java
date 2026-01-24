package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorTest {

    @Test
    public void opponent_blackIsWhite() {
        assertEquals(Color.WHITE, Color.BLACK.opponent());
    }

    @Test
    public void opponent_whiteIsBlack() {
        assertEquals(Color.BLACK, Color.WHITE.opponent());
    }

    @Test
    public void opponent_emptyIsEmpty() {
        assertEquals(Color.EMPTY, Color.EMPTY.opponent());
    }

    @Test
    public void enumValues_containsAllColors() {
        Color[] values = Color.values();

        assertEquals(3, values.length);
        assertArrayEquals(
                new Color[]{Color.BLACK, Color.WHITE, Color.EMPTY},
                values
        );
    }

    @Test
    public void valueOf_returnsCorrectEnum() {
        assertEquals(Color.BLACK, Color.valueOf("BLACK"));
        assertEquals(Color.WHITE, Color.valueOf("WHITE"));
        assertEquals(Color.EMPTY, Color.valueOf("EMPTY"));
    }
}
