package be.ugent.mmlab.europeana.enrichment2.oneRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/27/14.
 */
public class PhaseOneResult {
    private final long reference;
    private final Map<String, List<String>> objectToPossibleURIs = new HashMap<>();

    public PhaseOneResult(final long reference) {
        this.reference = reference;
    }

    public void add(final String subject, final List<String> possibleURIs) {
        objectToPossibleURIs.put(subject, possibleURIs);
    }

    public long getReference() {
        return reference;
    }

    public Map<String, List<String>> getObjectToPossibleURIs() {
        return objectToPossibleURIs;
    }
}
