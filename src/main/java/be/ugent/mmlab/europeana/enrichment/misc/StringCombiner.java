package be.ugent.mmlab.europeana.enrichment.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class to calculate combinations
 */
public class StringCombiner {

    public static String combinations(final String input) {
        //List<String> result = new ArrayList<>();
        List<String> normalizedNames = normalizeName(input);
        StringBuilder str = new StringBuilder();

        if (!normalizedNames.isEmpty()) {
            for (String normalizedName : normalizedNames) {
                Set<String> results = calcCombination(normalizedName);
                if (!results.isEmpty()) {
                    for (String result : results) {
                        result = result.replaceAll("_'", " and ");
                        result = result.replaceAll("^'", "");
                        str.append(result).append(" or ");
                    }
                    str.delete(str.length() - 4, str.length());
                }
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
        }

        // some final normalization
        return str.toString();
    }


    private static List<String> normalizeName(final String name) {
        List<String> result = new ArrayList<>();

        // remove everything between (square) brackets
        String normName = name.replaceAll("[\\[\\(].*?[\\]\\)]", ""); // extra '?' -> lazy matching

        // remove everything starting with small letters
        normName = normName.replaceAll(" or ", ",");
        normName = normName.replaceAll("\\s\\p{javaLowerCase}+", " ");
        normName = normName.replaceAll("^\\p{javaLowerCase}+", "");
        normName = normName.replaceAll(" - ", " , ");

        //normName = normName.replaceAll("'", " and ");

        //normName = normName.replaceAll("[,&]", " or ");
        // split
        String[] parts = normName.split("[,&]");
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                result.add(part);
            }
        }
        return result;
    }

    private static Set<String> calcCombination (final String name) {
        String[] parts = name.split("\\s+");
        final Set<String> result = new HashSet<>();
        calcCombination(result, parts);
        return result;
    }

    private static void calcCombination (final Set<String> result, final String[] parts) {
        result.add(join(parts));
        if (parts.length > 2) {
            int nrParts = parts.length;

            for (int i = 0; i < nrParts; i++) {
                calcCombination(result, removeAtIndex(parts, i));
            }

        }
    }

    private static String join(final String[] parts) {
        StringBuilder str = new StringBuilder();
        for (String part : parts) {
            str.append(part).append('_');
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    private static String[] removeAtIndex(final String parts[], int index) {
        int newSize = parts.length - 1;
        String[] newParts = new String[newSize];
        int i;
        for (i = 0; i < index; i++) {
            newParts[i] = parts[i];
        }
        i++;
        for (; i < parts.length; i++) {
            newParts[i - 1] = parts[i];
        }
        return newParts;
    }
}
