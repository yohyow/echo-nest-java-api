/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.track;

import com.echonest.api.util.EchoNestCommander;
import com.echonest.api.util.XmlUtil;
import com.echonest.api.v3.artist.EchoNestException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author plamere
 */
public class TrackAPI extends EchoNestCommander {
    /** The status of an analysis */
    public enum AnalysisStatus {UNKNOWN, PENDING, COMPLETE, ERROR};
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

    public TrackStatus uploadTrack(URL trackUrl, boolean wait) throws EchoNestException {
        try {
            String url = "upload?wait=" + (wait ? "Y" : "N");
            url += "&url=" + encode(trackUrl.toExternalForm());
            Document doc = sendCommand("upload_url", url, true);
            Element docElement = doc.getDocumentElement();
            Element trackElement = (Element) XmlUtil.getDescendent(docElement, "track");
            String trackID = trackElement.getAttribute("id");
            String md5 = trackElement.getAttribute("md5");
            boolean ready = trackElement.getAttribute("md5").equals("true");
            return new TrackStatus(trackID, md5, ready);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    public TrackStatus uploadTrack(File trackFile, boolean wait) throws EchoNestException {
        try {
            String url = "upload?wait=" + (wait ? "Y" : "N");
            url += "&file=" + encode(trackFile.getName());
            Document doc = sendCommand("upload_file", url, trackFile);
            Element docElement = doc.getDocumentElement();
            Element trackElement = (Element) XmlUtil.getDescendent(docElement, "track");
            String trackID = trackElement.getAttribute("id");
            String md5 = trackElement.getAttribute("md5");
            boolean ready = trackElement.getAttribute("md5").equals("true");
            return new TrackStatus(trackID, md5, ready);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    public float getDuration(String idOrMd5) throws EchoNestException {
        try {
            float duration = 0f;
            String cmdURL = "get_duration?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_duration", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            String sDur = XmlUtil.getDescendentText(analysisElement, "duration");
            if (sDur != null) {
                duration = Float.parseFloat(sDur);
            }
            return duration;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    private String getIDParam(String idOrMd5) {
        String pname = "md5";
        if (idOrMd5.startsWith("music:")) {
            pname = "id";
        }
        return pname + "=" + idOrMd5;
    }

    /**
     * Gets the analysis status for the given ID
     * @param iOrMd5d the analysis id or md5
     * @return the status of the analysis
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public AnalysisStatus getAnalysisStatus(String idOrMd5) throws EchoNestException {
        try {
            getDuration(idOrMd5);
            return AnalysisStatus.COMPLETE;
        }  catch (EchoNestException e) {
            if (e.getCode() == 11) {
                return AnalysisStatus.PENDING;
            } else {
                throw e;
            }
        }
    }

    /**
     * Wait for an analysis to finish
     * @param id the id of the track being analyzed (as returned from uploadTrack)
     * @param timeoutMillis maximum milliseconds to wait for the analysis
     * @return the status
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public AnalysisStatus waitForAnalysis(String idOrMD5, long timeoutMillis)  throws EchoNestException {
        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        AnalysisStatus status = AnalysisStatus.UNKNOWN;
        do {
            status = getAnalysisStatus(idOrMD5);
            elapsed = System.currentTimeMillis() - startTime;
        } while (status == AnalysisStatus.PENDING && elapsed < timeoutMillis);
        return status;
    }
}
