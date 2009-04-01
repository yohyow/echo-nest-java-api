/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

/**
 *
 * @author plamere
 */
public class TrackStatus {
    private String id;
    private String md5;
    private boolean ready;

    public TrackStatus(String id, String md5, boolean ready) {
        this.id = id;
        this.md5 = md5;
        this.ready = ready;
    }

    public String getID() {
        return id;
    }

    public String getMD5() {
        return md5;
    }

    public boolean isReady() {
        return ready;
    }

    public String toString() {
        return id + " " + md5 + " " + ready;
    }

}
