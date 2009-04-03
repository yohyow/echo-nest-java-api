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
import com.echonest.api.v3.artist.EchoNestException;
import com.echonest.api.v3.artist.News;
import com.echonest.api.v3.artist.Review;
import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Video;
import com.echonest.api.v3.track.TrackAPI;
import com.echonest.api.v3.track.TrackStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    private TestHarness testHarness;

    EchonestDevShell() throws EchoNestException {
        artistAPI = new ArtistAPI();
        trackAPI = new TrackAPI();
        shell = new Shell();
        shell.setPrompt("nest% ");
        addEchoNestCommands();
    }

    public void go() {
        shell.run();
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

        shell.add("runTests", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                String tests = "basic";
                int count = 100;

                if (args.length >= 2) {
                    count = Integer.parseInt(args[1]);
                }

                if (args.length >= 3) {
                    tests = args[2];
                }

                if (testHarness == null) {
                    testHarness = new TestHarness(artistAPI);
                }

                testHarness.runTests(tests, count);
                return "";
            }

            public String getHelp() {
                return "Usage: runTests  [count] [testset]";
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
                    List<Scored<Artist>> artists = artistAPI.getSimilarArtists(artist, 0, ArtistAPI.MAX_ROWS);
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

        /**
        shell.add("uploadTrack", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length < 2) {
                    return "Usage: uploadTrack url | file";
                } else {
                    String path = args[1];
                    if (path.startsWith("http:")) {
                        String id = artistAPI.uploadTrack(new URL(path), false);
                        System.out.println("ID is: " + id);
                    } else {
                        String id = artistAPI.uploadTrack(new File(path), false);
                        System.out.println("ID is: " + id);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "uploads a track";
            }
        });
         **/



        shell.add("get_blogs", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Blogs for " + artist.getName());
                    DocumentList<Blog> blogs = artistAPI.getBlogs(artist, 0, ArtistAPI.MAX_ROWS);
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
                    DocumentList<Audio> audioList = artistAPI.getAudio(artist, 0, ArtistAPI.MAX_ROWS);
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
                    DocumentList<Video> videoList = artistAPI.getVideo(artist, 0, ArtistAPI.MAX_ROWS);
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
                    DocumentList<News> newsList = artistAPI.getNews(artist, 0, ArtistAPI.MAX_ROWS);
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
                    DocumentList<Review> reviews = artistAPI.getReviews(artist, 0, ArtistAPI.MAX_ROWS);
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
                    System.out.println("Familiarty for " + artist.getName() + " " + artistAPI.getFamiliarity(artist));
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets familiarity for an artist";
            }
        });

        shell.add("report", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    artistReport(artist.getId(), true);
                    System.out.println("Familiarty for " + artist.getName() + " " + artistAPI.getFamiliarity(artist));
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "generates an artist report";
            }
        });

        shell.add("fullreport", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length != 2) {
                    System.err.println("Usage: fullreport index-file-path");
                } else {
                    fullReport(args[1]);
                }
                return "";
            }

            public String getHelp() {
                return "generates an artist report from an index file";
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

        shell.add("get_goodness", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(ci.mash(args, 1));
                if (artist != null) {
                    System.out.println("Goodness for " + artist.getName() + " " + artistAPI.getGoodness(artist));
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets goodness for an artist";
            }
        });

        shell.add("top_hot", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                List<Scored<Artist>> hotArtists = artistAPI.getTopHotttArtists(ArtistAPI.MAX_ROWS);
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
                } else {
                    System.out.println("Usage: trace true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables trace";
            }
        });

        shell.add("saveCache", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    artistAPI.saveCache(args[1]);
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
                    artistAPI.loadCache(args[1]);
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
                    TrackStatus ts = null;
                    String id;
                    if (arg.startsWith("http:")) {
                        id = trackAPI.uploadTrack(new URL(arg));
                    } else {
                        id = trackAPI.uploadTrack(new File(arg));
                    }
                    System.out.println("ID: " + id);
                } else {
                    System.out.println("Usage: trackUpload file|url");
                }
                return "";
            }

            public String getHelp() {
                return "uploads a track";
            }
        });

        shell.add("trackUploadDir", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length >= 2) {
                    String arg = ci.mash(args, 1);
                    File dir = new File(arg);
                    if (dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        for (File f : files) {
                            if (f.getAbsolutePath().toLowerCase().endsWith("mp3")) {
                                System.out.println("Uploading " + f);
                                String id = trackAPI.uploadTrack(f);
                                System.out.println("   done. ID is " + id);
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
                return "uploads a directory of tracks";
            }
        });

        shell.add("trackDuration", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    float duration = trackAPI.getDuration(arg);
                    System.out.println("Duration: " + duration);
                } else {
                    System.out.println("Usage: trackDuration id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the duration of a track";
            }
        });

        shell.add("trackEndOfFadeIn", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getEndOfFadeIn(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the end of fade in of a track";
            }
        });

        shell.add("trackStartOfFadeOut", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getStartOfFadeOut(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the start of fade out of a track";
            }
        });

        shell.add("trackTimeSignature", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getTimeSignature(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall time signature of a track";
            }
        });

        shell.add("trackMode", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getMode(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall mode of a track";
            }
        });

        shell.add("trackTempo", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getTempo(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall Tempo of a track";
            }
        });

        shell.add("trackLoudness", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getOverallLoudness(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall loudness of a track";
            }
        });

        shell.add("trackMetadata", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getMetadata(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the ID3 metadata of a track";
            }
        });

        shell.add("trackKey", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    System.out.println(trackAPI.getKey(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the overall key of a track";
            }
        });

        shell.add("trackBeats", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    show(trackAPI.getBeats(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the beats of a track";
            }
        });

        shell.add("trackBars", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    show(trackAPI.getBars(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the bars of a track";
            }
        });

        shell.add("trackTatums", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    show(trackAPI.getTatums(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the tatums of a track";
            }
        });

        shell.add("trackSections", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    show(trackAPI.getSections(arg));
                } else {
                    System.out.println("Usage: " + args[0] + " id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "gets the tatums of a track";
            }
        });


        shell.add("trackWait", new ShellCommand() {

            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    String arg = args[1];
                    trackAPI.waitForAnalysis(arg, 60000);
                } else {
                    System.out.println("Usage: trackWait id|md5");
                }
                return "";
            }

            public String getHelp() {
                return "waits for an analysis to be complete";
            }
        });
    }

    private Artist getArtist(String name) throws EchoNestException {
        Artist artist = artistCache.get(name);
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

    public static void main(String[] args) {
        try {
            EchonestDevShell shell = new EchonestDevShell();
            shell.go();
        } catch (EchoNestException e) {
            System.err.println("Can't connect to the echonest");
        }
    }
    private final static int MAX_NEWS = 15;
    private final static int MAX_REVIEWS = 15;
    private final static int MAX_BLOGS = 15;

    private void fullReport(String path) throws IOException, EchoNestException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = null;

        System.out.println("<html>");
        System.out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/> ");
        System.out.println("<body>");

        while ((line = in.readLine()) != null) {
            String[] fields = line.split("::");
            if (fields.length == 2) {
                System.err.println("Processing " + fields[0]);
                String id = "music://id.echonest.com/~/AR/" + fields[1].trim();
                artistReport(id, false);
            } else {
                System.err.println("Bad line " + line);
            }
        }
        System.out.println("</body>");
        System.out.println("</html>");
    }

    private void artistReport(String id, boolean header) throws EchoNestException {
        Artist artist = artistAPI.getProfile(id);
        if (artist != null) {
            if (header) {
                System.out.println("<html>");
                System.out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/> ");
                System.out.println("<body>");
            }
            System.out.println("<div class=\"artist\">");
            System.out.println("<h1>" + artist.getName() + "</h1>");
            artistBlogs(artist);
            artistReviews(artist);
            artistNews(artist);
            System.out.println("</div>");
            if (header) {
                System.out.println("</body>");
                System.out.println("</html>");
            }
        } else {
            System.err.println("Can't find artist for " + id);
        }
    }

    private void artistNews(Artist artist) throws EchoNestException {
        System.out.println("<h2> News for " + artist.getName() + "</h2>");
        DocumentList<News> newsList = artistAPI.getNews(artist.getId(), 0, MAX_NEWS);
        for (News news : newsList.getDocuments()) {
            System.out.println("<h3>" + mklink(news.getURL(), news.getName()) + "</h3>");
            System.out.println("From: " + news.getURL() +"<p>");
            System.out.println("<div class=\"summary\">" + detag(news.getSummary()) + "</div>");
        }
    }

    private void artistBlogs(Artist artist) throws EchoNestException {
        System.out.println("<h2> Blogs for " + artist.getName() + "</h2>");
        DocumentList<Blog> blogs = artistAPI.getBlogs(artist.getId(), 0, MAX_NEWS);
        for (Blog blog : blogs.getDocuments()) {
            System.out.println("<h3>" + mklink(blog.getURL(), blog.getName()) + "</h3>");
            System.out.println("From: " + blog.getURL() +"<p>");
            System.out.println("<div class=\"summary\">" + detag(blog.getSummary()) + "</div>");
        }
    }

    private void artistReviews(Artist artist) throws EchoNestException {
        System.out.println("<h2> Reviews for " + artist.getName() + "</h2>");
        DocumentList<Review> reviews = artistAPI.getReviews(artist.getId(), 0, MAX_NEWS);
        for (Review review : reviews.getDocuments()) {
            System.out.println("<h3>" + mklink(review.getURL(), review.getName()) + "</h3>");
            System.out.println("From: " + review.getURL() +"<p>");
            System.out.println("<div class=\"summary\">" + detag(review.getSummary()) + "</div>");
        }
    }

    private String mklink(String url, String text) {
        if (url != null && text != null) {
            return "<a href=\"" + url + "\">" + text + "</a>";
        } else if (text != null) {
            return text;
        } else {
            return "";
        }
    }

    public static String detag(String s) {
        if (s != null) {
            return s.replaceAll("\\<.*?\\>", "");
        } else {
            return "";
        }
    }

    private void show(List<?> list) {
        for (Object o : list) {
            System.out.println("  " + o);
        }

        System.out.println("\nTotal elements: " + list.size());
    }

}
