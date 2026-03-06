package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OscillatorTypeTest {

    @Test void sineJsValue() { assertEquals("sine", OscillatorType.SINE.toJsValue()); }
    @Test void squareJsValue() { assertEquals("square", OscillatorType.SQUARE.toJsValue()); }
    @Test void sawtoothJsValue() { assertEquals("sawtooth", OscillatorType.SAWTOOTH.toJsValue()); }
    @Test void triangleJsValue() { assertEquals("triangle", OscillatorType.TRIANGLE.toJsValue()); }
    @Test void customJsValue() { assertEquals("custom", OscillatorType.CUSTOM.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (OscillatorType type : OscillatorType.values()) {
            assertEquals(type, OscillatorType.fromJsValue(type.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> OscillatorType.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(5, OscillatorType.values().length);
    }
}
