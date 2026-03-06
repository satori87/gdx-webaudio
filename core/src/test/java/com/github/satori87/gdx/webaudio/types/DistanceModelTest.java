package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceModelTest {

    @Test void linearJsValue() { assertEquals("linear", DistanceModel.LINEAR.toJsValue()); }
    @Test void inverseJsValue() { assertEquals("inverse", DistanceModel.INVERSE.toJsValue()); }
    @Test void exponentialJsValue() { assertEquals("exponential", DistanceModel.EXPONENTIAL.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (DistanceModel model : DistanceModel.values()) {
            assertEquals(model, DistanceModel.fromJsValue(model.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> DistanceModel.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(3, DistanceModel.values().length);
    }
}
