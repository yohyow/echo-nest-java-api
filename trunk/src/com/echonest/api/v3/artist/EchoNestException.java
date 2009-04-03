/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

/**
 * Represents echo nest oriented exceptions
 * @author plamere
 */

public class EchoNestException extends Exception {
    /** access to an invalid field */
     public final static int ERR_INVALID_FIELDS = -2;
     /** a bad ID */
     public final static int ERR_BAD_ID = -3;
     /** no api key was given */
     public final static int ERR_NO_KEY = -3;
     /** MD5s mismatch */
     public final static int ERR_BAD_MD5 = -4;

     private int code = -1;
     private String message;

    /**
     * Creates an exception
     * @param code the error code
     * @param message a description of the exception
     */
    public EchoNestException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Creates an exception
     * @param arg0 the wrapped throwable
     */
    public EchoNestException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Gets the error code
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the exception message
     * @return the message
     */
    public String getMessage() {
        if (message != null) {
            return String.format("(%d) %s", code, message);
        } else {
            return super.getMessage();
        }
    }
}
