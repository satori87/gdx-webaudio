package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OverSampleTypeTest {

    @Test void noneJsValue() { assertEquals("none", OverSampleType.NONE.toJsValue()); }
    @Test void twoXJsValue() { assertEquals("2x", OverSampleType.TWO_X.toJsValue()); }
    @Test void fourXJsValue() { assertEquals("4x", OverSampleType.FOUR_X.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (OverSampleType type : OverSampleType.values()) {
            assertEquals(type, OverSampleType.fromJsValue(type.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> OverSampleType.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(3, OverSampleType.values().length);
    }
}
