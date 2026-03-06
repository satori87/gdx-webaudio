package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NoiseTypeTest {

    @Test void whiteToJsValue() { assertEquals("white", NoiseType.WHITE.toJsValue()); }
    @Test void pinkToJsValue() { assertEquals("pink", NoiseType.PINK.toJsValue()); }
    @Test void brownianToJsValue() { assertEquals("brownian", NoiseType.BROWNIAN.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (NoiseType t : NoiseType.values()) {
            assertEquals(t, NoiseType.fromJsValue(t.toJsValue()));
        }
    }

    @Test void fromJsValueInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> NoiseType.fromJsValue("invalid"));
    }

    @Test void hasThreeValues() {
        assertEquals(3, NoiseType.values().length);
    }
}
