package be.ugent.mmlab.europeana.enrichment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class to calculate combinations
 */
public class StringCombiner {
    /**
     * Takes a String as input, splits it up according to white spaces (~ in words), and returns the combinations of
     * these words.
     * E.g.: "I am Yoda" returns: "I am Yoda", "I Yoda am", "Yoda I am", "Yoda am I", "Am I Yoda", "Am Yoda I", "I am", "I Yoda", ...
     * @param input The input String
     * @return  A list of combinations of this String.
     */
    public static List<String> combinations(final String input) {
        List<String> namePermutations = new ArrayList<>();
        String normalizedName = input.trim().replaceAll("[\\s\\.]+", "_");
        //namePermutations.add(normalizedName);

        String[] nameParts = normalizedName.split("_");
        //permutation(new String[0], nameParts, namePermutations);

        // create index string
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < nameParts.length; i++) {
            str.append(i);
        }

        List<String> indexStrings = new ArrayList<>();
        permutation("", str.toString(), indexStrings);
        Collections.sort(indexStrings, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() == o2.length()) {
                    return o1.compareTo(o2);
                } else {
                    return o2.length() - o1.length();
                }
            }
        });

        int maxLength = nameParts.length;
        for (String indexString : indexStrings) {
            if (indexString.length() < maxLength - 1) {
                break;
            }
            namePermutations.add(mergeParts(indexString, nameParts));
        }

        return namePermutations;
    }


    private static void permutation(String prefix, String str, final List<String> indexStrings) {
        int n = str.length();
        if (n == 0) {
            if (prefix.length() > 0 && !indexStrings.contains(prefix)) {
                indexStrings.add(prefix);
                permutation("", prefix.substring(0, prefix.length() - 1), indexStrings);
            }
        }
        else {
            for (int i = 0; i < n; i++)
                permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n), indexStrings);
        }
    }

    private static String mergeParts(final String indexString, final String[] nameParts) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < indexString.length(); i++) {
            int index = Integer.parseInt(indexString.substring(i, i + 1));
            str.append(nameParts[index]).append('_');
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }
}
