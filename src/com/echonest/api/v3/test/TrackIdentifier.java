/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v1.ID3V1_1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;

/**
 *
 * @author plamere
 */
public class TrackIdentifier {

    public TrackInfo identifyTrack(File f) {
        try {
            return loadMP3File(f);
        } catch (IOException ex) {
            TrackInfo track = new TrackInfo();
            track.setFile(f);
            track.setIsValid(false);
            return track;
        }
    }

    public TrackInfo loadMP3File(File file) throws IOException {
        TrackInfo track = new TrackInfo();

        track.setFile(file);
        if (!file.canRead()) {
            track.setIsValid(false);
            System.err.println("ERR: Can't read " + file);
        }

        String artist = "";
        String albumName = "";
        String genreName = "";
        String title = "";
        String year = "";
        String trackNumber = "";
        String tag = "";
        int trackNum = 0;

        try {
            MP3File mp3 = new MP3File(file);
            ID3V1Tag v1tag = mp3.getID3V1Tag();
            ID3V2Tag v2tag = mp3.getID3V2Tag();

            if (v1tag != null) {
                tag += "ID3";
                artist = v1tag.getArtist();
                albumName = v1tag.getAlbum();
                genreName = v1tag.getGenre().toString();
                title = v1tag.getTitle();
                year = getYear(v1tag);
                trackNumber = getTrack(v1tag);
            }

            if (v2tag != null) {
                if (tag.length() > 0) {
                    tag += "+";
                }
                tag += "ID3V2";

                artist = takeBest(artist, v2tag.getArtist());
                albumName = takeBest(albumName, v2tag.getAlbum());
                genreName = takeBest(genreName, v2tag.getGenre());
                title = takeBest(title, v2tag.getTitle());
                trackNumber = takeBest(trackNumber, getTrack(v2tag));
                year = takeBest(year, getYear(v2tag));
            }

            genreName = filterGenre(genreName);

            try {
                trackNum = Integer.parseInt(trackNumber);
            } catch (NumberFormatException e) {
                trackNum = 0;
            }


            if (tag.length() == 0) {
                tag = "NO-TAG";
            }
        } catch (ID3Exception e) {
            track.setIsValid(false);
            System.err.println("Err: " + e + " " + file);
        }

        artist = artist.trim();

        if (artist.length() == 0) {
            artist = "(Unknown Artist)";
        }

        albumName = albumName.trim();
        if (albumName.length() == 0) {
            albumName = artist + " (Unknown Album)";
        }
        title = title.trim();

        // if there's no title then use
        if (title.length() == 0) {
            title = extractTitleFromFile(file);
        }

        track.setIsValid(true);
        track.setTitle(title);
        track.setAlbum(albumName);
        track.setArtist(artist);
        track.setYear(year);
        track.setId3Genre(genreName);
        track.setTrackNumber(trackNum);
        return track;
    }

    /**
     * Returns the best version. V2 is better than V1
     * unless V2 is null or empty. Note that many mp3s return
     * the genre "null" so we explicitly ignore that as well.
     */
    private String takeBest(String v1, String v2) {
        if (v2 != null && v2.length() > 0 && !v2.equals("null")) {
            return v2;
        } else {
            return v1;
        }
    }
    /**
     * Extract a title from a file name
     */
    private static Pattern underscorePattern = Pattern.compile("_");
    static Pattern mp3Pattern = Pattern.compile(".[mM][pP]3");

    private String extractTitleFromFile(File file) {
        String name = file.getName();
        name = underscorePattern.matcher(name).replaceAll(" ");
        name = mp3Pattern.matcher(name).replaceAll(" ");
        return name;
    }
    /**
     * Some MP3 files are tagged with genres like so (33). This method
     * will convert a genre to an english description.
     */
    private static Pattern numberInParens = Pattern.compile("\\(([0-9]+)\\)");

    private String filterGenre(String g) {
        String genre = g;
        Matcher matcher = numberInParens.matcher(g);
        if (matcher.find()) {
            String num = matcher.group(1);
            int val = Integer.parseInt(num);
            genre = mapGenre(val);
        }
        if (genre == null || genre.length() == 0) {
            genre = "Unknown";
        }
        return genre;
    }

    private String getYear(ID3Tag tag) {
        String year = "";
        try {
            if (tag instanceof ID3V2Tag) {
                ID3V2Tag v2tag = (ID3V2Tag) tag;
                year = "" + v2tag.getYear();
            } else {
                ID3V1Tag v1tag = (ID3V1Tag) tag;
                year = v1tag.getYear();
            }
        } catch (ID3Exception e) {
            year = "";
        }
        return year;
    }

    private String getTrack(ID3Tag tag) {
        String track = "";
        try {
            if (tag instanceof ID3V2Tag) {
                ID3V2Tag v2tag = (ID3V2Tag) tag;
                track = "" + v2tag.getTrackNumber();
            } else if (tag instanceof ID3V1_1Tag) {
                ID3V1_1Tag v11tag = (ID3V1_1Tag) tag;
                track = "" + v11tag.getAlbumTrack();
            } else {
                track = "";
            }
        } catch (ID3Exception e) {
            track = "";
        }
        return track;
    }

    private static String[] genreString = {
        "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk",
        "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other",
        "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial",
        "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack",
        "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion",
        "Trance", "Classical", "Instrumental", "Acid", "House", "Game",
        "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul",
        "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock",
        "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic",
        "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy",
        "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk",
        "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic",
        "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal",
        "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical",
        "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk",
        "Swing", "Fast Fusion", "Bebob", "Latin", "Revival",
        "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock",
        "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band",
        "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech",
        "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony",
        "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam",
        "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad",
        "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo",
        "A capella", "Euro-House", "Dance Hall"
    };

    private String mapGenre(int genre) {
        if (genre >= 0 && genre < genreString.length) {
            return genreString[genre];
        } else {
            return null;
        }
    }
}
