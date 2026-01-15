package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe enuma Color.
 */
public class ColorTest {

    @Test
    public void opponent_blackReturnsWhite() {
        assertEquals(Color.WHITE, Color.BLACK.opponent());
    }

    @Test
    public void opponent_whiteReturnsBlack() {
        assertEquals(Color.BLACK, Color.WHITE.opponent());
    }

    @Test
    public void opponent_emptyReturnsEmpty() {
        assertEquals(Color.EMPTY, Color.EMPTY.opponent());
    }

    @Test
    public void enum_containsExactlyThreeValues() {
        Color[] values = Color.values();

        assertEquals(3, values.length);
        assertTrue(contains(values, Color.BLACK));
        assertTrue(contains(values, Color.WHITE));
        assertTrue(contains(values, Color.EMPTY));
    }

    private boolean contains(Color[] values, Color c) {
        for (Color v : values) {
            if (v == c) return true;
        }
        return false;
    }
}
