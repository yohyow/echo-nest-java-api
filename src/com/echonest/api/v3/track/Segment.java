/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class Segment {
    private float start;
    private float duration;
    private float startLoudness;
    private float maxLoudness;
    private float maxLoudnessTimeOffset;
    private float[] pitches;
    private float[] timbre;


    public float getDuration() {
        return duration;
    }

    void setDuration(float duration) {
        this.duration = duration;
    }

    public float getMaxLoudness() {
        return maxLoudness;
    }

    void setMaxLoudness(float maxLoudness) {
        this.maxLoudness = maxLoudness;
    }

    public float getMaxLoudnessTimeOffset() {
        return maxLoudnessTimeOffset;
    }

    void setMaxLoudnessTimeOffset(float maxLoudnessTimeOffset) {
        this.maxLoudnessTimeOffset = maxLoudnessTimeOffset;
    }

    public float[] getPitches() {
        return pitches;
    }

    void setPitches(float[] pitches) {
        this.pitches = pitches;
    }

    public float getStart() {
        return start;
    }

    void setStart(float start) {
        this.start = start;
    }

    public float getStartLoudness() {
        return startLoudness;
    }

    void setStartLoudness(float startLoudness) {
        this.startLoudness = startLoudness;
    }

    public float[] getTimbre() {
        return timbre;
    }

    void setTimbre(float[] timbre) {
        this.timbre = timbre;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start: " + start + " Dur: " + duration + "\n");
        sb.append("Loudness: " + startLoudness + " max " + maxLoudness + " at " + maxLoudnessTimeOffset +"\n");

        sb.append("Pitches: ");
        for (int i = 0; i < pitches.length; i++) {
            sb.append(pitches[i] + " ");
        }
        sb.append("\n");

        sb.append("Timbre: ");
        for (int i = 0; i < timbre.length; i++) {
            sb.append(timbre[i] + " ");
        }
        sb.append("\n");

        return sb.toString();
    }
}
