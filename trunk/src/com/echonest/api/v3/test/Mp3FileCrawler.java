/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author plamere
 */
public class Mp3FileCrawler {
    private TrackIdentifier trackIdentifier;
    private List<TrackInfo> infos = new ArrayList<TrackInfo>();


    public Mp3FileCrawler() {
        trackIdentifier = new TrackIdentifier();
    }

    public void addFile(File file) {
        if (file.getName().toLowerCase().endsWith(".mp3")) {
            TrackInfo trackInfo = trackIdentifier.identifyTrack(file);
            infos.add(trackInfo);
        }
    }

    public void addFile(File file, boolean recursive) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (recursive && f.isDirectory()) {
                        addFile(f, recursive);
                    } else {
                        addFile(f);
                    }
                }
            }
        } else {
            addFile(file);
        }
    }

    public Set<String> getArtistNames() {
        Set<String> artistNames = new HashSet<String>();
        for (TrackInfo ti : infos) {
            artistNames.add(ti.getArtist());
        }
        return artistNames;
    }
}
