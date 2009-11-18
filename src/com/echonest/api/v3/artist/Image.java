/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import org.w3c.dom.Element;

/**
 * Represents a news story
 * @author plamere
 */
public class Image extends Document {
    private static String[] fields = {"url"};

    Image(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the URL of the item
     * @return the URL
     */
    public String getURL() {
        return get("url");
    }

}
