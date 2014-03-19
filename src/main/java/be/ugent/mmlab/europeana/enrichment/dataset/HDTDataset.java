package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.misc.CountArray;
import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rdfhdt.hdt.dictionary.DictionarySection;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 */
public class HDTDataset implements Dataset {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private static HDTDataset instance = null;
    private HDT hdt;

    public static HDTDataset getInstance() {
        if (instance == null) {
            instance = new HDTDataset();
        }
        return instance;
    }

    private HDTDataset() {
        // TODO: parameter!
        String hdtFile = "/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt";
        logger.debug("Initializing HDT (DBPedia)");
        try {
            hdt = HDTManager.mapHDT(hdtFile, null);
        } catch (IOException e) {
            logger.error("Could not load HDT file {}", e);
        }
    }

    @Override
    public List<String> searchSubject(final String subject) {
        logger.debug("Searching for subjects containing [{}] in HDT", subject);
        List<String> subjectParts = StringCombiner.normalizeAndSplit(subject);
        for (int i = 0; i < subjectParts.size(); i++) {
            subjectParts.set(i, subjectParts.get(i).toLowerCase());
        }
        if (hdt != null) {
            // first try literal search
            CountArray<String> scoredEntries = iterateOn(hdt.getDictionary().getSubjects(), subjectParts);
            CountArray<String> scoredEntries2 = iterateOn(hdt.getDictionary().getShared(), subjectParts);
            scoredEntries.addAll(scoredEntries2);
            return scoredEntries.getSortedByCount(scoredEntries.getHighestCount());
        }
        return Collections.emptyList();
    }

    private CountArray<String> iterateOn(final DictionarySection dictionarySection, final List<String> subject) {
        CountArray<String> scores = new CountArray<>();
        Iterator<? extends CharSequence> entryIter = dictionarySection.getSortedEntries();

        int hiscore = Math.min(2, subject.size());

        while (entryIter.hasNext()) {
            String entry = entryIter.next().toString();

            // now do filtering of the string
            if (entry.contains("resource") && !entry.contains("File:") && !entry.contains("Category:")) {
                int score = scoreSubject(entry, subject);
                if (score >= hiscore) {
                    scores.set(entry, score);
                    hiscore = score;
                }
            }
        }
        return scores;
    }

    private int scoreSubject(final String entry, final List<String> subject) {
        String lowEntry = entry.toLowerCase();
        int size = subject.size();
        int score = 0;
        int lastIndex = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            int index = lowEntry.indexOf(subject.get(i));
            if (index > 0) {
                score += index > lastIndex ? 2 : 1;
                lastIndex = index;
            }
        }
        return score;
    }
}
