/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.EchoNestException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    private final static String SEP = "<sep>";

    ArtistGraphCrawler(ArtistAPI en) {
        this.echoNest = en;
    }

    public void crawl(String prev, String path) {
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

            loadPrevious(prev, visited, todo, out);

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
                            out.printf("%s%s%s%s%6.4f%s%6.4f%s%s%s%s\n", artist.getId(),
                                    SEP, artist.getName(),
                                    SEP, familiarity,
                                    SEP, hotness,
                                    SEP, simArtist.getId(),
                                    SEP, simArtist.getName());
                            out.flush();
                        }
                    } catch (EchoNestException ioe) {
                        System.out.println("Call failed for " + artist + ", skipping");
                    }
                }
                try {
                    Thread.sleep(10L);
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

    void loadPrevious(String path, Set<String> visited, List<Artist> todo, PrintWriter out) throws IOException {
        if (path != null) {
            File p = new File(path);
            if (p.canRead()) {
                BufferedReader r = new BufferedReader(new FileReader(path));

                String line;

                while ((line = r.readLine()) != null) {
                    String[] fields = line.split(SEP);
                    if (fields.length == 6) {
                        visited.add(fields[0]);
                        todo.add(new Artist(fields[5], fields[4]));
                    }
                    out.println(line);
                }
                r.close();
                System.out.println("Restored " + visited.size() + " artists, and " + todo.size() + " toDos");
            }
        }
    }

    public static void main(String[] args) throws EchoNestException {
        ArtistAPI echoNest = new ArtistAPI();
        echoNest.setMaxCacheTime(0);
        //echoNest.setTrace(true);
        ArtistGraphCrawler agc = new ArtistGraphCrawler(echoNest);

        File prev = new File("crawl.dat");
        if (prev.exists()) {
            prev.renameTo(new File("crawl.dat.prev"));
            agc.crawl("crawl.dat.prev", "crawl.dat");
        } else {
            agc.crawl(null, "crawl.dat");
        }
    //nrt.testResolution(new File("artist1000.txt"));
    }
}
