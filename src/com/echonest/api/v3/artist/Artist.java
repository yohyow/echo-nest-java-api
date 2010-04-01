/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

import java.io.Serializable;

/**
 * Represents an Echo Nest artist.
 *
 * Sten -- Added familiarity and hotness for bucket support.
 *
 * @author plamere
 */
public class Artist implements Serializable {
    private static final long serialVersionUID = 7654321L;
    private String name;
    private String id;

    private float familiarity;
    private float hotness;

    /**
     * Creates an artist
     * @param name the name of the artist
     * @param id the id of the artist
     */
    public Artist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Gets the ID of the artist
     * @return the artist ID
     */
    public String getId() {
        return id;
    }


    /**
     * Gets the name of the artist
     * @return the artist name
     */
    public String getName() {
        return name;
    }

    public float getFamiliarity() {
        return familiarity;
    }

    public float getHotness() {
        return hotness;
    }

    void setFamiliarity(float familiarity) {
        this.familiarity = familiarity;
    }

    void setHotness(float hotness) {
        this.hotness = hotness;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Artist other = (Artist) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }

}
