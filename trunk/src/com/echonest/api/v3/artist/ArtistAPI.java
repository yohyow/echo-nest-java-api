/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import com.echonest.api.v3.EchoNestCommander;
import com.echonest.api.util.XmlUtil;
import java.io.IOException;
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
public class ArtistAPI extends EchoNestCommander {

    /** maximum rows returned by many calls*/
    public final static int MAX_ROWS = 15;

    /**
     * Creates an instance of the ArtistAPI class using an ArtistAPI key specified in the
     * the property ECHO_NEST_API_KEY
     * 
     * @throws EchoNestException
     */
    public ArtistAPI() throws EchoNestException {
        this(System.getProperty("ECHO_NEST_API_KEY"));
    }

    /**
     * Creates an instance of the ArtistAPI class
     * @param key the ArtistAPI key (available at http://developer.echonest.com/ )
     * @throws EchoNestException
     */
    public ArtistAPI(String key) throws EchoNestException {
        super(key, null);
    }


    /**
     * Given an Echo Nest Identifier get  the associated artist
     * @param id an Echo Nest Identifier
     * @return an artist 
     * @throws EchoNestException
     */
    public Artist getProfile(String id) throws EchoNestException {

        try {
            String cmdURL = "get_profile?id=" + id;
            Document doc = sendCommand("get_profile", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artist = (Element) XmlUtil.getDescendent(docElement, "artist");
            String newid = XmlUtil.getDescendentText(artist, "id");
            String name = XmlUtil.getDescendentText(artist, "name");

            if (!id.equals(newid)) {
                throw new EchoNestException(EchoNestException.CLIENT_SERVER_INCONSISTENCY, "mismatch ID");
            }
            return new Artist(name, newid);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Searches for artists by name
     * @param artistName the artist name
     * @param soundsLike if true, a 'sounds like' comparison is used in the search
     *  otherwise, only exact matches are returned.
     * @return a list of matching artists, ordered by how well they match the query.
     * @throws EchoNestException
     */
    public List<Artist> searchArtist(String artistName, boolean soundsLike) throws EchoNestException {
        List<Artist> artists = new ArrayList<Artist>();

        try {
            String cmdURL = "search_artists?query=" + encode(artistName);
            cmdURL += "&sounds_like=" + (soundsLike ? "Y" : "N");
            Document doc = sendCommand("search_artist", cmdURL);
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
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Returns a list of blogs about the given artist
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of blogs about the artist
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public DocumentList<Blog> getBlogs(String id, int startRow, int count) throws EchoNestException {
        try {
            String url = "get_blogs?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand("get_blogs", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = parseInt("get_blogs found", sfound);
            int shown = parseInt("get_blogs shown", sshown);
            int curStart = parseInt("get_blogs start", sstart);

            DocumentList<Blog> list = new DocumentList<Blog>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Blog((Element) itemList.item(i)));
            }
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Returns a list of news about the given artist
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of news about the artist
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public DocumentList<News> getNews(String id, int startRow, int count) throws EchoNestException {
        try {
            String url = "get_news?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand("get_news", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = parseInt("get_news found", sfound);
            int shown = parseInt("get_news shown", sshown);
            int curStart = parseInt("get_news start", sstart);

            DocumentList<News> list = new DocumentList<News>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new News((Element) itemList.item(i)));
            }
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Get found reviews for an artist's work
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public DocumentList<Review> getReviews(String id, int startRow, int count) throws EchoNestException {
        try {
            String url = "get_reviews?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand("get_reviews", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = parseInt("get_reviews found", sfound);
            int shown = parseInt("get_reviews shown", sshown);
            int curStart = parseInt("get_reviews start", sstart);

            DocumentList<Review> list = new DocumentList<Review>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Review((Element) itemList.item(i)));
            }
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Get a list of audio documents found on the web related to an artist.
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public DocumentList<Audio> getAudio(String id, int startRow, int count) throws EchoNestException {
        try {
            String url = "get_audio?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand("get_audio", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = parseInt("get_audio found", sfound);
            int shown = parseInt("get_audio shown", sshown);
            int curStart = parseInt("get_audio start", sstart);

            DocumentList<Audio> list = new DocumentList<Audio>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Audio((Element) itemList.item(i)));
            }
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Get a list of video documents found on the web related to an artist.
     * @param artist the artist of interest
     * @param startRow the starting row of the query
     * @param count the number of items returned
     * @return a list of reviews about the artist
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public DocumentList<Video> getVideo(String id, int startRow, int count) throws EchoNestException {
        try {
            String url = "get_video?id=" + id + "&start=" + startRow + "&rows=" + count;

            Document doc = sendCommand("get_video", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "results");

            String sfound = similar.getAttribute("found");
            String sshown = similar.getAttribute("shown");
            String sstart = similar.getAttribute("start");

            int found = parseInt("get_video found", sfound);
            int shown = parseInt("get_video shown", sshown);
            int curStart = parseInt("get_video start", sstart);

            DocumentList<Video> list = new DocumentList<Video>(found, curStart, shown);

            NodeList itemList = similar.getElementsByTagName("doc");
            for (int i = 0; i < itemList.getLength(); i++) {
                list.add(new Video((Element) itemList.item(i)));
            }
            return list;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }


    /**
     * 
     * Returns a numerical estimation of how familiar an artist currently is to the world. 
     * @param artist the artist of interest
     * @return a number between 0 and 1. 1 is most familiar
     * @throws EchoNestException
     */
    public float getFamiliarity(Artist artist) throws EchoNestException {
        return getFamiliarity(artist.getId());
    }

    /**
     *
     * Returns a numerical estimation of how familiar an artist currently is to the world.
     * @param id the id of the artist of interest
     * @return a number between 0 and 1. 1 is most familiar
     * @throws EchoNestException
     */
    public float getFamiliarity(String id) throws EchoNestException {
        try {
            float familiarity = 0f;
            String cmdURL = "get_familiarity?id=" + id;
            Document doc = sendCommand("get_familiarity", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artistElement = XmlUtil.getFirstElement(docElement, "artist");
            String sFam = XmlUtil.getDescendentText(artistElement, "familiarity");
            if (sFam != null && sFam.length() > 0) {
                familiarity = parseFloat("familiarity", sFam);
            } else {
                System.err.println("no familiarty for " + id);
            }
            return familiarity;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Returns a numerical description of how hottt an artist currently is.
     * @param id the id of the artist of interest
     * @return a number between 0 and 1. 1 is most hot
     * @throws EchoNestException
     */
    public float getHotness(String id) throws EchoNestException {
        try {
            float hotness = 0f;
            String cmdURL = "get_hotttnesss?id=" + id;
            Document doc = sendCommand("get_hottttnesss", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element artistElement = XmlUtil.getFirstElement(docElement, "artist");
            String sFam = XmlUtil.getDescendentText(artistElement, "hotttnesss");
            if (sFam != null) {
                hotness = parseFloat("hotness", sFam);
            }
            return hotness;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Returns a numerical description of how hottt an artist currently is.
     * @param artist  the artist of interest
     * @return a number between 0 and 1. 1 is most hot
     * @throws EchoNestException
     */
    public float getHotness(Artist artist) throws EchoNestException {
        return getHotness(artist.getId());
    }


    /**
     * Retrieves a list of the top hottt artists
     * @param count the number of results to return
     * @return A list of artists, scrored by hotness
     * @throws EchoNestException
     */
    public List<Scored<Artist>> getTopHotttArtists(int count) throws EchoNestException {
        try {
            List<Scored<Artist>> artists = new ArrayList<Scored<Artist>>();

            String cmdURL = "get_top_hottt_artists?rows=" + count;

            Document doc = sendCommand("get_top_hottt_artists", cmdURL);
            Element docElement = doc.getDocumentElement();
            NodeList itemList = docElement.getElementsByTagName("artist");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String name = XmlUtil.getDescendentText(item, "name");
                String id = XmlUtil.getDescendentText(item, "id");
                String sHotness = XmlUtil.getDescendentText(item, "hotttnesss");
                float hotness = 1;

                if (sHotness != null) {
                    hotness = parseFloat("get_top_hottt_artists", sHotness);
                }

                Artist artist = new Artist(name, id);
                artists.add(new Scored<Artist>(artist, hotness));
            }
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Gets a list of similar artists
     * @param artist the seed artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws EchoNestException
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
     * @throws EchoNestException
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
     * @throws EchoNestException
     */
    public List<Scored<Artist>> getSimilarArtists(String id, int start, int count) throws EchoNestException {
        String[] ids = new String[1];
        ids[0] = id;
        return getSimilarArtists(ids, start, count);
    }

    public List<Scored<Artist>> getSimilarArtists(String id, int start, int count, boolean limit) throws EchoNestException {
        String[] ids = new String[1];
        ids[0] = id;
        return getSimilarArtists(ids, start, count, limit);
    }


    /**
     * Return similar artists given one or more artists for comparison.
     * @param ids the list of seed artists artist
     * @param start the starting position
     * @param count the number of artists to return
     * @return a scored list of similar artists
     * @throws EchoNestException
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

    public List<Scored<Artist>> getSimilarArtists(String[] ids, int start, int count, boolean limit) throws EchoNestException {
        String cmdURL = "get_similar?start=" + start + "&rows=" + count + "&limit=" + (limit ? "Y"  : "N");
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
        try {
            List<Scored<Artist>> artists = new ArrayList<Scored<Artist>>();

            Document doc = sendCommand("get_similar", url);
            Element docElement = doc.getDocumentElement();
            Element similar = (Element) XmlUtil.getDescendent(docElement, "similar");
            NodeList itemList = similar.getElementsByTagName("artist");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String name = XmlUtil.getDescendentText(item, "name");
                String enid = XmlUtil.getDescendentText(item, "id");
                String srank = XmlUtil.getDescendentText(item, "rank");
                int rank = parseInt("get_similar rank", srank);
                Artist artist = new Artist(name, enid);
                artists.add(new Scored<Artist>(artist, 1.0 / rank));
            }
            return artists;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }




    /**
     * Get links to the artist's official site, MusicBrainz site, MySpace site, 
     * Wikipedia article, Amazon list, and iTunes page. 
     * @param artist the artist of interest
     * @return the links for the artist
     * @throws EchoNestException
     */
    public Map<String, String> getUrls(Artist artist) throws EchoNestException {
        return getUrls(artist.getId());
    }

    /**
     * Get links to the artist's official site, MusicBrainz site, MySpace site,
     * Wikipedia article, Amazon list, and iTunes page.
     * @param id the id of the artist of interest
     * @return the links for the artist
     * @throws EchoNestException
     */
    public Map<String, String> getUrls(String id) throws EchoNestException {
        try {
            Map<String, String> urls = new HashMap<String, String>();
            String url = "get_urls?id=" + id;
            Document doc = sendCommand("get_urls", url);
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
            return urls;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }


    /**
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            ArtistAPI echoNest = new ArtistAPI();
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
