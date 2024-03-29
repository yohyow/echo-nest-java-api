/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */

package com.echonest.api.util;

import com.echonest.api.v3.artist.Scored;
import java.util.Comparator;

/**
 * A Comparator for <Scored<T>>s
 */

public class ScoredComparator<T> implements Comparator<Scored<T>> {

    public final static ScoredComparator COMPARATOR = new ScoredComparator();

    public int compare(Scored<T> o1, Scored<T> o2) {
        if (o1.getScore() > o2.getScore()) {
            return 1;
        } else if (o1.getScore() < o2.getScore()) {
            return -1;
        } else {
            return 0;
        }
    }
}
