/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class Metadata {
    private String artist = "";
    private String release = "";
    private String title = "";
    private String genre = "";
    private float duration = 0;
    private int samplerate = 0;
    private int bitrate = 0;

    public String getArtist() {
        return artist;
    }

    void setArtist(String artist) {
        this.artist = artist;
    }

    public int getBitrate() {
        return bitrate;
    }

    void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public float getDuration() {
        return duration;
    }

    void setDuration(float duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRelease() {
        return release;
    }

    void setRelease(String release) {
        this.release = release;
    }

    public int getSamplerate() {
        return samplerate;
    }

    void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (artist != null) {
            sb.append("Artist    : " + artist + "\n");
        }
        if (release != null) {
            sb.append("Release   : " + release + "\n");
        }

        if (title != null) {
            sb.append("Title     : " + title + "\n");
        }

        if (genre != null) {
            sb.append("Genre     : " + genre + "\n");
        }

        sb.append("Duration  : " + duration + "\n");
        sb.append("Samplerate: " + samplerate + "\n");
        sb.append("Bitrate   : " + bitrate + "\n");
        return sb.toString();
    }
}
