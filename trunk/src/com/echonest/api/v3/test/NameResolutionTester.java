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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
        int notFirstMatchCount = 0;
        int noMatchCount = 0;
        int failureCount = 0;

        for (String name : names) {
            String matchName = "";
            try {
                count++;
                List<Artist> artists = echoNest.searchArtist(name, false);

                if (artists.size() > 0) {
                    matchName = artists.get(0).getName();
                    if (isMatch(name, matchName)) {
                        matchCount++;
                    } else {
                        System.out.printf("Suspicious match: '%s'   '%s'\n", name, matchName);
                        suspiciousMatchCount++;
                        for (int i = 1; i < artists.size(); i++) {
                            Artist artist = artists.get(i);
                            if (isMatch(name, artist.getName())) {
                                System.out.printf("    Best match found at position %d: '%s' \n", i, artist.getName());
                                System.out.printf("    ID is: %s \n", artist.getId());
                                String[] fields = artist.getId().split("/");
                                System.out.printf(" %s ### %s\n", name, fields[5]);
                                notFirstMatchCount++;
                            }
                        }
                    }
                } else {
                    System.out.printf("No match for: '%s' '\n", name);
                    noMatchCount++;
                }
            } catch (EchoNestException e) {
                failureCount++;
            }

            System.out.printf("    %d %d %d %d %d %d '%s' '%s'\n",
                    count, matchCount, suspiciousMatchCount, notFirstMatchCount, noMatchCount, failureCount,
                    name, matchName);
        }
        if (count > 0) {
            System.out.printf("Summary   %d %.2f %.2f %.2f %.2f %.2f\n",
                    count,
                    100F * matchCount / count,
                    100F * suspiciousMatchCount / count,
                    100F * notFirstMatchCount / count,
                    100F * noMatchCount / count,
                    100F * failureCount / count);
        }
    }

    int findBestMatch(List<Artist> artists, String name) {
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            if (isMatch(name, artist.getName())) {
                return i;
            }
        }
        return -1;
    }

    public void testResolution(File file) throws IOException {
        List<String> names = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                names.add(line.trim());
            }
        }
        in.close();
        testResolution(names);
    }

    public void testResolution(String name) {
        List<String> names = new ArrayList<String>();
        names.add(name);
        testResolution(names);
    }

    public void quickQueryTest() {
        for (int i = 0; i < 1000; i++) {
            //echoNest.getProfile("music://id.echonest.com/~/AR/AR8BUVM1187FB4D94D");
        }
    }

    private boolean isMatch(String name1, String name2) {
        // first try exact match
        if (name1.equalsIgnoreCase(name2)) {
            return true;
        }

        // next try a de-accented match
        if (Utilities.removeAccents(name1).equalsIgnoreCase(Utilities.removeAccents(name2))) {
            return true;
        }

        // then try the full dan ellis style normalization
        String nname1 = Utilities.normalize(name1);
        String nname2 = Utilities.normalize(name2);
        return nname1.equals(nname2);
    }
}
