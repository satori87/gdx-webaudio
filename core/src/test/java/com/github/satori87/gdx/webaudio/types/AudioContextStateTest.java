package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AudioContextStateTest {

    @Test void suspendedJsValue() { assertEquals("suspended", AudioContextState.SUSPENDED.toJsValue()); }
    @Test void runningJsValue() { assertEquals("running", AudioContextState.RUNNING.toJsValue()); }
    @Test void closedJsValue() { assertEquals("closed", AudioContextState.CLOSED.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (AudioContextState state : AudioContextState.values()) {
            assertEquals(state, AudioContextState.fromJsValue(state.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> AudioContextState.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(3, AudioContextState.values().length);
    }
}
