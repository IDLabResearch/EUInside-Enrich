package be.ugent.mmlab.europeana.enrichment.misc;

import java.util.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/17/14.
 */
public class CountArray<C extends Comparable<C>> {

    private class CountObject<C extends Comparable<C>> implements Comparable<CountObject<C>> {
        private final C cObject;
        private int count;

        public CountObject(C cObject) {
            this.cObject = cObject;
            count = 1;
        }

        public void increment() {
            count++;
            if (count > highestCount) {
                highestCount = count;
            }
        }

        @Override
        public int hashCode() {
            return cObject.hashCode();
        }


        @Override
        public int compareTo(CountObject<C> o) {
            int diff = o.count - count;
            if (diff == 0) {
                return o.cObject.compareTo(cObject);
            } else {
                return diff;
            }
        }
    }


    private final Map<C, CountObject<C>> countObjects = new HashMap<>();
    private int highestCount = 0;

    public int getHighestCount() {
        return highestCount;
    }

    public void add(C object) {
        CountObject<C> countObject = countObjects.get(object);
        if (countObject == null) {
            countObject = new CountObject<>(object);
            countObjects.put(object, countObject);
        } else {
            countObject.increment();
        }
    }

    public List<C> getSortedByCount(int minimumCount) {
        List<CountObject<C>> objectsByCount = new ArrayList<>();
        for (CountObject<C> countObject : countObjects.values()) {
            if (countObject.count >= minimumCount) {
                objectsByCount.add(countObject);
            }
        }
        Collections.sort(objectsByCount);
        List<C> sortedByCount = new ArrayList<>();
        for (CountObject<C> cCountObject : objectsByCount) {
            sortedByCount.add(cCountObject.cObject);
        }
        return sortedByCount;
    }

}
