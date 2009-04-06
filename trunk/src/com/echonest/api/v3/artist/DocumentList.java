/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.v3.artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of documents
 * @param <T> the type of document
 * @author plamere
 */
public class DocumentList<T extends Document> {
    private List<T> documents;
    private int total;
    private int start;
    private int count;

    DocumentList(int total, int start, int count) {
        this.total = total;
        this.start = start;
        this.count = count;
        documents = new ArrayList<T>();
    }

    /**
     * Dumps the document list for debugging purposes
     */
    public void dump() {
        System.out.printf("Total: %d  Start: %d  Count: %d\n", total, start, count);
        for (T t : documents) {
            t.dump();
        }
    }


    void add(T doc) {
        documents.add(doc);
    }

    /**
     * Gets the number of elements available
     * @return the number of elements
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the list of documents
     * @return the list of documents
     */
    public List<T> getDocuments() {
        return documents;
    }

    /**
     * Gets the starting index
     * @return the starting index
     */
    public int getStart() {
        return start;
    }

    /**
     * Gets the total available
     * @return the total available
     */
    public int getTotal() {
        return total;
    }
}
