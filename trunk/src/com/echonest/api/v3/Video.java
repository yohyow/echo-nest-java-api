/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3;

import java.util.Date;
import org.w3c.dom.Element;

/**
 * Represents a video
 * @author plamere
 */
public class Video extends Document {
    private static String[] fields = {"site", "title", "url", "date_found", "image_url"};

    Video(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the originating site of the video
     * @return the site
     */
    public String getSite() {
        return get("site");
    }

    /**
     * Gets the title of the video
     * @return the title
     */
    public String getTitle() {
        return get("title");
    }

    /**
     * Gets the URL of the video
     * @return the URL
     */
    public String getURL() {
        return get("url");
    }


    /**
     * Gets a thumbnail image of the video
     * @return a URL to the image
     */
    public String getImageUrl() {
        return get("image_url");
    }

    /**
     * Gets the date the image was found
     * @return the date found
     */
    public Date getDateFound() {
        return getDate("date_found");
    }
}
