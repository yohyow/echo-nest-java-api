/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v3.artist;


import java.io.Serializable;

/**
 * Represents a generic item with a score
 * 
 * @param T the type of the scored item.
 */

public class Scored<T> implements Serializable {
    private T item;
    private double score;

    /**
     * Creates a scored item
     * @param item the item
     * @param score the score
     */
    public Scored(T item, double score) {
        this.item = item;
        this.score = score;
    }

    /**
     * Gets the item
     * @return the item
     */
    public T getItem() {
        return item;
    }
    
    @Override
    public String toString() {
        return String.format("<%.3f, %s>", score, item.toString());
    }

    /**
     * Gets the score of the item
     * @return  the score for the scored item
     */
    public double getScore() {
        return score;
    }
}
