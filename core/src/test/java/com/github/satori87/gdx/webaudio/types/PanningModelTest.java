package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PanningModelTest {

    @Test void equalpowerJsValue() { assertEquals("equalpower", PanningModel.EQUALPOWER.toJsValue()); }
    @Test void hrtfJsValue() { assertEquals("hrtf", PanningModel.HRTF.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (PanningModel model : PanningModel.values()) {
            assertEquals(model, PanningModel.fromJsValue(model.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PanningModel.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(2, PanningModel.values().length);
    }
}
