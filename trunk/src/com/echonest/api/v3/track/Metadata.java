/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

import com.echonest.api.v3.track.TrackAPI.AnalysisStatus;

/**
 * Represents a track ID3 metadata
 */
public class Metadata {
    private TrackAPI.AnalysisStatus status;
    private String artist = "";
    private String release = "";
    private String title = "";
    private String genre = "";
    private float duration = 0;
    private int samplerate = 0;
    private int bitrate = 0;

    /**
     * Gets the artist name
     * @return the artist name
     */
    public String getArtist() {
        return artist;
    }

    void setArtist(String artist) {
        this.artist = artist;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    /**
     * Gets the bitrate
     * @return the bitrate
     */
    public int getBitrate() {
        return bitrate;
    }

    void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * Gets the track duration
     * @return the duration in seconds
     */
    public float getDuration() {
        return duration;
    }

    void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Gets the genre
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Gets the release (album) name
     * @return the release name
     */
    public String getRelease() {
        return release;
    }

    void setRelease(String release) {
        this.release = release;
    }

    /**
     * Gets the sample rate
     * @return the sample rate
     */
    public int getSamplerate() {
        return samplerate;
    }

    void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    /**
     * Gets the track title 
     * @return the track title
     */
    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Status    : " + status + "\n");
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
