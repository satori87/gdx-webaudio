package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChannelInterpretationTest {

    @Test void speakersJsValue() { assertEquals("speakers", ChannelInterpretation.SPEAKERS.toJsValue()); }
    @Test void discreteJsValue() { assertEquals("discrete", ChannelInterpretation.DISCRETE.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (ChannelInterpretation interp : ChannelInterpretation.values()) {
            assertEquals(interp, ChannelInterpretation.fromJsValue(interp.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ChannelInterpretation.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(2, ChannelInterpretation.values().length);
    }
}
