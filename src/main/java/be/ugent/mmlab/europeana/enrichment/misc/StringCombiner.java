package be.ugent.mmlab.europeana.enrichment.misc;

import java.util.*;

/**
 * Helper class to calculate combinations
 */
public class StringCombiner {

    public static String combinations(final String input) {
        List<String> normalizedNames = normalizeName(input);
        StringBuilder str = new StringBuilder();

        if (!normalizedNames.isEmpty()) {
            for (String normalizedName : normalizedNames) {
                Set<String> results = calcCombination(normalizedName);
                if (!results.isEmpty()) {
                    for (String result : results) {
                        result = result.replaceAll("'", "");
                        str.append(result).append(" or ");
                    }
                    str.delete(str.length() - 4, str.length());
                }
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
        }

        return str.toString();
    }

    private static List<String> normalizeName(final String name) {
        List<String> result = new ArrayList<>();

        String normName = name.substring(name.lastIndexOf('/') + 1).replaceAll("_", " ");
        // remove everything between (square) brackets
        normName = normName.replaceAll("[\\[\\(].*?[\\]\\)]", ""); // extra '?' -> lazy matching

        // remove everything starting with small letters
        normName = normName.replaceAll(" or ", ",");
        normName = normName.replaceAll("\\s\\p{javaLowerCase}+", " ");
        normName = normName.replaceAll("^\\p{javaLowerCase}+", "");
        normName = normName.replaceAll("-", ",");

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

    public static int score (final String combinations1, final String combinations2) {
        String norm1 = combinations1.replaceAll("\\s\\p{javaLowerCase}+", "");
        String norm2 = combinations2.replaceAll("\\s\\p{javaLowerCase}+", "");
        String[] parts1 = norm1.split(" ");
        String[] parts2 = norm2.split(" ");
        Set<String> set1 = new HashSet<>(Arrays.asList(parts1));
        Set<String> set2 = new HashSet<>(Arrays.asList(parts2));

        set1.retainAll(set2);

        int score = set1.size();

        if (score != set2.size()) {
            score--;
        }
        return score;
    }

    private static Set<String> calcCombination (final String name) {
        String[] parts = name.split("\\s+");
        return calcCombination(parts);
    }

    private static Set<String> calcCombination (final String[] parts) {
        Set<String> result = new HashSet<>();
        if (parts.length == 1) {
            result.add(parts[0]);
        } else {
            for (int firstIndex = 0; firstIndex < parts.length - 1; firstIndex++) {
                for (int secondIndex = firstIndex + 1; secondIndex < parts.length; secondIndex++) {
                    result.add(parts[firstIndex] + " and " + parts[secondIndex]);
                }
            }
        }
        return result;
    }
}
