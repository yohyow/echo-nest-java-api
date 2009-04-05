/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v3.track;

import com.echonest.api.util.EchoNestCommander;
import com.echonest.api.util.Utilities;
import com.echonest.api.util.XmlUtil;
import com.echonest.api.v3.artist.EchoNestException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author plamere
 */
public class TrackAPI extends EchoNestCommander {

    /** The status of an analysis */
    public enum AnalysisStatus {

        UNKNOWN, PENDING, COMPLETE, ERROR
    };

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

    public String uploadTrack(URL trackUrl) throws EchoNestException {
        try {
            boolean wait = false;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("wait", (wait ? "Y" : "N"));
            params.put("version", "3");
            params.put("api_key", getKey());
            params.put("url", trackUrl.toExternalForm());
            Document doc = postCommand("upload_url", "upload", params);
            Element docElement = doc.getDocumentElement();
            Element trackElement = (Element) XmlUtil.getDescendent(docElement, "track");
            String trackID = trackElement.getAttribute("id");
            String md5 = trackElement.getAttribute("md5");
            boolean ready = trackElement.getAttribute("md5").equals("true");
            return trackID;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Uploads the audio to the echonest for analysis
     * @param trackFile the file to upload
     * @return the ID or the MD5 of the track. The id is returned if the track 
     *    was uploaded, the MD5 is returned if the track was already uploaded
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public String uploadTrack(File trackFile) throws EchoNestException {
        try {
            String fileMD5 = Utilities.md5(trackFile);
            if (!isKnownTrack(fileMD5)) {
                boolean wait = false;
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("wait", (wait ? "Y" : "N"));
                params.put("version", "3");
                params.put("api_key", getKey());
                params.put("file", trackFile);
                Document doc = postCommand("upload_file", "upload", params);
                Element docElement = doc.getDocumentElement();
                Element trackElement = (Element) XmlUtil.getDescendent(docElement, "track");
                String trackID = trackElement.getAttribute("id");
                String md5 = trackElement.getAttribute("md5");
                if (!fileMD5.equals(md5)) {
                    throw new EchoNestException(EchoNestException.ERR_BAD_MD5,
                            "MD5 of analysis mismatches MD5 of file, found " + md5 + " expected " + fileMD5);
                }
                boolean ready = trackElement.getAttribute("md5").equals("true");
                return trackID;
            }
            return fileMD5;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Gets the duration of a previously analyzed track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the duration of the track in seconds
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
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

    /**
     * Retrieves the start of the fade out at the end of a track in seconds
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the fade-out time in seconds
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public float getStartOfFadeOut(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_start_of_fade_out?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_start_of_fade_out", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return XmlUtil.getDescendentTextAsFloat(analysisElement, "start_of_fade_out", 0f);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves the estimated modality (major or minor) of a track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the fade-in time in seconds
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public IntWithConfidence getMode(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_mode?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_mode", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return getIntWithConfidenceFromElement(analysisElement, "mode");
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves the estimated overall key of a track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the key
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public IntWithConfidence getKey(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_key?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_key", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return getIntWithConfidenceFromElement(analysisElement, "key");
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves the end of the fade-in introduction to a track in seconds
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the fade-in time in seconds
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public float getEndOfFadeIn(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_end_of_fade_in?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_end_of_fade_in", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return XmlUtil.getDescendentTextAsFloat(analysisElement, "end_of_fade_in", 0f);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves an estimated overall time signature of a previously uploaded track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the number of beats per bar
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public IntWithConfidence getTimeSignature(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_time_signature?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_time_signature", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return getIntWithConfidenceFromElement(analysisElement, "time_signature");
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves ID3 metadata for a track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return beats per minute
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public Metadata getMetadata(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_metadata?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_metadata", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            Metadata metadata = new Metadata();
            metadata.setArtist(XmlUtil.getDescendentText(analysisElement, "artist"));
            metadata.setTitle(XmlUtil.getDescendentText(analysisElement, "title"));
            metadata.setRelease(XmlUtil.getDescendentText(analysisElement, "release"));
            metadata.setGenre(XmlUtil.getDescendentText(analysisElement, "genre"));
            metadata.setDuration(XmlUtil.getDescendentTextAsFloat(analysisElement, "duration", 0f));
            metadata.setSamplerate(XmlUtil.getDescendentTextAsInt(analysisElement, "samplerate", 0));
            metadata.setBitrate(XmlUtil.getDescendentTextAsInt(analysisElement, "bitrate", 0));
            return metadata;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves an overall estimated tempo of a track in beats per minute
     * @param idOrMd5 the ID or the MD5 of the track
     * @return beats per minute
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public FloatWithConfidence getTempo(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_tempo?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_tempo", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return getFloatWithConfidenceFromElement(analysisElement, "tempo");
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves a set of times of beats of a track (in seconds)
     * @param idOrMd5 the ID or the MD5 of the track
     * @return list of times with confidences
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public List<FloatWithConfidence> getBeats(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_beats?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_beats", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            List<FloatWithConfidence> results = new ArrayList<FloatWithConfidence>();
            NodeList beatNodes = analysisElement.getElementsByTagName("beat");
            for (int i = 0; i < beatNodes.getLength(); i++) {
                Element beat = (Element) beatNodes.item(i);
                results.add(getFloatWithConfidenceFromElement(beat));
            }
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves a set of times of bars of a track (in seconds)
     * @param idOrMd5 the ID or the MD5 of the track
     * @return list of times with confidences
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public List<FloatWithConfidence> getBars(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_bars?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_bars", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            List<FloatWithConfidence> results = new ArrayList<FloatWithConfidence>();
            NodeList beatNodes = analysisElement.getElementsByTagName("bar");
            for (int i = 0; i < beatNodes.getLength(); i++) {
                Element beat = (Element) beatNodes.item(i);
                results.add(getFloatWithConfidenceFromElement(beat));
            }
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves a set of times of tatums of a track (in seconds)
     * @param idOrMd5 the ID or the MD5 of the track
     * @return list of times with confidences
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public List<FloatWithConfidence> getTatums(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_tatums?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_tatums", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            List<FloatWithConfidence> results = new ArrayList<FloatWithConfidence>();
            NodeList beatNodes = analysisElement.getElementsByTagName("tatum");
            for (int i = 0; i < beatNodes.getLength(); i++) {
                Element beat = (Element) beatNodes.item(i);
                results.add(getFloatWithConfidenceFromElement(beat));
            }
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves a set of segments of a track. A segment is a short sound entity that is
     * relatively uniform in timbre and harmony.
     * @param idOrMd5 the ID or the MD5 of the track
     * @return list of sections
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public List<Segment> getSegments(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_segments?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_segments", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            List<Segment> results = new ArrayList<Segment>();
            NodeList nodes = analysisElement.getElementsByTagName("segments");
            for (int i = 0; i < nodes.getLength(); i++) {
                Segment segment = new Segment();

                Element segmentElement = (Element) nodes.item(i);
                float startTime = Float.parseFloat(segmentElement.getAttribute("start"));
                float duration = Float.parseFloat(segmentElement.getAttribute("duration"));

                segment.setDuration(duration);
                segment.setStart(startTime);

                {
                    // get the loudness info
                    NodeList loudnessNodes = segmentElement.getElementsByTagName("dB");
                    for (int j = 0; j < loudnessNodes.getLength(); j++) {
                        Element dbElement = (Element) nodes.item(j);
                        float db = Float.parseFloat(dbElement.getTextContent());
                        if ("max".equalsIgnoreCase(dbElement.getAttribute("type"))) {
                            float maxTime = Float.parseFloat(dbElement.getAttribute("time"));
                            segment.setMaxLoudnessTimeOffset(maxTime);
                            segment.setMaxLoudness(db);
                        } else {
                            segment.setStartLoudness(db);
                        }
                    }
                }

                {
                    // get the pitches
                    NodeList pitchNodes = segmentElement.getElementsByTagName("pitch");
                    float[] pitch = new float[pitchNodes.getLength()];
                    for (int j = 0; j < pitchNodes.getLength(); j++) {
                        Element pitchElement = (Element) nodes.item(j);
                        pitch[j] = Float.parseFloat(pitchElement.getTextContent());
                    }
                    segment.setPitches(pitch);
                }

                {
                    // get the timbre coeffes
                    NodeList coeffNodes = segmentElement.getElementsByTagName("coeff");
                    float[] coeff = new float[coeffNodes.getLength()];
                    for (int j = 0; j < coeffNodes.getLength(); j++) {
                        Element coeffElement = (Element) nodes.item(j);
                        coeff[j] = Float.parseFloat(coeffElement.getTextContent());
                    }
                    segment.setTimbre(coeff);
                }

                results.add(segment);
            }
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves a set of the locations of sections of a track
     * @param idOrMd5 the ID or the MD5 of the track
     * @return list of sections
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public List<Section> getSections(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_sections?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_sections", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            List<Section> results = new ArrayList<Section>();
            NodeList nodes = analysisElement.getElementsByTagName("section");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element section = (Element) nodes.item(i);
                float start = Float.parseFloat(section.getAttribute("start"));
                float duration = Float.parseFloat(section.getAttribute("duration"));
                results.add(new Section(start, duration));
            }
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    /**
     * Retrieves an overall loudness of a track in decibels
     * @param idOrMd5 the ID or the MD5 of the track
     * @return the loudness of the track
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public float getOverallLoudness(String idOrMd5) throws EchoNestException {
        try {
            String cmdURL = "get_loudness?" + getIDParam(idOrMd5);
            Document doc = sendCommand("get_loudness", cmdURL);
            Element docElement = doc.getDocumentElement();
            Element analysisElement = XmlUtil.getFirstElement(docElement, "analysis");
            return XmlUtil.getDescendentTextAsFloat(analysisElement, "loudness", 0);
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    private IntWithConfidence getIntWithConfidenceFromElement(Element element, String subElementName)
            throws EchoNestException {
        try {
            Element subElement = XmlUtil.getFirstElement(element, subElementName);
            String sconf = subElement.getAttribute("confidence");
            String sval = subElement.getTextContent();
            return new IntWithConfidence(Float.parseFloat(sconf), Integer.parseInt(sval));
        } catch (NumberFormatException e) {
            throw new EchoNestException(e);
        } catch (IOException e) {
            throw new EchoNestException(e);
        }
    }

    private FloatWithConfidence getFloatWithConfidenceFromElement(Element element, String subElementName)
            throws EchoNestException {
        try {
            Element subElement = XmlUtil.getFirstElement(element, subElementName);
            String sconf = subElement.getAttribute("confidence");
            String sval = subElement.getTextContent();
            return new FloatWithConfidence(Float.parseFloat(sconf), Float.parseFloat(sval));
        } catch (NumberFormatException e) {
            throw new EchoNestException(e);
        } catch (IOException e) {
            throw new EchoNestException(e);
        }
    }

    private FloatWithConfidence getFloatWithConfidenceFromElement(Element element) throws EchoNestException {
        try {
            String sconf = element.getAttribute("confidence");
            String sval = element.getTextContent();
            return new FloatWithConfidence(Float.parseFloat(sconf), Float.parseFloat(sval));
        } catch (NumberFormatException e) {
            throw new EchoNestException(e);
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
        } catch (EchoNestException e) {
            if (e.getCode() == 11) {
                return AnalysisStatus.PENDING;
            } else {
                throw e;
            }
        }
    }

    /**
     * Determines whether or not the track is known by the echo nest
     * @param iOrMd5d the analysis id or md5
     * @return true if the track is known
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public boolean isKnownTrack(String idOrMd5) {
        try {
            getAnalysisStatus(idOrMd5);
            return true;
        } catch (EchoNestException e) {
            return false;
        }
    }

    /**
     * Wait for an analysis to finish
     * @param id the id of the track being analyzed (as returned from uploadTrack)
     * @param timeoutMillis maximum milliseconds to wait for the analysis
     * @return the status
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public AnalysisStatus waitForAnalysis(String idOrMD5, long timeoutMillis) throws EchoNestException {
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
