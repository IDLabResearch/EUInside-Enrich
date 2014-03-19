package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.misc.CountArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/19/14.
 */
public class TestCountArray {

    @Test
    public void simpleTest() {
        CountArray<String> countArray = new CountArray<>();
        countArray.add("de");
        countArray.add("kat");
        countArray.add("vangt");
        countArray.add("de");
        countArray.add("muis");
        List<String> ranked = countArray.getSortedByCount(0);
        for (String s : ranked) {
            System.out.println(s);
        }
    }

    @Test
    public void testAddCountArray() {
        CountArray<String> countArray1 = new CountArray<>();
        countArray1.addAll(Arrays.asList("this", "is", "a", "test"));
        CountArray<String> countArray2 = new CountArray<>();
        countArray2.addAll(Arrays.asList("and", "this", "is", "another", "test"));
        countArray1.addAll(countArray2);
        for (String s : countArray1.getSortedByCount(countArray1.getHighestCount())) {
            System.out.println(s);
        }
    }
}
