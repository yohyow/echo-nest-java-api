package com.echonest.api.v3.artist;

/**
 * An Enum for specifying artist "buckets". Currently only Hotness and Familarity are supported.
 *
 * @author Sten Anderson
 */
public enum Bucket {

    HOTNESS ("hotttnesss"),
    FAMILIARITY ("familiarity");


    private final String name;

    Bucket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
