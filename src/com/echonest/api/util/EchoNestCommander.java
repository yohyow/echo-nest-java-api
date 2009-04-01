/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.util;

import com.echonest.api.v3.artist.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A client side API for the Echo Nest developer API.  The Echo Nest developer
 * API requires an API key. You can obtain a key at: http://developer.echonest.com/
 *
 * This client supports cacheing of the return results. The cache is enabled by
 * default.
 *
 * @author plamere
 */
public class EchoNestCommander {

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
    public EchoNestCommander() throws EchoNestException {
        this(System.getProperty("ECHO_NEST_API_KEY"), null);
    }

    /**
     * Creates an instance of the EchoNest class
     * @param key the API key (available at http://developer.echonest.com/ )
     * @throws com.echonest.api.v3.EchoNestException
     */
    public EchoNestCommander(String key, String prefix) throws EchoNestException {
        if (key == null) {
            System.err.println("No API Key is defined. Get a key from http://developer.echonest.com");
            throw new EchoNestException(EchoNestException.ERR_NO_KEY, "No API key defined");
        }

        try {
            //commander = new Commander("EchoNest", "http://developer.echonest.com/api/", "&version=3&api_key=" + key);
            if (prefix == null) {
                prefix = System.getProperty("ECHO_NEST_API_PREFIX");
            }

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

    protected void checkStatus(Document doc) throws EchoNestException {
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

    protected Document sendCommand(String name, String url) throws IOException, EchoNestException {
        return sendCommand(name, url, false);
    }

    protected Document sendCommand(String name, String url, File file) throws IOException, EchoNestException {
        StatsManager.Tracker tracker = sm.start(name);
        try {
            Document doc = commander.sendCommand(name, file);
            checkStatus(doc);
            return doc;
        } finally {
            sm.close(tracker);
        }
    }

    protected Document sendCommand(String name, String url, boolean usePost) throws IOException, EchoNestException {
        Document doc = null;

        if (!usePost) {
            doc = cache.get(url);
        }

        if (doc == null) {
            StatsManager.Tracker tracker = sm.start(name);
            try {
                doc = commander.sendCommand(url, usePost);
                checkStatus(doc);
                sm.end(tracker);
                if (!usePost) {
                    cache.put(url, doc);
                }
            } finally {
                sm.close(tracker);
            }
        }
        return doc;
    }

    protected String encode(String parameter) {
        try {
            return URLEncoder.encode(parameter, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            return parameter;
        }
    }
}
