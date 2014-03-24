package be.ugent.mmlab.europeana.enrichment.misc;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/24/14.
 */
public class Normalizer {

    public static String normalize(final String input) {
        // remove everything between brackets
        String normalized = input.replaceAll("[\\[\\(].*?[\\]\\)]", ""); // extra '?' -> lazy matching

        // remove words starting with lower case
        normalized = normalized.replaceAll("\\s\\p{javaLowerCase}+", " ");
        normalized = normalized.replaceAll("^\\p{javaLowerCase}+", "");

        // remove accents and diacritics
        normalized = java.text.Normalizer.normalize(normalized, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // remove all non-word characters
        normalized = normalized.replaceAll("[\\P{L}0-9]", " ");

        // remove all single characters
        normalized = normalized.replaceAll("\\s+", " ");

        normalized = normalized.toLowerCase();

        // typical name normalizations
        normalized = normalized.replaceAll("jr", "junior");
        normalized = normalized.replaceAll("sr", "senior");


        return normalized;
    }



}
