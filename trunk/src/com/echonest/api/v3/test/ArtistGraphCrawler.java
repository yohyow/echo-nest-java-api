/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.test;

import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.artist.EchoNestException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author plamere
 */
public class ArtistGraphCrawler {
    private ArtistAPI echoNest;

    ArtistGraphCrawler(ArtistAPI en) {
        this.echoNest = en;
    }

    public void crawl(String path) {
        Set<String> visited = new HashSet<String>();
        List<Artist> todo = new LinkedList<Artist>();

        try {
            List<Scored<Artist>> seeds = echoNest.getTopHotttArtists(15);
            for (Scored<Artist> sartist : seeds) {
                todo.add(sartist.getItem());
            }
        } catch (EchoNestException ee) {
            System.err.println("Can't get top hott artists");
            return;
        }

        PrintWriter out = null;
        int count = 0;
        try {
            out = new PrintWriter(path);
            while (todo.size() > 0) {
                Artist artist = todo.remove(0);
                if (!visited.contains(artist.getId())) {
                    visited.add(artist.getId());
                    try {
                        float familiarity = echoNest.getFamiliarity(artist.getId());
                        float hotness = echoNest.getHotness(artist.getId());
                        System.out.printf("Visited:%d/%d  Familiarity %6.4f Hotness: %6.4f Artist:%s\n",
                                visited.size(), todo.size(), familiarity, hotness, artist.toString());
                        List<Scored<Artist>> similar = echoNest.getSimilarArtists(artist.getId(), 0, 15);
                        for (Scored<Artist> sim : similar) {
                            Artist simArtist = sim.getItem();
                            System.out.println("   " + sim.getItem().getName());
                            if (!visited.contains(sim.getItem().getId())) {
                                todo.add(sim.getItem());
                            }
                            out.printf("%s|%s|%6.4f|%6.4f|%s|%s\n", artist.getId(), artist.getName(), familiarity, hotness,
                                    simArtist.getId(), simArtist.getName());
                            out.flush();
                        }
                    } catch (EchoNestException ioe) {
                        System.out.println("Call failed for " + artist + ", skipping");
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }

                if (++count % 10 == 0) {
                    echoNest.showStats();
                }
            }
        } catch (IOException ioe) {
            System.err.println("Can't write to file " + ioe);
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) throws EchoNestException {
        ArtistAPI echoNest = new ArtistAPI();
        //echoNest.setTrace(true);
        ArtistGraphCrawler agc = new ArtistGraphCrawler(echoNest);
        agc.crawl("crawl.dat");
    //nrt.testResolution(new File("artist1000.txt"));
    }
}
