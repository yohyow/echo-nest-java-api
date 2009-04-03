/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class FloatWithConfidence {
    private float confidence;
    private float value;

    public FloatWithConfidence(float confidence, float value) {
        this.confidence = confidence;
        this.value = value;
    }

    public float getConfidence() {
        return confidence;
    }

    public float getValue() {
        return value;
    }

    public String toString() {
        return value + " (" + confidence + ")";
    }
}
