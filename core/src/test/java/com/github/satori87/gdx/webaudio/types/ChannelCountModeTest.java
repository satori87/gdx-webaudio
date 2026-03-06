package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChannelCountModeTest {

    @Test void maxJsValue() { assertEquals("max", ChannelCountMode.MAX.toJsValue()); }
    @Test void clampedMaxJsValue() { assertEquals("clamped-max", ChannelCountMode.CLAMPED_MAX.toJsValue()); }
    @Test void explicitJsValue() { assertEquals("explicit", ChannelCountMode.EXPLICIT.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (ChannelCountMode mode : ChannelCountMode.values()) {
            assertEquals(mode, ChannelCountMode.fromJsValue(mode.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ChannelCountMode.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(3, ChannelCountMode.values().length);
    }
}
