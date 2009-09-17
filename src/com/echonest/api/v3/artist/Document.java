/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v3.artist;

import com.echonest.api.v3.EchoNestException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Represents a document in the system. A Document has an ID, a type and a set of
 * field / value pairs.
 * @author plamere
 */
public class Document {
    private String id;
    private String type;
    private Map<String, String> values = new HashMap<String, String>();
    private boolean disableFieldCheck = true;

    Document(Element element, String[] validFields) throws EchoNestException {
        values = new HashMap<String, String>();
        id = element.getAttribute("id");
        type = element.getAttribute("type");
        NodeList pv = element.getChildNodes();
        for (int i = 0; i < pv.getLength(); i++) {
            Element nestedElement = (Element) pv.item(i);
            String name = nestedElement.getTagName();
            String value = nestedElement.getTextContent();
            values.put(name, value);
        }
        checkFields(values.keySet(), validFields);
    }

    Document(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    String get(String name) {
        return values.get(name);
    }

    void put(String name, String value) {
        values.put(name, value);
    }

    float getFloat(String name) {
        String sval = values.get(name);
        try {
            return Float.parseFloat(sval);
        } catch (NumberFormatException e) {
            System.err.println("Bad float format for " + name);
            return 0f;
        }
    }

    Date getDate(String name) {
        String date = get(name);
        SimpleDateFormat sdf = new SimpleDateFormat();
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            System.err.println("Bad date format " + date);
            return null;
        }
    }

    /**
     * Dumps the document
     */
    public void dump() {
        System.out.println("id: " + id + " type: " + type);
        for (String key : values.keySet()) {
            String value = values.get(key);
            System.out.printf("  %s: %s\n", key, value);
        }
        System.out.println("");
    }

    private void checkFields(Set<String> found, String[] validFields) throws EchoNestException {
        if (!disableFieldCheck && validFields != null) {
            Set<String> validSet = new HashSet<String>(Arrays.asList(validFields));

            if (!(found.containsAll(validSet) && validSet.containsAll(found))) {
                for (String s : validSet) {
                    if (!found.contains(s)) {
                        // System.err.println("Missing (" + s + ")");
                    }
                }

                for (String s : found) {
                    if (!validSet.contains(s)) {
                        throw new EchoNestException(EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                                "Extra field " + s);
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<String>(values.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            sb.append(key + ": " + values.get(key) + "\n");
        }
        return sb.toString();
    }
}
