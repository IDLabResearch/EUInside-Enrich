package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.misc.CountArray;
import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rdfhdt.hdt.dictionary.DictionarySection;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 */
public class HDTDataset extends AbstractDataset {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private static HDTDataset instance = null;
    private HDT hdt;

    public static HDTDataset getInstance(final String hdtFile) {
        if (instance == null) {
            instance = new HDTDataset(hdtFile);
        }
        return instance;
    }

    private HDTDataset(final String hdtFile) {
        logger.debug("Initializing HDT (DBPedia)");
        try {
            hdt = HDTManager.mapIndexedHDT(hdtFile, null);
        } catch (IOException e) {
            logger.error("Could not load HDT file {}", e);
        }
    }

    @Override
    public List<String> searchSubject(final String subject) {
        List<String> subjects = super.searchSubject(subject);
        if (subjects.isEmpty()) {
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
                subjects = scoredEntries.getSortedByCount(scoredEntries.getHighestCount());
            }
        }
        return subjects;
    }

    @Override
    public Model getModelFor(String subjectResource) {
        logger.debug("Generating model for [{}]", subjectResource);

        try {
            StringBuilder str = new StringBuilder();
            IteratorTripleString tripleIter = hdt.search(subjectResource, "", "");
            while (tripleIter.hasNext()) {
                TripleString tripleString = tripleIter.next();
                str.append(tripleString.asNtriple());
            }
            StringReader in = new StringReader(str.toString());

            Model model = ModelFactory.createDefaultModel();
            model.read(in, "http://dbPedia.org", "N-TRIPLE");
            return model;
        } catch (NotFoundException | IOException e) {
            logger.error("Subject {} not found!", subjectResource, e);
            return null;
        }
    }

    public Collection<String> searchSubject(final String predicate, final String object) {
        List<String> subjects = new ArrayList<>();
        logger.debug("Searching for subjects with predicate [{}] and object [{}]", predicate, object);
        try {
            IteratorTripleString tripleIter = hdt.search("", predicate, object);
            if (tripleIter.estimatedNumResults() < 10000) {    // TODO make parameter
                while (tripleIter.hasNext()) {
                    TripleString tripleString = tripleIter.next();
                    subjects.add(tripleString.getSubject().toString());
                }
            } else {
                logger.debug("Too many subject for [{}]", object);
            }
        } catch (NotFoundException e) {
            logger.error("No subjects found for predicate [{}] and object [{}]!", predicate, object, e);
        }
        return subjects;
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
