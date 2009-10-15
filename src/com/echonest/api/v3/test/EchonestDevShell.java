/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import com.echonest.api.util.Shell;
import com.echonest.api.util.ShellCommand;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.Audio;
import com.echonest.api.v3.artist.Blog;
import com.echonest.api.v3.artist.DocumentList;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.EchoNestException;
import com.echonest.api.v3.artist.News;
import com.echonest.api.v3.artist.Review;
import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Video;
import com.echonest.api.v3.track.TrackAPI;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author plamere
 */
public class EchonestDevShell {
    private Shell shell;
    private ArtistAPI artistAPI;
    private TrackAPI trackAPI;
    private Map<String, Artist> artistCache = new HashMap<String, Artist>();
    private String currentTrackID = null;
    private int displayCount = ArtistAPI.MAX_ROWS;

    public EchonestDevShell() throws EchoNestException {
        artistAPI = new ArtistAPI();
        trackAPI = new TrackAPI(System.getProperty("ECHO_NEST_API_KEY"), null, 3);
        shell = new Shell();
        shell.setPrompt("nest% ");
        addEchoNestCommands();
    }

    public EchonestDevShell(ArtistAPI artistAPI, TrackAPI trackAPI) throws EchoNestException {
        this.artistAPI = artistAPI;
        this.trackAPI = trackAPI;
        shell = new Shell();
        shell.setPrompt("nest% ");
        addEchoNestCommands();
    }

    public void go() {
        System.out.println("Welcome to The Echo Nest API Shell");
        System.out.println("   type 'help' ");
        shell.run();
    }

    public Shell getShell() {
        return shell;
    }

    private void addEchoNestCommands() {
        shell.add("enid", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println(artist.getName() + " " + artist.getId());
                }
                return "";
            }

            public String getHelp() {
                return "gets the ENID for an arist";
            }
        });

        shell.add("search_artist", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = artistAPI.searchArtist(ci.mash(args, 1), false);
                for (Artist artist : artists) {
                    System.out.println(artist.getId() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists exact match  ";
            }
        });

        shell.add("display_count", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    displayCount = Integer.parseInt(args[1]);
                } else {
                    System.out.println("Display count: "  + displayCount);
                }
                return "";
            }

            public String getHelp() {
                return "sets/gets the number of items to display";
            }
        });

        shell.add("qbd", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = artistAPI.searchArtistByDescription(ci.mash(args, 1), 15);
                for (Artist artist : artists) {
                    System.out.println(artist.getId() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "query by description";
            }
        });


        shell.add("search_artist_sl", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = artistAPI.searchArtist(ci.mash(args, 1), true);
                for (Artist artist : artists) {
                    System.out.println(artist.getId() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists with sounds like.  ";
            }
        });

        shell.add("get_similar", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Similarity for " + artist.getName());
                    List<Scored<Artist>> artists = artistAPI.getSimilarArtists(artist, 0, displayCount);
                    for (Scored<Artist> sartist : artists) {
                        System.out.printf("  %.2f %s\n", sartist.getScore(), sartist.getItem().getName());
                    }

                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "finds similar artists";
            }
        });

        shell.add("get_similars", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> query = new ArrayList<Artist>();
                for (int i = 1; i < args.length; i++) {
                    Artist artist = getArtist(args[i]);
                    if (artist != null) {
                        query.add(artist);
                    }
                }
                List<Scored<Artist>> artists = artistAPI.getSimilarArtists(query, 0, displayCount);
                for (Scored<Artist> sartist : artists) {
                    System.out.printf("  %.2f %s\n", sartist.getScore(), sartist.getItem().getName());
                }

                return "";
            }

            public String getHelp() {
                return "finds similar artists to a set of artists";
            }
        });


        shell.add("get_blogs", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Blogs for " + artist.getName());
                    DocumentList<Blog> blogs = artistAPI.getBlogs(artist, 0, displayCount);
                    System.out.printf("Total Blogs %d\n", blogs.getTotal());
                    for (Blog blog : blogs.getDocuments()) {
                        blog.dump();
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets blogs for an artist";
            }
        });

        shell.add("get_audio", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Audio for " + artist.getName());
                    DocumentList<Audio> audioList = artistAPI.getAudio(artist, 0, displayCount);
                    System.out.printf("Total audio %d\n", audioList.getTotal());
                    for (Audio audio : audioList.getDocuments()) {
                        audio.dump();
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets audio for an artist";
            }
        });

        shell.add("get_video", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Video for " + artist.getName());
                    DocumentList<Video> videoList = artistAPI.getVideo(artist, 0, displayCount);
                    System.out.printf("Total audio %d\n", videoList.getTotal());
                    for (Video video : videoList.getDocuments()) {
                        video.dump();
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets video for an artist";
            }
        });

        shell.add("get_news", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("News for " + artist.getName());
                    DocumentList<News> newsList = artistAPI.getNews(artist, 0, displayCount);
                    System.out.printf("Total news %d\n", newsList.getTotal());
                    for (News news : newsList.getDocuments()) {
                        news.dump();
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets news for an artist";
            }
        });

        shell.add("get_reviews", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Reviews for " + artist.getName());
                    DocumentList<Review> reviews = artistAPI.getReviews(artist, 0, displayCount);
                    System.out.printf("Total Reviews %d\n", reviews.getTotal());
                    for (Review review : reviews.getDocuments()) {
                        review.dump();
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets Reviews for an artist";
            }
        });

        shell.add("get_urls", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("URLS for " + artist.getName());
                    Map<String, String> urlMap = artistAPI.getUrls(artist);
                    List<String> keys = new ArrayList<String>(urlMap.keySet());
                    Collections.sort(keys);
                    for (String key : keys) {
                        System.out.printf("%20s : %s\n", key, urlMap.get(key));
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets Reviews for an artist";
            }
        });

        shell.add("get_fam", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Familiarity for " + artist.getName() + " " + artistAPI.getFamiliarity(artist));
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets familiarity for an artist";
            }
        });


        shell.add("get_hot", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Hotttnesss for " + artist.getName() + " " + artistAPI.getHotness(artist));
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets hotttnesss for an artist";
            }
        });


        shell.add("top_hot", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Scored<Artist>> hotArtists = artistAPI.getTopHotttArtists(displayCount);
                for (Scored<Artist> sartist : hotArtists) {
                    System.out.printf("%.2f %s\n", sartist.getScore(), sartist.getItem().getName());
                }
                return "";
            }

            public String getHelp() {
                return "gets top artists ";
            }
        });

        shell.add("stats", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                artistAPI.showStats();
                trackAPI.showStats();
                return "";
            }

            public String getHelp() {
                return "shows stats";
            }
        });

        shell.add("trace", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    artistAPI.setTrace(args[1].equals("true"));
                    trackAPI.setTrace(args[1].equals("true"));
                } else {
                    System.out.println("Usage: trace true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables trace";
            }
        });

        shell.add("ignoreParseErrors", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    artistAPI.setIgnoreParsingErrors(args[1].equals("true"));
                    trackAPI.setIgnoreParsingErrors(args[1].equals("true"));
                } else {
                    System.out.println("Usage: ignoreParseErrors true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables ignoring parse errors";
            }
        });

        shell.add("saveCache", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    artistAPI.saveCache(args[1] + ".artist.cache");
                    trackAPI.saveCache(args[1] + ".track.cache");
                } else {
                    System.out.println("Usage: saveCache filename");
                }
                return "";
            }

            public String getHelp() {
                return "saves the cache";
            }
        });

        shell.add("loadCache", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    artistAPI.loadCache(args[1] + ".artist.cache");
                    artistAPI.loadCache(args[1] + ".track.cache");
                } else {
                    System.out.println("Usage: loadCache filename");
                }
                return "";
            }

            public String getHelp() {
                return "loads the cache";
            }
        });

        shell.add("setMaxCacheTime", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    long cacheTime = Integer.parseInt(args[1]) * 1000L;
                    artistAPI.setMaxCacheTime(cacheTime);
                    trackAPI.setMaxCacheTime(cacheTime);
                } else {
                    System.out.println("Usage: setMaxCacheTime secs");
                }
                return "";
            }

            public String getHelp() {
                return "sets the cache time";
            }
        });

        shell.add("getMaxCacheTime", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 1) {
                    System.out.println("cache time: " + artistAPI.getMaxCacheTime() / 1000L);
                } else {
                    System.out.println("Usage: getMaxCacheTime");
                }
                return "";
            }

            public String getHelp() {
                return "gets the cache time";
            }
        });

        shell.add("trackUpload", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = ci.mash(args, 1);
                    String id;
                    if (arg.startsWith("http:")) {
                        id = trackAPI.uploadTrack(new URL(arg), false);
                    } else {
                        id = trackAPI.uploadTrack(new File(arg), false);
                    }
                    System.out.println("ID: " + id);
                    currentTrackID = id;
                } else {
                    System.out.println("Usage: trackUpload file|url");
                }
                return "";
            }

            public String getHelp() {
                return "uploads a track";
            }
        });

        shell.add("trackMD5", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = ci.mash(args, 1);
                    System.out.println(trackAPI.getMD5(new File(arg)));
                } else {
                    System.out.println("Usage: trackMD5 file");
                }
                return "";
            }

            public String getHelp() {
                return "gets the MD5 for a track";
            }
        });

        shell.add("trackStatus", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = ci.mash(args, 1);
                    String md5 = trackAPI.getMD5(new File(arg));
                    TrackAPI.AnalysisStatus status = trackAPI.getAnalysisStatus(md5);
                    System.out.println("Status: " + status);

                } else {
                    System.out.println("Usage: trackStatus file");
                }
                return "";
            }

            public String getHelp() {
                return "gets the analysis status for a track";
            }
        });

        shell.add("trackAnalysisVersion", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    int version = Integer.parseInt(args[1]);
                    trackAPI = new TrackAPI(System.getProperty("ECHO_NEST_API_KEY"), null, version);
                } else {
                    System.out.println("trackAnalysisVersion " + trackAPI.getAnalysisVersion());
                }
                return "";
            }

            public String getHelp() {
                return "gets/sets the analysis version to use";
            }
        });

        shell.add("trackUploadDir", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = ci.mash(args, 1);
                    File dir = new File(arg);
                    processDir(dir);
                } else {
                    System.out.println("Usage: trackUploadDir dir");
                }
                return "";
            }

            public String getHelp() {
                return "uploads a directory of tracks";
            }
        });




        shell.add("trackShowDir", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = Shell.mash(args, 1);
                    File dir = new File(arg);
                    if (dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        for (File f : files) {
                            if (f.getAbsolutePath().toLowerCase().endsWith("mp3")) {
                                String md5 = trackAPI.getMD5(f);
                                if (trackAPI.isKnownTrack(md5)) {
                                    showAll(md5);
                                }
                            }
                        }
                    } else {
                        System.out.println("Usage: " + args[0] + " dir");
                    }
                } else {
                    System.out.println("Usage: trackUploadDir dir");
                }
                return "";
            }

            public String getHelp() {
                return "Shows the info for a directory of previously analyzed tracks";
            }
        });

        shell.add("trackDuration", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    float duration = trackAPI.getDuration(id);
                    System.out.println("Duration: " + duration);
                }
                return "";
            }

            public String getHelp() {
                return "gets the duration of a track";
            }
        });

        shell.add("trackEndOfFadeIn", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getEndOfFadeIn(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the end of fade in of a track";
            }
        });

        shell.add("trackStartOfFadeOut", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getStartOfFadeOut(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the start of fade out of a track";
            }
        });

        shell.add("trackTimeSignature", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getTimeSignature(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall time signature of a track";
            }
        });

        shell.add("trackMode", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getMode(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall mode of a track";
            }
        });

        shell.add("trackTempo", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getTempo(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall Tempo of a track";
            }
        });

        shell.add("trackLoudness", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getOverallLoudness(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall loudness of a track";
            }
        });

        shell.add("trackMetadata", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getMetadata(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the ID3 metadata of a track";
            }
        });

        shell.add("trackKey", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    System.out.println(trackAPI.getKey(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall key of a track";
            }
        });

        shell.add("trackBeats", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    show("beats", trackAPI.getBeats(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the beats of a track";
            }
        });

        shell.add("trackBars", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    show("bars", trackAPI.getBars(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the bars of a track";
            }
        });

        shell.add("trackTatums", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    show("tatums", trackAPI.getTatums(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the tatums of a track";
            }
        });

        shell.add("trackSections", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    show("sections", trackAPI.getSections(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the tatums of a track";
            }
        });

        shell.add("trackSegments", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    show("segments", trackAPI.getSegments(id));
                }
                return "";
            }

            public String getHelp() {
                return "gets the segments of a track";
            }
        });

        shell.add("trackShowAll", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    showAll(id);
                }
                return "";
            }

            public String getHelp() {
                return "shows everything about a track";
            }
        });

        shell.add("trackWait", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String id = getTrackIDFromArgs(args);
                if (id != null) {
                    trackAPI.waitForAnalysis(id, 60000);
                }
                return "";
            }

            public String getHelp() {
                return "waits for an analysis to be complete";
            }
        });
    }

    private void showAll(String idOrMd5) throws EchoNestException {
        System.out.println("Duration: " + trackAPI.getDuration(idOrMd5));
        System.out.println("FadeIn End: " + trackAPI.getEndOfFadeIn(idOrMd5));
        System.out.println("FadeOut Start: " + trackAPI.getStartOfFadeOut(idOrMd5));
        System.out.println("Key: " + trackAPI.getKey(idOrMd5));
        System.out.println("Loudness: " + trackAPI.getOverallLoudness(idOrMd5));
        System.out.println("Metadata: " + trackAPI.getMetadata(idOrMd5));
        System.out.println("Mode: " + trackAPI.getMode(idOrMd5));
        System.out.println("Tempo: " + trackAPI.getTempo(idOrMd5));
        System.out.println("Time Signature: " + trackAPI.getTimeSignature(idOrMd5));
        show("sections", trackAPI.getSections(idOrMd5));
        show("bars", trackAPI.getBars(idOrMd5));
        show("beats", trackAPI.getBeats(idOrMd5));
        show("tatums", trackAPI.getTatums(idOrMd5));
        show("segments", trackAPI.getSegments(idOrMd5));
    }


    private String getTrackIDFromArgs(String[] args) {
        if (args.length  == 1 && currentTrackID != null) {
            return currentTrackID;
        } else if (args.length == 2) {
            return args[1];
        } else {
            System.out.println("Usage: " + args[0] + " id or md5");
            return null;
        }
    }

    private Artist getArtist(String name) throws EchoNestException {
        Artist artist = null;

        if (artist == null) {
            if (name.startsWith("music://")) {
                artist = artistAPI.getProfile(name);
            } else {
                List<Artist> artists = artistAPI.searchArtist(name, false);
                if (artists.size() > 0) {
                    artist = artists.get(0);
                    artistCache.put(name, artist);
                }

            }
            if (artist != null) {
                artistCache.put(artist.getId(), artist);
                artistCache.put(artist.getName(), artist);
            }
        }
        return artist;
    }


    private void processDir(File dir) throws IOException, EchoNestException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.getAbsolutePath().toLowerCase().endsWith(".mp3")) {
                    System.out.println("Uploading " + f);
                    String id = trackAPI.uploadTrack(f, true);
                    System.out.println("   done. ID is " + id);
                } else if (f.isDirectory()) {
                    processDir(f);
                }
            }
        }
    }



    private void show(String title, List<?> list) {
        System.out.println(title);
        for (Object o : list) {
            System.out.println("  " + o);
        }

        System.out.println("Total elements: " + list.size());
        System.out.println();
    }

    public static void main(String[] args) {
        try {
            EchonestDevShell shell = new EchonestDevShell();
            shell.go();
        } catch (EchoNestException e) {
            System.err.println("Can't connect to the echonest");
        }
    }
}
