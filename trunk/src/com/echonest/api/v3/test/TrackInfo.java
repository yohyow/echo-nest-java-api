/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.test;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author plamere
 */
public class TrackInfo implements Serializable {
    private static final long serialVersionUID = -2683894576845479019L;
    private String title;
    private String album;
    private String artist;
    private String id3Genre;
    private String year;
    private int trackNumber;
    private File file;
    private boolean isValid;


    public void dump() {
        System.out.println("  File:   " + file);
        System.out.println("  Title:  " + title);
        System.out.println("  Album:  " + album);
        System.out.println("  Artist: " + artist);
        System.out.println("  Genre:  " + id3Genre);
        System.out.println("  Year:   " + year);
        System.out.println("  track#: " + trackNumber);
        System.out.println("  valid:  " + isValid);
    }

    public String toString() {
        return title + " " + album  + " " + artist + " " + id3Genre + " " + isValid + " " + year + " " + trackNumber + " " + file ;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getId3Genre() {
        return id3Genre;
    }

    public void setId3Genre(String id3Genre) {
        this.id3Genre = id3Genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrackInfo other = (TrackInfo) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.album == null) ? (other.album != null) : !this.album.equals(other.album)) {
            return false;
        }
        if ((this.artist == null) ? (other.artist != null) : !this.artist.equals(other.artist)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }


}
