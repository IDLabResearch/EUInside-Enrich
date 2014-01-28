package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import org.junit.Test;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/27/14.
 */
public class TestRegex {

    @Test
    public void testStringCombiner() {
        String[] input = {
                "A B C D",
                "einstein",
                "Einstein, Zweisteiner",
                "Alessandro Tognozzi (fonditore)",
                "Andreas Albrecht [attr.]",
                "Benedetto Bregans (lens), Francesco Spighi, Gaspero Mazzeranghi (mount)",
                "Multimedia Laboratory of the Museo Galileo - Institute and Museum of the History of Science",
                "Benedetto Bregans (lente), Francesco Spighi, Gaspero Mazzeranghi, (montatura)",
                "Benjamin Thompson conte di Rumford",
                "Michel-Ferdinand d'Albert d'Ailly Duc de Chaulnes",
                "Charles-Philippe Robin",
                "Ditta G. & S. Merz",
                "Ditta Youngs & Son",
                "Egnazio Danti or Giovanni Battista Giusti [attr.]"
        };
        for (String s : input) {
            System.out.println(s);
            String normalized = StringCombiner.combinations(s);
            System.out.println(" --> " + normalized);
        }
    }

    /*private List<String> normalizeName(final String name) {
        List<String> result = new ArrayList<>();

        // remove everything between (square) brackets
        String normName = name.replaceAll("[\\[\\(].*?[\\]\\)]", ""); // extra '?' -> lazy matching

        // remove everything starting with small letters
        normName = normName.replaceAll("\\s\\p{javaLowerCase}+", "");
        normName = normName.replaceAll("'", " ");

        // split
        String[] parts = normName.split("[,&]");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.add(part.trim());
            }
        }

        //result.add(normName);

        return result;
    } */
}
