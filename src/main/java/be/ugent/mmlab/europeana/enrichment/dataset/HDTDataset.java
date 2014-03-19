package be.ugent.mmlab.europeana.enrichment.dataset;

import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import java.io.IOException;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 */
public class HDTDataset implements Dataset {
    private HDTDataset instance = null;
    private HDT hdt;

    private HDTDataset getInstance() {
        if (instance == null) {
            instance = new HDTDataset();
        }
        return instance;
    }

    private HDTDataset() {
        // TODO: parameter!
        try {
            hdt = HDTManager.mapHDT("/home/ghaesen/data/dbPedia_hdt/DBPedia-3.9-en.hdt", null);
        } catch (IOException e) {
            // leave null
        }
    }

    @Override
    public Set<String> searchSubject(final String subject) {
        if (hdt != null) {
            // first try literal search

        }
        return null;
    }
}
