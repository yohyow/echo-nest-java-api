/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.artist;

/**
 *
 * @author plamere
 */
public class Biography {
    private String text;
    private String site;
    private String url;

    Biography(String text, String site, String url) {
        this.text = text;
        this.site = site;
        this.url = url;
    }

    /**
     * Gets the name of the site that is the source of the biography
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Gets the bio text
     * @return the text of the biography
     */
    public String getText() {
        return text;
    }

    /**
     * gets the URL for the source of the biography
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
         return text + "\n( via " + site + " " + url + ")\n\n";
    }
}
