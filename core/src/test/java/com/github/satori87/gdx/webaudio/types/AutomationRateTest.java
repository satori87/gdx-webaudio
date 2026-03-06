package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AutomationRateTest {

    @Test void aRateJsValue() { assertEquals("a-rate", AutomationRate.A_RATE.toJsValue()); }
    @Test void kRateJsValue() { assertEquals("k-rate", AutomationRate.K_RATE.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (AutomationRate rate : AutomationRate.values()) {
            assertEquals(rate, AutomationRate.fromJsValue(rate.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> AutomationRate.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(2, AutomationRate.values().length);
    }
}
