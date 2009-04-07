/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import java.util.List;

/**
 * A simple test class. Usage:  java -DECHO_NEST_API_KEY=YOUR_KEY com.echonest.api.v3.Main 'artist name'
 * @author plamere
 */
public class Main {

    /**
     * The main entry point
     * @param args optional - contains the artist name
     */
    public static void main(String[] args) {
        try {
            ArtistAPI echoNest = new ArtistAPI();
            String artistQuery = "Weezer";
            if (args.length > 0) {
                artistQuery = args[0];
            }

            List<Artist> artists = echoNest.searchArtist(artistQuery, false);
            for (Artist artist : artists) {
                System.out.println("Artist Name: " + artist.getName());
                System.out.println("Artist ID  : " + artist.getId());
                System.out.println("Artist Familiarity: " + echoNest.getFamiliarity(artist));
                System.out.println("Artist Hotness: " + echoNest.getHotness(artist));

                System.out.println("Similar Artists: ");
                for (Scored<Artist> sartist : echoNest.getSimilarArtists(artist, 0, 15)) {
                    System.out.println("    " + sartist.getItem().getName());
                }
                System.out.println("Artist News: ");
                for (News news : echoNest.getNews(artist, 0, 15).getDocuments()) {
                    if (news.getName() != null) {
                        System.out.println("    " + news.getName());
                    } else {
                        System.out.println("    " + news.getURL());
                    }
                }

                System.out.println("Artist Reviews: ");
                for (Review review : echoNest.getReviews(artist, 0, 15).getDocuments()) {
                    if (review.getName() != null) {
                        System.out.println("    " + review.getName());
                    } else {
                        System.out.println("    " + review.getURL());
                    }
                }

                System.out.println("Artist Blogs: ");
                for (Blog blog : echoNest.getBlogs(artist, 0, 15).getDocuments()) {

                    if (blog.getName() != null) {
                        System.out.println("    " + blog.getName());
                    } else {
                        System.out.println("    " + blog.getURL());
                    }
                }
            }
        } catch (EchoNestException e) {
            System.out.println("Trouble: " + e);
        }
    }
}
