/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.EchoNestException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author plamere
 */
public class ArtistGraph {
    private List<Scored<Artist>> workQueue = new ArrayList<Scored<Artist>>();
    private ArtistAPI echoNest = new ArtistAPI();
    private Set<Artist> plottedSet = new HashSet<Artist>();

    ArtistGraph() throws EchoNestException {
    }

    public void createArtistGraph(String path, String artistName, int size) throws EchoNestException, IOException {


        PrintWriter out = new PrintWriter(path);
        out.println("digraph MusicTags {\n");
        out.println("  graph [rankdir=\"LR\"]\n");

        List<Artist> artists = echoNest.searchArtist(artistName, false);
        if (artists.size() > 0) {
            enqueue(out, artists.get(0));
        }

        while (plottedSet.size() < size && workQueue.size() > 0) {
            Scored<Artist> sartist = workQueue.remove(0);
            System.out.println("Size: " + plottedSet.size() + " artist: " + sartist.getItem().getName());
            List<Scored<Artist>> simArtists = echoNest.getSimilarArtists(sartist.getItem(), 0, 6);

            double familiarity = sartist.getScore();
            for (Scored<Artist> scoredArtist : simArtists) {
                Artist similarArtist = scoredArtist.getItem();
                float simFamiliarity = echoNest.getFamiliarity(similarArtist);
                if (simFamiliarity < familiarity) {
                    out.printf("\"%s\" -> \"%s\";\n", sartist.getItem().getId(), similarArtist.getId());
                    if (!plottedSet.contains(similarArtist)) {
                        enqueue(out, similarArtist, simFamiliarity);
                    }
                }
            }
        }
        out.println("}\n");
        out.close();
    }

    public void simpleArtistGraph(String path, String artistName, int size) throws EchoNestException, IOException {

        List<Artist> workQueue = new ArrayList<Artist>();
        Set<Artist> plottedSet = new HashSet<Artist>();

        PrintWriter out = new PrintWriter(path);
        out.println("digraph ArtistTags {\n");
        out.println("  graph [rankdir=\"LR\"]\n");

        List<Artist> artists = echoNest.searchArtist(artistName, false);
        if (artists.size() > 0) {
            workQueue.add(artists.get(0));
        }

        while (workQueue.size() > 0) {
            Artist artist = workQueue.remove(0);

            List<Scored<Artist>> simArtists = echoNest.getSimilarArtists(artist, 0, 6);
            float familiarity = echoNest.getFamiliarity(artist);

            for (Scored<Artist> scoredArtist : simArtists) {
                Artist similarArtist = scoredArtist.getItem();
                float simFamiliarity = echoNest.getFamiliarity(similarArtist);
                if (simFamiliarity < familiarity) {
                    out.printf("\"%s\" -> \"%s\";\n", artist.getId(), similarArtist.getId());
                    if (!plottedSet.contains(similarArtist)) {
                        workQueue.add(similarArtist);
                        plottedSet.add(similarArtist);
                        out.printf("\"%s\" [label=\"%s\"]\n", similarArtist.getId(), similarArtist.getName());
                    }
                }
            }
        }
        out.println("}\n");
        out.close();
    }

    private void enqueue(PrintWriter out, Artist artist, float familiarity) {
        workQueue.add(new Scored<Artist>(artist, familiarity));
        plottedSet.add(artist);
        out.printf("\"%s\" [label=\"%s\"]\n", artist.getId(), artist.getName());
    }

    private void enqueue(PrintWriter out, Artist artist) throws EchoNestException {
        float familiarity = echoNest.getFamiliarity(artist);
        enqueue(out, artist, familiarity);
    }

    public static void main(String[] args) throws Exception {
        ArtistGraph ag = new ArtistGraph();
        ag.createArtistGraph("md4.dot", "Led Zeppelin", 80);
    }
}
