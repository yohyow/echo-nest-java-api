/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3;

import java.util.Date;
import org.w3c.dom.Element;

/**
 * Represents a news story
 * @author plamere
 */
public class News extends Document {
    private static String[] fields = {"name", "url", "summary", "date_found"};

    News(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the news article title 
     * @return title
     */
    public String getName() {
        return get("name");
    }

    /**
     * Gets the URL of the item
     * @return the URL
     */
    public String getURL() {
        return get("url");
    }

    /**
     * Gets a summary of the item
     * @return the summary
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