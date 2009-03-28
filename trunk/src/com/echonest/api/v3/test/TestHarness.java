/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.test;

import com.echonest.api.v3.artist.Scored;
import com.echonest.api.v3.artist.Artist;
import com.echonest.api.v3.artist.Audio;
import com.echonest.api.v3.artist.DocumentList;
import com.echonest.api.v3.artist.ArtistAPI;
import com.echonest.api.v3.artist.EchoNestException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author plamere
 */
public class TestHarness {

    private ArtistAPI echoNest;
    private Map<String, List<Test>> testSets = new HashMap<String, List<Test>>();
    private boolean autoAdvance = true;
    private Artist curArtist = null;
    private HashSet<Artist> visited = new HashSet<Artist>();
    private List<Artist> artistQueue = new ArrayList<Artist>();

    TestHarness(ArtistAPI en) throws EchoNestException {
        echoNest = en;
        echoNest.setTrace(false);

        addBasicTests();
        addDetailedTests();

        // collect artists

        List<Scored<Artist>> hotArtists = echoNest.getTopHotttArtists(ArtistAPI.MAX_ROWS);
        for (Scored<Artist> sartist : hotArtists) {
            artistQueue.add(sartist.getItem());
        }
        advanceArtist();
    }

    private void addBasicTests() {
        String testSet = "basic";

        add(testSet, new Test() {

            public String getName() {
                return "get_audio";
            }

            public boolean go() throws Exception {
                echoNest.getAudio(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return true;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_blogs";
            }

            public boolean go() throws Exception {
                echoNest.getBlogs(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return true;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_familiarity";
            }

            public boolean go() throws Exception {
                return echoNest.getFamiliarity(getArtistID()) >= .0f;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_hotttnesss";
            }

            public boolean go() throws Exception {
                return echoNest.getHotness(getArtistID()) >= .0f;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "search_artist";
            }

            public boolean go() throws Exception {
                List<Artist> artists = echoNest.searchArtist(getArtistName(), false);
                for (Artist artist : artists) {
                    if (artist.equals(curArtist)) {
                        return true;
                    }
                }
                return false;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "search_artist (soundslike)";
            }

            public boolean go() throws Exception {
                List<Artist> artists = echoNest.searchArtist(getArtistName(), true);
                for (Artist artist : artists) {
                    if (artist.equals(curArtist)) {
                        return true;
                    }
                }
                return false;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_news";
            }

            public boolean go() throws Exception {
                echoNest.getNews(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return true;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_reviews";
            }

            public boolean go() throws Exception {
                echoNest.getReviews(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return true;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_profile";
            }

            public boolean go() throws Exception {
                Artist artist = echoNest.getProfile(getArtistID());
                return artist.getName().equals(getArtistName());
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_similar";
            }

            public boolean go() throws Exception {
                List<Scored<Artist>> artists = echoNest.getSimilarArtists(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return artists.size() == ArtistAPI.MAX_ROWS;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_urls";
            }

            public boolean go() throws Exception {
                Map<String, String> urlMap = echoNest.getUrls(getArtistID());
                return urlMap.get("itunes_url") != null && urlMap.get("amazon_url") != null;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_videos";
            }

            public boolean go() throws Exception {
                echoNest.getVideo(getArtistID(), 0, ArtistAPI.MAX_ROWS);
                return true;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_top_hottt_artists";
            }

            public boolean go() throws Exception {
                return echoNest.getTopHotttArtists(ArtistAPI.MAX_ROWS).size() == ArtistAPI.MAX_ROWS;
            }
        });
    }

    void addDetailedTests() {
        String testSet = "details";

        add(testSet, new Test() {

            public String getName() {
                return "get_audio (all)";
            }

            public boolean go() throws Exception {
                int start = 0;
                int count = ArtistAPI.MAX_ROWS;
                int total = 0;
                while (true) {
                    System.out.printf("Call %d/%d/%d\n", start, count, total);
                    DocumentList<Audio> results = echoNest.getAudio(getArtistID(), start, count);

                    assertTrue(results.getDocuments().size() == results.getCount(),
                            "Mismatched returned document size");
                    assertTrue(start == results.getStart(), "mismatched start");

                    // calculate counts for next
                    start = results.getStart() + results.getCount();

                    if (start < results.getTotal() && results.getCount() == 0) {
                        fail("Exhausted results before reaching count");
                    }

                    if (start >= results.getTotal()) {
                        break;
                    }

                    count = results.getTotal() - start;
                    if (count > ArtistAPI.MAX_ROWS) {
                        count = ArtistAPI.MAX_ROWS;
                    }

                    // check to make sure total doesn't change
                    if (total == 0) {
                        total = results.getTotal();
                    } else {
                        assertTrue(total == results.getTotal(), "total docs changed");
                    }
                }
                return true;
            }
        });
    }

    public void runTests(String testName, int count) {
        int oldRetries = echoNest.getRetries();
        long oldCacheTime = echoNest.getMaxCacheTime();
        echoNest.setRetries(0);
        int pass = 0;
        int fail = 0;
        List<Test> tests = testSets.get(testName);
        if (tests != null) {
            for (int i = 0; i < count; i++) {
                for (Test test : tests) {
                    try {
                        Thread.sleep(500);
                        System.out.print("    " + test.getName() + ": ");
                        if (test.go()) {
                            pass++;
                            System.out.println("OK");
                        } else {
                            fail++;
                            System.out.println("FAIL");
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }
                if (autoAdvance) {
                    advanceArtist();
                }
                if (i % 10 == 1) {
                    showStats();
                    System.out.printf("Test status:  Passed: %d,  Failed: %d\n", pass, fail);
                }
            }
        }
        echoNest.setRetries(oldRetries);
        echoNest.setMaxCacheTime(oldCacheTime);
    }

    public void showStats() {
        echoNest.showStats();
    }

    private void add(String testSetName, Test test) {
        List<Test> testSet = testSets.get(testSetName);

        if (testSet == null) {
            testSet = new ArrayList<Test>();
            testSets.put(testSetName, testSet);
        }
        testSet.add(test);
    }


    private String getArtistID() {
        return curArtist.getId();
    }

    private String getArtistName() {
        return curArtist.getName();
    }

    void advanceArtist() {
        do {
            curArtist = artistQueue.remove(0);
            log("Current artist is: " + curArtist.getName());
        } while (visited.contains(curArtist));

        visited.add(curArtist);

        try {
            List<Scored<Artist>> similarArtists = echoNest.getSimilarArtists(curArtist, 0, ArtistAPI.MAX_ROWS);
            for (Scored<Artist> sartist : similarArtists) {
                if (!visited.contains(sartist.getItem())) {
                    artistQueue.add(sartist.getItem());
                }
            }
        } catch (EchoNestException e) {
            log("trouble  building the queue to advance artists");
        }
    }

    void assertTrue(boolean condition, String message) throws TestException {
        if (!condition) {
            throw new TestException(message);
        }
    }

    void fail(String message) throws TestException {
        throw new TestException(message);
    }

    void log(String message) {
        System.out.println(message);
    }

}

interface Test {

    String getName();

    boolean go() throws Exception;
}

class TestException extends Exception {

    TestException(String message) {
        super(message);
    }
}