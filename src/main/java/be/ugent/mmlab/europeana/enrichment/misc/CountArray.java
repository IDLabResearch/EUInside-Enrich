package be.ugent.mmlab.europeana.enrichment.misc;

import java.util.*;
import java.util.stream.Collectors;

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
            if (highestCount == 0) {
                highestCount = 1;
            }
        }

        public CountObject(C cObject, final int count) {
            this.cObject = cObject;
            this.count = count;
            if (count > highestCount) {
                highestCount = count;
            }
        }

        public void increment() {
            count++;
            if (count > highestCount) {
                highestCount = count;
            }
        }

        public void increment(int i) {
            count += i;
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

    public void addAll(final Collection<C> collection) {
        collection.forEach(this::add);
    }

    public void addAll(final CountArray<C> countArray) {
        for (Map.Entry<C, CountObject<C>> cCountObjectEntry : countArray.countObjects.entrySet()) {
            C object = cCountObjectEntry.getKey();
            CountObject<C> countObject = countObjects.get(object);
            if (countObject == null) {
                countObject = new CountObject<>(object, cCountObjectEntry.getValue().count);
                countObjects.put(object, countObject);
            } else {
                countObject.increment(cCountObjectEntry.getValue().count);
            }
        }
    }

    public void set(C object, final int count) {
        countObjects.put(object, new CountObject<>(object, count));
    }

    public List<C> getSortedByCount(int minimumCount) {
        List<CountObject<C>> objectsByCount = countObjects.values().stream()
                .filter(countObject -> countObject.count >= minimumCount)
                .map(countObject -> countObject).collect(Collectors.toList());
        Collections.sort(objectsByCount);
        return objectsByCount.stream()
                .map(cCountObject -> cCountObject.cObject)
                .collect(Collectors.toList());
    }

    public List<C> getSortedByCountMax(int maxItems) {
        List<CountObject<C>> objectsByCount = new ArrayList<>();
        objectsByCount.addAll(countObjects.values());
        Collections.sort(objectsByCount);

        List<C> sortedByCount = new ArrayList<>(maxItems);
        for (int i = 0; i < Math.min(maxItems, objectsByCount.size()); i++) {
            sortedByCount.add(objectsByCount.get(i).cObject);
        }
        return sortedByCount;
    }

}
