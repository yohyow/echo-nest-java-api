/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 * Represents a floating number with an associated confidence
 */
public class FloatWithConfidence {
    private float confidence;
    private float value;

    /**
     * Creates the FloatWithConfidence
     * @param confidence the confidence
     * @param value the value
     */
    public FloatWithConfidence(float confidence, float value) {
        this.confidence = confidence;
        this.value = value;
    }

    /**
     * Gets the confidence
     * @return the confidence
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * Gets the value
     * @return the value
     */
    public float getValue() {
        return value;
    }

    public String toString() {
        return value + " (" + confidence + ")";
    }
}
