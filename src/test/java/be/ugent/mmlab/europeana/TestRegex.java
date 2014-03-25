package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.misc.Normalizer;
import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import org.junit.Test;

import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/27/14.
 */
public class TestRegex {

    @Test
    public void testStringCombiner() {
        String[] input = {
                "A B C D",
                "Einstein",
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
            List<String> normalizedAndSplit = StringCombiner.normalizeAndSplit(s);
            for (String split : normalizedAndSplit) {
                System.out.println("     " + split);
            }
            String combinated = StringCombiner.combinations(s);
            System.out.println(" --> " + combinated);
            System.out.println();
        }
    }

    @Test
    public void testNormalizer() {
        String[] input = {
                "small letter input",
                "L'état, c'est Moi",
                "Aaáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ",
                "Ibr\u00E2him 'Ibn Sa\u00EEd as Sahl\u00EC",
                "A$_|*@B/?",
                "alkotó: Francisco de Goya y Lucientes, spanyol (Fuendetodos [Zaragoza] 1746 – 1828 Bordeaux)",
                "Fotós/Készítő / Photographer/Creator: Martsa Alajos  (1910-1979) ",
                "Zuber &amp; cie, Jean",
                "A B C D",
                "Einstein",
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
                "Egnazio Danti or Giovanni Battista Giusti [attr.]",
                "Henry van de Velde"
        };
        for (String s : input) {
            System.out.println(s);
            String normalizedForIndexing = Normalizer.normalizeForIndexing(s);
            String normalizedForQuerying = Normalizer.normalizeForQuerying(s);
            System.out.println(" --> " + normalizedForIndexing);
            System.out.println(" --> " + normalizedForQuerying);
            System.out.println();
        }
    }

    @Test
    public void testNormalizeALotOfStrings() {
        int nrOfCopies = 50000;

        System.out.println("Building strings...");

        String[] input = {
                "small letter input",
                "L'état, c'est Moi",
                "Aaáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ",
                "Ibr\u00E2him 'Ibn Sa\u00EEd as Sahl\u00EC",
                "A$_|*@B/?",
                "alkotó: Francisco de Goya y Lucientes, spanyol (Fuendetodos [Zaragoza] 1746 – 1828 Bordeaux)",
                "Fotós/Készítő / Photographer/Creator: Martsa Alajos  (1910-1979) ",
                "Zuber &amp; cie, Jean",
                "A B C D",
                "Einstein",
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
                "Egnazio Danti or Giovanni Battista Giusti [attr.]",
                "Henry van de Velde"
        };

        int length = nrOfCopies * input.length;
        String[] toNormalize = new String[length];

        for (int i = 0; i < nrOfCopies; i++) {
            System.arraycopy(input, 0, toNormalize, i * input.length, input.length);
        }

        System.out.println("Normalizing...");
        long start = System.currentTimeMillis();

        long totalLength = 0;
        for (int i = 0; i < length; i++) {
            totalLength += Normalizer.normalizeForIndexing(toNormalize[i]).length();
        }

        long stop = System.currentTimeMillis();

        System.out.println("***");
        System.out.println("normalized " + length + " strings in " + (stop - start) + " ms!");
        System.out.println("total characters = " + totalLength);
    }
}
