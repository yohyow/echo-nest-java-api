/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import com.echonest.api.util.Utilities;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.EchoNestException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paul
 */
public class NameResolutionTester {

    private ArtistAPI echoNest;

    NameResolutionTester(ArtistAPI en) {
        this.echoNest = en;
    }

    public void testResolution(List<String> names) {
        int count = 0;
        int matchCount = 0;
        int suspiciousMatchCount = 0;
        int noMatchCount = 0;
        int failureCount = 0;

        for (String name : names) {
            try {
                count++;
                List<Artist> artists = echoNest.searchArtist(name, false);
                if (artists.size() > 0) {
                    String matchName = artists.get(0).getName();
                    if (isMatch(name, matchName)) {
                        matchCount++;
                    } else {
                        System.out.printf("Suspicious match: '%s'   '%s'\n", name, matchName);
                        suspiciousMatchCount++;
                    }
                } else {
                    noMatchCount++;
                }
            } catch (EchoNestException e) {
                failureCount++;
            }

            System.out.printf("%d %d %d %d %d %s\n",
                    count, matchCount, suspiciousMatchCount, noMatchCount, failureCount, name);
        }
    }

    public void testResolution(File file) throws IOException {
        List<String> names = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                names.add(line.trim());
            }
        }
        in.close();
        testResolution(names);
    }


    public void quickQueryTest() {
        for (int i = 0; i < 1000; i++) {
            //echoNest.getProfile("music://id.echonest.com/~/AR/AR8BUVM1187FB4D94D");

        }
    }

    private boolean isMatch(String name1, String name2) {
        if (name1.equalsIgnoreCase(name2)) {
            return true;
        }
        String nname1 = Utilities.normalize(name1);
        String nname2 = Utilities.normalize(name2);

        return nname1.equals(nname2);
    }
}
