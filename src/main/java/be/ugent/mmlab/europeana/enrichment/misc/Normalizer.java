package be.ugent.mmlab.europeana.enrichment.misc;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/24/14.
 */
public class Normalizer {

    /**
     * Normalizes (DBPedia) labels to strings suited for indexing in Lucene. It is tuned for names.
     * @param input    the label to index
     * @return         the normalized label
     */
    public static String normalizeForIndexing(final String input) {
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


        return normalized.trim();
    }


    /**
     * Normalizes names found in Europeana records to strings suited for querying on an index created with the
     * <code>normalizeForInexing</code> method.
     * @param input    The name to normalize
     * @return         The normalized name
     */
    public static String normalizeForQuerying(final String input) {
        String normalized = input;
        // remove everything before ':'
        int colonIndex = input.indexOf(':');
        if (colonIndex > 0) {
            normalized = normalized.substring(colonIndex + 1);
        }

        // TODO: strip html tags?

        // normalize as previous method
        normalized = normalizeForIndexing(normalized);

        // and the perform extra filtering
        //normalized.replaceAll("");
        return normalized;
    }

}
