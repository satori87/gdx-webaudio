package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BiquadFilterTypeTest {

    @Test void lowpassJsValue() { assertEquals("lowpass", BiquadFilterType.LOWPASS.toJsValue()); }
    @Test void highpassJsValue() { assertEquals("highpass", BiquadFilterType.HIGHPASS.toJsValue()); }
    @Test void bandpassJsValue() { assertEquals("bandpass", BiquadFilterType.BANDPASS.toJsValue()); }
    @Test void lowshelfJsValue() { assertEquals("lowshelf", BiquadFilterType.LOWSHELF.toJsValue()); }
    @Test void highshelfJsValue() { assertEquals("highshelf", BiquadFilterType.HIGHSHELF.toJsValue()); }
    @Test void peakingJsValue() { assertEquals("peaking", BiquadFilterType.PEAKING.toJsValue()); }
    @Test void notchJsValue() { assertEquals("notch", BiquadFilterType.NOTCH.toJsValue()); }
    @Test void allpassJsValue() { assertEquals("allpass", BiquadFilterType.ALLPASS.toJsValue()); }

    @Test void fromJsValueRoundTrip() {
        for (BiquadFilterType type : BiquadFilterType.values()) {
            assertEquals(type, BiquadFilterType.fromJsValue(type.toJsValue()));
        }
    }

    @Test void fromJsValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> BiquadFilterType.fromJsValue("invalid"));
    }

    @Test void valuesCount() {
        assertEquals(8, BiquadFilterType.values().length);
    }
}
