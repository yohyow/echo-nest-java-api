/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

import java.util.Date;
import org.w3c.dom.Element;

/**
 * Represents a blog entry
 * @author plamere
 */
public class Blog extends Document {
    private static String[] fields = {"name", "url", "summary", "date_found"};

    Blog(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the blog name
     * @return the blog name
     */
    public String getName() {
        return get("name");
    }

    /**
     * Gets the blog url
     * @return the url
     */
    public String getURL() {
        return get("url");
    }

    /**
     * Gets the blog summary
     * @return the blog summary
     */
    public String getSummary() {
        return get("summary");
    }

    /**
     * Gets the date found
     * @return the date found
     */
    public Date getDateFound() {
        return getDate("date_found");
    }
}
