/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class IntWithConfidence {
    private float confidence;
    private int value;

    public IntWithConfidence(float confidence, int value) {
        this.confidence = confidence;
        this.value = value;
    }

    public float getConfidence() {
        return confidence;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return value + " (" + confidence + ")";
    }
}
