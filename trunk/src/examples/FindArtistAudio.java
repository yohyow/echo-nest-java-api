/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.artist.Audio;
import com.echonest.api.v3.artist.DocumentList;
import com.echonest.api.v3.artist.EchoNestException;
import java.util.List;

/**
 * An example of using the Echo Nest API to find Audio for a set of artists
 */
public class FindArtistAudio {

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
                        DocumentList<Audio> audioList = artistAPI.getAudio(artist, 0, ArtistAPI.MAX_ROWS);

                        System.out.println(" === Audio for " + artist.getName() + " ===");
                        for (Audio audio : audioList.getDocuments()) {
                            System.out.println(audio.toString());
                        }
                    }
                }
            } catch (EchoNestException e) {
                System.err.println("Trouble: " + e);
            }
        } else {
            System.out.println("Usage: java -cp examples.FindArtistAudio 'artist name'");
        }
    }
}
