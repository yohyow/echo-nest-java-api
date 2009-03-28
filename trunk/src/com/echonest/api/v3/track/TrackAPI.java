/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echonest.api.v3.track;

import com.echonest.api.util.EchoNestCommander;
import com.echonest.api.v3.artist.EchoNestException;

/**
 *
 * @author plamere
 */
public class TrackAPI extends EchoNestCommander {
    /**
     * Creates an instance of the TrackAPI class using an API key specified in the
     * the property ECHO_NEST_API_KEY
     *
     * @throws com.echonest.api.v3.EchoNestException
     */
    public TrackAPI() throws EchoNestException {
        this(System.getProperty("ECHO_NEST_API_KEY"));
    }

    /**
     * Creates an instance of the TrackAPI class
     * @param key the TrackAPI key (available at http://developer.echonest.com/ )
     * @throws com.echonest.api.v3.EchoNestException
     */
    public TrackAPI(String key) throws EchoNestException {
        super(key, null);
    }

}
