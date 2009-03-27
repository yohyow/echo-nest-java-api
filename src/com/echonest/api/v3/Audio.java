/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3;

import java.util.Date;
import org.w3c.dom.Element;

/**
 * Represents an audio file
 * @author plamere
 */
public class Audio extends Document {
    private static String[] fields = {"artist_id", "artist", "release",  "title", "url", "link", "date", "length" };
    private Artist artist = null;

    Audio(Element element) throws EchoNestException {
        super(element, fields);
        artist = new Artist(getArtistName(), getArtistID());
    }

    /**
     * Gets the artist
     * @return the artist
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     * Gets the artist name
     * @return the artist name
     */
    public String getArtistName() {
        return get("artist");
    }

    /**
     * Gets the artist id
     * @return the artist id
     */
    public String getArtistID() {
        return get("artist_id");
    }

    /**
     * Gets release (album) information
     * @return the release name
     */
    public String getRelease() {
        return get("release");
    }

    /**
     * Gets the audio title 
     * @return the title
     */
    public String getTitle() {
        return get("title");
    }

    /**
     * Gets the audio link
     * @return the link
     */
    public String getLink() {
        return get("link");
    }

    /**
     * Gets the date of the audio
     * @return the date
     */
    public Date getDate() {
        return getDate("date");
    }


    /**
     * Gets the URL of the audio
     * @return the URL
     */
    public String getUrl() {
        return get("url");
    }

    /**
     * Gets the length (in seconds) of the audio
     * @return the length of the audio
     */
    public float getLength() {
        return getFloat("length");
    }
}
