/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package examples;

import com.echonest.api.v3.EchoNestException;
import com.echonest.api.v3.track.FloatWithConfidence;
import com.echonest.api.v3.track.Metadata;
import com.echonest.api.v3.track.TrackAPI;
import com.echonest.api.v3.track.TrackAPI.AnalysisStatus;
import java.io.File;

/**
 *
 * @author plamere
 */
/**
 * An example of using the Echo Nest API to find similar artists to an artist
 */
public class GetTrackBPM {

    /**
     * Usage: FindSimilarArtist 'artist name'
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                // Create a trackAPI that gets the API KEY from the
                //  java system property ECHO_NEST_API_KEY
                TrackAPI trackAPI = new TrackAPI();

                // or explicitly pass in the key
                // TrackAPI trackAPI = new TrackAPI(MY_ECHO_NEST_API_KEY);

                String trackPath = args[0];

                // first upload the track
                System.out.println("Uploading and analyzing ...");
                String id = trackAPI.uploadTrack(new File(trackPath), false);
                AnalysisStatus status = trackAPI.waitForAnalysis(id, 60000);
                if (status == AnalysisStatus.COMPLETE) {
                    Metadata metadata = trackAPI.getMetadata(id);
                    FloatWithConfidence bpm = trackAPI.getTempo(id);
                    System.out.println("Metadata:");
                    System.out.println(metadata);
                    System.out.println("BPM is " + bpm);
                } else {
                    System.out.println("Status is " + status);
                }
            } catch (EchoNestException e) {
                System.err.println("Trouble: " + e);
            }
        } else {
            System.out.println("Usage: java -cp examples.GetTrackBPM 'path-to-mp3'");
        }
    }
}