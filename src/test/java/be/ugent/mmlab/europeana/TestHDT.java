package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.dataset.Dataset;
import be.ugent.mmlab.europeana.enrichment.dataset.HDTDataset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/12/14.
 */
public class TestHDT {
    private final Logger logger = LogManager.getLogger();
    private long start, load, search, end = 0;

    @Test
    public void test() throws IOException, ParserException {

        HDT hdt = HDTManager.generateHDT(
                "/home/ghaesen/data/europeana/edm/all_merged_corrected.rdf.gz",
                "http://dbpedia.org",
                RDFNotation.RDFXML,
                new HDTSpecification(),
                null);

        System.out.println("size = " + hdt.size());
        hdt.saveToHDT("/home/ghaesen/data/europeana/edm/all.hdt", null);


    }

    @Test
    public void dbPediaIterateAll() throws IOException {
        // this takes, like, forever
        logger.debug("*** Loading mapped and indexed hdt file...");
        start = System.currentTimeMillis();
        HDT hdt = HDTManager.mapHDT("/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt", /*new ProgressOut()*/ null);
        load = System.currentTimeMillis();
        try {
            logger.debug("*** Searching HDT file for labels 'Paul Hankar'");
            IteratorTripleString result = hdt.search("", "", "");
            search = System.currentTimeMillis();
            while (result.hasNext()) {
                TripleString triple = result.next();
                if (triple.getSubject().toString().endsWith("Hankar")) {
                    logger.debug(triple);
                }
            }
            end = System.currentTimeMillis();
            printTimings();
        } catch (NotFoundException e) {
            logger.warn("No triples found!");
        }
    }

    @Test
    public void dbPediaSearchIdentical() throws IOException {
        // this is blazing fast!
        logger.debug("*** Loading mapped and indexed hdt file...");
        start = System.currentTimeMillis();
        HDT hdt = HDTManager.mapHDT("/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt", /*new ProgressOut()*/ null);
        load = System.currentTimeMillis();
        try {
            logger.debug("*** Searching HDT file for labels 'Paul Hankar'");
            IteratorTripleString result = hdt.search("http://dbpedia.org/resource/Paul_Hankar", "", "");
            search = System.currentTimeMillis();
            while (result.hasNext()) {
                TripleString triple = result.next();
                logger.debug(triple);
            }
            end = System.currentTimeMillis();
            printTimings();
        } catch (NotFoundException e) {
            logger.warn("No triples found!");
        }
    }

    @Test
    public void dbPediaIndexedWildcard() throws IOException {
        // this goes out of memory
        logger.debug("*** Loading mapped and indexed hdt file...");
        start = System.currentTimeMillis();
        HDT hdt = HDTManager.mapIndexedHDT("/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt", /*new ProgressOut()*/ null);
        load = System.currentTimeMillis();
        try {
            logger.debug("*** Searching HDT file for labels 'Paul Hankar'");
            IteratorTripleString result = hdt.search("*Hankar", "", "");
            search = System.currentTimeMillis();
            while (result.hasNext()) {
                TripleString triple = result.next();
                logger.debug(triple);
            }
            end = System.currentTimeMillis();
            printTimings();
        } catch (NotFoundException e) {
           logger.warn("No triples found!");
        }
    }

    @Test
    public void dbPediaDictionarySubjects() throws IOException {
        // this doesn't work? Yes it does, and fast! But you need to look also in the SHARED dictionary! (see hdt code)
        logger.debug("loading mapped hdt file...");
        start = System.currentTimeMillis();
        HDT hdt = HDTManager.mapHDT("/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt", null);
        load = System.currentTimeMillis();

        Iterator<? extends CharSequence> subjIter = hdt.getDictionary().getSubjects().getSortedEntries();
        search = System.currentTimeMillis();
        while (subjIter.hasNext()) {
            String subject = subjIter.next().toString();
            if (subject.contains("Paul_Hankar")) {
                System.out.println(subject);
            }
        }
        Iterator<? extends CharSequence> sharedIter = hdt.getDictionary().getShared().getSortedEntries();
        search = System.currentTimeMillis();
        while (sharedIter.hasNext()) {
            String subject = sharedIter.next().toString();
            if (subject.contains("Paul_Hankar")) {
                System.out.println(subject);
            }
        }

        end = System.currentTimeMillis();
        printTimings();
    }

    @Test
    public void testHDTDataset() {
        searchAndPrint("Paul Hankar");
        searchAndPrint("George Adams Junior");
        searchAndPrint("Michel-Ferdinand d'Albert d'Ailly Duc de Chaulnes");
        searchAndPrint("Spighi");
    }

    private void searchAndPrint(final String subject) {
        System.out.println("==== " + subject + " ====");
        Dataset dataset = HDTDataset.getInstance();
        List<String> subjects = dataset.searchSubject(subject);
        for (String s : subjects) {
            System.out.println(" --> " + s);
        }
        System.out.println();
    }

    private void printTimings () {
        logger.debug(" ==== TIMINGS ====");
        logger.debug("total:  {}", end - start);
        logger.debug("load:   {}", load - start);
        logger.debug("search: {}", search - load);
        logger.debug("iter:   {}", end - search);
    }
}
