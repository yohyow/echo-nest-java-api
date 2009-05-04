/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author plamere
 */
public class ExpiringCache<T> {

    private HashMap<String, TimedItem<T>> cache = new HashMap<String, TimedItem<T>>();
    private long maxAge = 7 * 24 * 60 * 60 * 1000;
    private int count = 0;

    public T get(String key) {

        checkPurge();

        TimedItem<T> ti = cache.get(key);
        if (ti != null && ti.getAge() < maxAge) {
            return ti.getItem();
        } else {
            return null;
        }
    }

    public void put(String key, T t) {
        if (maxAge > 0) {
            cache.put(key, new TimedItem<T>(t));
        }
    }


    private void checkPurge() {
        if (count++ % 1000 == 0) {
            purge();
        }
    }

    private void purge() {
        Set<TimedItem<T>> removedSet = new HashSet<TimedItem<T>>();
        for (Entry<String, TimedItem<T>>  e : cache.entrySet()) {
            if (e.getValue().getAge() >= maxAge) {
                removedSet.add(e.getValue());
            }
        }

        for (TimedItem<T> item : removedSet) {
            cache.remove(item);
        }
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Attempts to save the cache to the given path
     * @param path the path
     * @throws java.io.IOException
     */
    public void save(String path) throws IOException {
        purge();
        File file = new File(path);
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(cache);
            oos.close();
        } catch (IOException ex) {
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Attempts to load the cache from the given path
     * @param path the path
     * @throws java.io.IOException
     */
    public void load(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object cacheObj = ois.readObject();
                if (cacheObj != null) {
                    HashMap<String, TimedItem<T>> newCache = (HashMap<String, TimedItem<T>>) cacheObj;
                    for (String key : newCache.keySet()) {
                        TimedItem<T> ti = newCache.get(key);
                        if (!isExpired(ti)) {
                            cache.put(key, ti);
                        }
                    }
                }
                purge();
            } catch (ClassNotFoundException ex) {
                System.err.println("Can't find the class " + ex);
            } catch (IOException ex) {
                System.err.println("Can't read cache " + ex);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException ex) {
                    System.err.println("Trouble with the close " + ex);
                }
            }
        }
    }

    private boolean isExpired(TimedItem<T> item)  {
        return item.getAge() > maxAge;
    }
}

class TimedItem<T> implements Serializable {

    private long created;
    private T item;

    TimedItem(T t) {
        item = t;
        created = System.currentTimeMillis();
    }

    public long getAge() {
        return System.currentTimeMillis() - created;
    }

    public T getItem() {
        return item;
    }

    long getCreated() {
        return created;
    }
}
