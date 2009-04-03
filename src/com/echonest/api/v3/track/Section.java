/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class Section {
    float start;
    float duration;

    public Section(float start, float duration) {
        this.start = start;
        this.duration = duration;
    }

    public float getDuration() {
        return duration;
    }

    public float getStart() {
        return start;
    }

    public String toString() {
        return "  Start: " + start + "  Dur:" + duration;
    }
}
