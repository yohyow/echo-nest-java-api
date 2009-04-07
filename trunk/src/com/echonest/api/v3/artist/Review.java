/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * Represents a review
 * @author plamere
 */
public class Review extends Document {
    private static String[] fields = {"name", "review_text", "url", "summary", "date_found", "image_url"};

    Review(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the title of the review
     * @return the title
     */
    public String getName() {
        return get("name");
    }

    /**
     * Gets the review text
     * @return the review text
     */
    public String getReviewText() {
        return get("review_text");
    }

    /**
     * Gets the URL of the review
     * @return the URL
     */
    public String getURL() {
        return get("url");
    }

    /**
     * Gets a summary of the review
     * @return a summary
     */
    public String getSummary() {
        return get("summary");
    }

    /**
     * Gets an image associated with the review
     * @return the image url
     */
    public String getImageUrl() {
        return get("image_url");
    }

    /**
     * Get the date the item was found
     * @return the date
     */
    public Date getDateFound() {
        return getDate("date_found");
    }
}
