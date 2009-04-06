/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 * Represents an integer with confidence
 */
public class IntWithConfidence {
    private float confidence;
    private int value;

    /**
     * Constructs in IntWithConfidence
     * @param confidence the confidence
     * @param value the associated value
     */
    public IntWithConfidence(float confidence, int value) {
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
    public int getValue() {
        return value;
    }

    public String toString() {
        return value + " (" + confidence + ")";
    }
}
