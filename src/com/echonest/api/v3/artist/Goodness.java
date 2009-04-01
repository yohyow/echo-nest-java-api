/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.artist;

/**
 *
 * @author plamere
 */
public class Goodness {
    private float goodness;
    private String instaCritic;

    public Goodness(float goodness, String instaCritic) {
        this.goodness = goodness;
        this.instaCritic = instaCritic;
    }

    public float getGoodness() {
        return goodness;
    }

    public String getInstaCritic() {
        return instaCritic;
    }

    public String toString() {
        return "Goodness: " + goodness + " Critique: " + instaCritic;
    }

}
