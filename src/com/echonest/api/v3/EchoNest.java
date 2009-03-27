/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v3;

import com.echonest.api.util.Commander;
import com.echonest.api.util.ExpiringCache;
import com.echonest.api.util.StatsManager;
import com.echonest.api.util.XmlUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A client side API for the Echo Nest developer API.  The Echo Nest developer
 * API requires an API key. You can obtain a key at: http://developer.echonest.com/
 *
 * This client supports cacheing of the return results. The cache is enabled by
 * default.
 *
 * @author plamere
 */
public class EchoNest {

    /** maximum rows returned by many calls*/
    public final static int MAX_ROWS = 15;

    private Commander commander;
    private StatsManager sm = new StatsManager();
    private ExpiringCache<Document> cache;

    /**
     * Creates an instance of the EchoNest class using an API key specified in the 
     * the property ECHO_NEST_API_KEY
     * 
     * @throws com.echonest.api.v3.EchoNestException
     */
    public EchoNest() throws EchoNestException {
        this(System.getProperty("ECHO_NEST_API_KEY"));
    }

    /**
     * Creates an instance of the EchoNest class
     * @param key the API key (available at http://developer.echonest.com/ )
     * @throws com.echonest.api.v3.EchoNestException
     */
    public EchoNest(String key) throws EchoNestException {
        if (key == null) {
            System.err.println("No API Key is defined. Get a key from http://developer.echonest.com");
            throw new EchoNestException(EchoNestException.ERR_NO_KEY, "No API key defined");
        }

        try {
            //commander = new Commander("EchoNest", "http://developer.echonest.com/api/", "&version=3&api_key=" + key);
            String prefix = System.getProperty("ECHO_NEST_API_PREFIX");
            if (prefix == null) {
                prefix = "http://developer.echonest.com/api/";
            }

            commander = new Commander("EchoNest", prefix, "&version=3&api_key=" + key);
            commander.setRetries(5);
            commander.setTraceSends(false);
            commander.setTrace(false);
            commander.setMinimumCommandPeriod(500L);
            commander.setTimeout(30 * 1000);
            cache = new ExpiringCache<Document>();
            setMaxCacheTime(7 * 24 * 60 * 60 * 1000L);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    //commander.setMinimumCommandPeriod(0);
    }

    /**
     * Sets the maximum time that values will be cached by this library. To disable
     * the cache, set milli to zero.
     * @param milli cache life in milliseconds
     */
    public void setMaxCacheTime(long milli) {
        cache.setMaxAge(milli);
    }

    /**
     * Gets the maximum time that values will be cached by this library
     * @return cache life in milliseconds
     */
    public long getMaxCacheTime() {
        return cache.getMaxAge();
    }

    /**
     * Saves the cache to the give path
     * @param path the path to the cache
     * @throws java.io.IOException
     */
    public void saveCache(String path) throws IOException {
        cache.save(path);
    }

    /**
     * Loads the cache from the given path
     * @param path the path
     * @throws java.io.IOException
     */
    public void loadCache(String path) throws IOException {
        cache.load(path);
    }

    /**
     * Shows statistics about the API usage
     */
    public void showStats() {
        sm.dump();
    }

    /**
     * Turns API call tracing on or off.  When on, API calls are printed
     * on standard out
     * @param on if true, tracing is turned on
     */
    public void setTrace(boolean on) {
        commander.setTraceSends(on);
    }

    /**
     * Gets the number of API retries
     * @return the number of retries
     */
    public int getRetries() {
        return commander.getRetries();
    }

    /**
     * Sets the number of retries
     * @param retries the number of retries
     */
    public void setRetries(int retries) {
        commander.setRetries(retries);
    }

    /**
     * Given an Echo Nest Identifier get  the associated artist
     * @param id an Echo Nest Identifier
     * @return an artist 
     * @throws com.echonest.api.v3.EchoNestException
     */
    public Artist getProfile(String id) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_profile");

        try {
            String cmdURL = "get_profile?id=" + id;
            Document doc = sendCommand(cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artist = (Element) XmlUtil.getDescendent(docElement, "artist");
            String newid = XmlUtil.getDescendentText(artist, "id");
            String name = XmlUtil.getDescendentText(artist, "name");

            if (!id.equals(newid)) {
                throw new EchoNestException(EchoNestException.ERR_BAD_ID, "mismatch ID");
            }
            sm.end(tracker);
            return new Artist(name, newid);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Searches for artists by name
     * @param artistName the artist name
     * @param soundsLike if true, a 'sounds like' comparison is used in the search
     *  otherwise, only exact matches are returned.
     * @return a list of matching artists, ordered by how well they match the query.
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Artist> searchArtist(String artistName, boolean soundsLike) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("search_artist");
        List<Artist> artists = new ArrayList<Artist>();

        try {
            String cmdURL = "search_artists?query=" + encode(artistName);
            cmdURL += "&sounds_like=" + (soundsLike ? "Y" : "N");
            Document doc = sendCommand(cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artistList = (Element) XmlUtil.getDescendent(docElement, "artists");
            if (artistList != null) {
                NodeList itemList = artistList.getElementsByTagName("artist");
                for (int i = 0; i < itemList.getLength(); i++) {
                    Element item = (Element) itemList.item(i);
                    String name = XmlUtil.getDescendentText(item, "name");
                    String enid = XmlUtil.getDescendentText(item, "id");
                    Artist artist = new Artist(name, enid);
                    artists.add(artist);
                }
            }
            sm.end(tracker);
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Returns a list of blogs about the given artist
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of blogs about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Blog> getBlogs(Artist artist, int startRow, int count) throws EchoNestException {
        return getBlogs(artist.getId(), startRow, count);
    }

    /**
     * Returns a list of blogs about the given artist
     * @param id id of the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of blogs about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Blog> getBlogs(String id, int startRow, int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_blogs");
        try {
            String url = "get_blogs?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = Integer.parseInt(sfound);
            int shown = Integer.parseInt(sshown);
            int curStart = Integer.parseInt(sstart);

            DocumentList<Blog> list = new DocumentList<Blog>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Blog((Element) itemList.item(i)));
            }
            sm.end(tracker);
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Returns a list of news about the given artist
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of news about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<News> getNews(Artist artist, int startRow, int count) throws EchoNestException {
        return getNews(artist.getId(), startRow, count);
    }

    /**
     * Returns a list of news about the given artist
     * @param id of the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of news about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<News> getNews(String id, int startRow, int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_news");
        try {
            String url = "get_news?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = Integer.parseInt(sfound);
            int shown = Integer.parseInt(sshown);
            int curStart = Integer.parseInt(sstart);

            DocumentList<News> list = new DocumentList<News>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new News((Element) itemList.item(i)));
            }
            sm.end(tracker);
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Get found reviews for an artist's work
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Review> getReviews(Artist artist, int startRow, int count) throws EchoNestException {
        return getReviews(artist.getId(), startRow, count);
    }

    /**
     * Get found reviews for an artist's work
     * @param id the artist id of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Review> getReviews(String id, int startRow, int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_reviews");
        try {
            String url = "get_reviews?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = Integer.parseInt(sfound);
            int shown = Integer.parseInt(sshown);
            int curStart = Integer.parseInt(sstart);

            DocumentList<Review> list = new DocumentList<Review>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Review((Element) itemList.item(i)));
            }
            sm.end(tracker);
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Get a list of audio documents found on the web related to an artist.
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Audio> getAudio(Artist artist, int startRow, int count) throws EchoNestException {
        return getAudio(artist.getId(), startRow, count);
    }

    /**
     * Get a list of audio documents found on the web related to an artist.
     * @param id the artist id of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Audio> getAudio(String id, int startRow, int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_audio");
        try {
            String url = "get_audio?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = Integer.parseInt(sfound);
            int shown = Integer.parseInt(sshown);
            int curStart = Integer.parseInt(sstart);

            DocumentList<Audio> list = new DocumentList<Audio>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Audio((Element) itemList.item(i)));
            }
            sm.end(tracker);
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Get a list of video documents found on the web related to an artist.
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Video> getVideo(Artist artist, int startRow, int count) throws EchoNestException {
        return getVideo(artist.getId(), startRow, count);
    }

    /**
     * Get a list of video documents found on the web related to an artist.
     * @param id the id of the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public DocumentList<Video> getVideo(String id, int startRow, int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_video");
        try {
            String url = "get_video?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = Integer.parseInt(sfound);
            int shown = Integer.parseInt(sshown);
            int curStart = Integer.parseInt(sstart);

            DocumentList<Video> list = new DocumentList<Video>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Video((Element) itemList.item(i)));
            }
            sm.end(tracker);
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    private String encode(String parameter) {
        try {
            return URLEncoder.encode(parameter, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return parameter;
        }
    }

    /**
     * 
     * Returns a numerical estimation of how familiar an artist currently is to the world. 
     * @param artist the artist of interest
     * @return a number between 0 and 1. 1 is most familiar
     * @throws com.echonest.api.v3.EchoNestException
     */
    public float getFamiliarity(Artist artist) throws EchoNestException {
        return getFamiliarity(artist.getId());
    }

    /**
     *
     * Returns a numerical estimation of how familiar an artist currently is to the world.
     * @param id the id of the artist of interest
     * @return a number between 0 and 1. 1 is most familiar
     * @throws com.echonest.api.v3.EchoNestException
     */
    public float getFamiliarity(String id) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_familiarity");
        try {
            float familiarity = 0f;
            String cmdURL = "get_familiarity?id=" + id;
            Document doc = sendCommand(cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artistElement = XmlUtil.getFirstElement(docElement, "artist");
            String sFam = XmlUtil.getDescendentText(artistElement, "familiarity");
            if (sFam != null && sFam.length() > 0) {
                familiarity = Float.parseFloat(sFam);
            } else {
                System.err.println("no familiarty for " + id);
            }
            sm.end(tracker);
            return familiarity;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Returns a numerical description of how hottt an artist currently is.
     * @param id the id of the artist of interest
     * @return a number between 0 and 1. 1 is most hot
     * @throws com.echonest.api.v3.EchoNestException
     */
    public float getHotness(String id) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_hotttnesss");
        try {
            float hotness = 0f;
            String cmdURL = "get_hotttnesss?id=" + id;
            Document doc = sendCommand(cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artistElement = XmlUtil.getFirstElement(docElement, "artist");
            String sFam = XmlUtil.getDescendentText(artistElement, "hotttnesss");
            if (sFam != null) {
                hotness = Float.parseFloat(sFam);
            }
            sm.end(tracker);
            return hotness;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Returns a numerical description of how hottt an artist currently is.
     * @param artist  the artist of interest
     * @return a number between 0 and 1. 1 is most hot
     * @throws com.echonest.api.v3.EchoNestException
     */
    public float getHotness(Artist artist) throws EchoNestException {
        return getHotness(artist.getId());
    }

    /**
     * Retrieves a list of the top hottt artists
     * @param count the number of results to return
     * @return A list of artists, scrored by hotness
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Scored<Artist>> getTopHotttArtists(int count) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_top_hottt_artists");
        try {
            List<Scored<Artist>> artists = new ArrayList<Scored<Artist>>();

            String cmdURL = "get_top_hottt_artists?rows=" + count;

            Document doc = sendCommand(cmdURL);
            Element docElement = doc.getDocumentElement();
            NodeList itemList = docElement.getElementsByTagName("artist");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String name = XmlUtil.getDescendentText(item, "name");
                String id = XmlUtil.getDescendentText(item, "id");
                String sHotness = XmlUtil.getDescendentText(item, "hotttnesss");
                double hotness = 1;

                if (sHotness != null) {
                    hotness = Double.parseDouble(sHotness);
                }

                Artist artist = new Artist(name, id);
                artists.add(new Scored<Artist>(artist, hotness));
            }
            sm.end(tracker);
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * Gets a list of similar artists
     * @param artist the seed artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Scored<Artist>> getSimilarArtists(Artist artist, int start, int count) throws EchoNestException {
        return getSimilarArtists(artist.getId(), start, count);
    }

    /**
     * Return similar artists given one or more artists for comparison.
     * @param artists the list of seed artists artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Scored<Artist>> getSimilarArtists(List<Artist> artists, int start, int count) throws EchoNestException {
        String[] ids = new String[artists.size()];

        for (int i = 0; i < artists.size(); i++) {
            ids[i] = artists.get(i).getId();
        }
        return getSimilarArtists(ids, start, count);
    }

    /**
     * Return similar artists given one or more artists for comparison.
     * @param id the id of the seed artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Scored<Artist>> getSimilarArtists(String id, int start, int count) throws EchoNestException {
        String[] ids = new String[1];
        ids[0] = id;
        return getSimilarArtists(ids, start, count);
    }


    /**
     * Return similar artists given one or more artists for comparison.
     * @param ids the list of seed artists artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws com.echonest.api.v3.EchoNestException
     */
    public List<Scored<Artist>> getSimilarArtists(String[] ids, int start, int count) throws EchoNestException {
        String cmdURL = "get_similar?start=" + start + "&rows=" + count;
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            sb.append("&id=");
            sb.append(id);
        }
        cmdURL += sb.toString();
        List<Scored<Artist>> artists = fetchSimilarArtists(cmdURL);
        if (artists.size() > count) {
            System.err.printf("getSimilarArtists retuned %d, expected %d\n", artists.size(), count);
            artists = artists.subList(0, count);
        }
        return artists;
    }

    private List<Scored<Artist>> fetchSimilarArtists(String url) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_similar");
        try {
            List<Scored<Artist>> artists = new ArrayList<Scored<Artist>>();

            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "similar");
            NodeList itemList = similar.getElementsByTagName("artist");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String name = XmlUtil.getDescendentText(item, "name");
                String enid = XmlUtil.getDescendentText(item, "id");
                String srank = XmlUtil.getDescendentText(item, "rank");

                int rank = 1;
                if (srank != null) {
                    rank = Integer.parseInt(srank);
                }

                Artist artist = new Artist(name, enid);
                artists.add(new Scored<Artist>(artist, 1.0 / rank));
            }
            sm.end(tracker);
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    /**
     * save this for the track API
    public String uploadTrack(URL trackUrl, boolean wait) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("uploadTrack");
        try {
            String url = "upload?wait=" + (wait ? "Y" : "N");
            url += "&url=" + encode(trackUrl.toExternalForm());
            Document doc = sendCommand(url, true);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "track");
            String id = similar.getAttribute("id");
            sm.end(tracker);
            return id;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    public String uploadTrack(File trackFile, boolean wait) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("uploadTrack(file)");
        try {
            String url = "upload?wait=" + (wait ? "Y" : "N");
            url += "&file=" + encode(trackFile.getName());
            Document doc = sendCommand(url, true);
            // TBD: BUG - todo
            // Implement this
            sm.end(tracker);
            return "";
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }
     **/



    /**
     * Get links to the artist's official site, MusicBrainz site, MySpace site, 
     * Wikipedia article, Amazon list, and iTunes page. 
     * @param artist the artist of interest
     * @return the links for the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public Map<String, String> getUrls(Artist artist) throws EchoNestException {
        return getUrls(artist.getId());
    }

    /**
     * Get links to the artist's official site, MusicBrainz site, MySpace site,
     * Wikipedia article, Amazon list, and iTunes page.
     * @param id the id of the artist of interest
     * @return the links for the artist
     * @throws com.echonest.api.v3.EchoNestException
     */
    public Map<String, String> getUrls(String id) throws EchoNestException {
        StatsManager.Tracker tracker = sm.start("get_urls");
        try {
            Map<String, String> urls = new HashMap<String, String>();
            String url = "get_urls?id=" + id;
            Document doc = sendCommand(url);
            Element docElement = doc.getDocumentElement();
            Element artist = (Element) XmlUtil.getDescendent(docElement, "artist");
            NodeList pv = artist.getChildNodes();
            for (int i = 0; i < pv.getLength(); i++) {
                Element nestedElement = (Element) pv.item(i);
                String name = nestedElement.getTagName();
                if (name.endsWith("_url")) {
                    String value = nestedElement.getTextContent();
                    urls.put(name, value);
                }
            }
            sm.end(tracker);
            return urls;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            sm.close(tracker);
        }
    }

    private void checkStatus(Document doc) throws EchoNestException {
        try {
            Element docElement = doc.getDocumentElement();
            Element status = (Element) XmlUtil.getDescendent(docElement, "status");
            String scode = XmlUtil.getDescendentText(status, "code");
            String message = XmlUtil.getDescendentText(status, "message");
            int code = - 1;
            if (scode != null || scode.length() > 0) {
                try {
                    code = Integer.parseInt(scode);
                } catch (NumberFormatException e) {
                }
            }
            if (code != 0) {
                throw new EchoNestException(code, message);
            }
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }


    private Document sendCommand(String url) throws IOException, EchoNestException {
        Document doc = cache.get(url);
        if (doc == null) {
            doc = commander.sendCommand(url);
            checkStatus(doc);
            cache.put(url, doc);
        }
        return doc;
    }


    public static void main(String[] args) throws IOException {
        try {
            EchoNest echoNest = new EchoNest();
            echoNest.setTrace(true);
            int start = 0;
            int count = 15;

            DocumentList<Audio> audio = null;
            do {
                audio = echoNest.getAudio(
                        "music://id.echonest.com/~/AR/ARH6W4X1187B99274F", start, count);
                audio.dump();
                start = audio.getStart() + audio.getCount();
                count = audio.getTotal() - start;
                if (count > 15) {
                    count = 15;
                }
            } while (audio.getStart() + audio.getCount() < audio.getTotal());
        } catch (EchoNestException e) {
            System.out.println("Trouble: " + e);
        }
    }
}
