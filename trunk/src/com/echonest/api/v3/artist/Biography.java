/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import org.w3c.dom.Element;

/**
 *
 * @author plamere
 */
public class Biography extends Document {
    private static String[] fields = {"text", "url"};

    Biography(Element element) throws EchoNestException {
        super(element, fields);
    }

    /**
     * Gets the name of the site that is the source of the biography
     * @return the site
     */
    public String getSite() {
        return get("url");
    }

    /**
     * Gets the bio text
     * @return the text of the biography
     */
    public String getText() {
        return get("text");
    }

    /**
     * gets the URL for the source of the biography
     * @return the url
     */
    public String getUrl() {
        return get("url");
    }
}
