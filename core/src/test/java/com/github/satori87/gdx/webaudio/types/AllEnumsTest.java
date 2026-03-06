package com.github.satori87.gdx.webaudio.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests verifying that all enum types follow the expected pattern:
 * non-null jsValue, successful round-trip via fromJsValue(toJsValue()), and
 * proper exception on invalid input.
 */
class AllEnumsTest {

    @Test void allOscillatorTypesRoundTrip() {
        for (OscillatorType v : OscillatorType.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, OscillatorType.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allBiquadFilterTypesRoundTrip() {
        for (BiquadFilterType v : BiquadFilterType.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, BiquadFilterType.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allDistanceModelsRoundTrip() {
        for (DistanceModel v : DistanceModel.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, DistanceModel.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allPanningModelsRoundTrip() {
        for (PanningModel v : PanningModel.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, PanningModel.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allChannelCountModesRoundTrip() {
        for (ChannelCountMode v : ChannelCountMode.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, ChannelCountMode.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allChannelInterpretationsRoundTrip() {
        for (ChannelInterpretation v : ChannelInterpretation.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, ChannelInterpretation.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allOverSampleTypesRoundTrip() {
        for (OverSampleType v : OverSampleType.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, OverSampleType.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allAudioContextStatesRoundTrip() {
        for (AudioContextState v : AudioContextState.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, AudioContextState.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allAutomationRatesRoundTrip() {
        for (AutomationRate v : AutomationRate.values()) {
            assertNotNull(v.toJsValue());
            assertFalse(v.toJsValue().isEmpty());
            assertEquals(v, AutomationRate.fromJsValue(v.toJsValue()));
        }
    }

    @Test void allEnumsRejectNull() {
        assertThrows(Exception.class, () -> OscillatorType.fromJsValue(null));
        assertThrows(Exception.class, () -> BiquadFilterType.fromJsValue(null));
        assertThrows(Exception.class, () -> DistanceModel.fromJsValue(null));
        assertThrows(Exception.class, () -> PanningModel.fromJsValue(null));
        assertThrows(Exception.class, () -> ChannelCountMode.fromJsValue(null));
        assertThrows(Exception.class, () -> ChannelInterpretation.fromJsValue(null));
        assertThrows(Exception.class, () -> OverSampleType.fromJsValue(null));
        assertThrows(Exception.class, () -> AudioContextState.fromJsValue(null));
        assertThrows(Exception.class, () -> AutomationRate.fromJsValue(null));
    }
}
