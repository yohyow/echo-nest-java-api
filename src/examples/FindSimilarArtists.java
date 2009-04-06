/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.artist.Audio;
import com.echonest.api.v3.artist.EchoNestException;
import com.echonest.api.v3.artist.Scored;
import java.util.List;

/**
 * An example of using the Echo Nest API to find similar artists to an artist
 */
public class FindSimilarArtists {

    /**
     * Usage: FindSimilarArtist 'artist name'
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                // gets API KEY FROM java system property ECHO_NEST_API_KEY
                ArtistAPI artistAPI = new ArtistAPI();

                // or explicitly pass in the key
                // ArtistAPI artistAPI = new ArtistAPI(MY_ECHO_NEST_API_KEY);

                String artistName = args[0];

                // search for the set of artists that match the requested artistName

                List<Artist> artists = artistAPI.searchArtist(artistName, false);
                if (artists.size() > 0) {
                    
                    // for each matching artist get the audio
                    for (Artist artist : artists) {
                        List<Scored<Artist>> similars = artistAPI.getSimilarArtists(artist, 0, ArtistAPI.MAX_ROWS);

                        System.out.println(" === Similar artists for " + artist.getName() + " ===");
                        for (Scored<Artist> simArtist : similars) {
                            System.out.println("   " + simArtist.getItem().getName());
                        }
                    }
                }
            } catch (EchoNestException e) {
                System.err.println("Trouble: " + e);
            }
        } else {
            System.out.println("Usage: java -cp examples.FindSimilarArtists 'artist name'");
        }
    }
}
