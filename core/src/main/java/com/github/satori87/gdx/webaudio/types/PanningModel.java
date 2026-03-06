package com.github.satori87.gdx.webaudio.types;

/**
 * Enumeration of spatialization algorithms used by a {@code PannerNode}.
 * Maps to the Web Audio API {@code PanningModelType} enumeration.
 *
 * @see PannerNode.panningModel (Web Audio API)
 */
public enum PanningModel {
    /** A simple equal-power panning algorithm. Lower quality but less CPU-intensive. */
    EQUALPOWER("equalpower"),
    /** Head-related transfer function panning for realistic 3D audio spatialization. Higher quality but more CPU-intensive. */
    HRTF("hrtf");

    private final String jsValue;

    PanningModel(String jsValue) {
        this.jsValue = jsValue;
    }

    /**
     * Returns the JavaScript string value corresponding to this panning model.
     *
     * @return the JavaScript enum string
     */
    public String toJsValue() {
        return jsValue;
    }

    /**
     * Returns the enum constant matching the given JavaScript string value.
     *
     * @param value the JavaScript enum string
     * @return the matching {@code PanningModel}
     * @throws IllegalArgumentException if the value does not match any constant
     */
    public static PanningModel fromJsValue(String value) {
        for (PanningModel model : values()) {
            if (model.jsValue.equals(value)) return model;
        }
        throw new IllegalArgumentException("Unknown PanningModel: " + value);
    }
}
