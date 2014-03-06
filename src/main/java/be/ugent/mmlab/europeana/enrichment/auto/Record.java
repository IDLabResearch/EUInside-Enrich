package be.ugent.mmlab.europeana.enrichment.auto;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class Record {
    private final String reference;
    private final String record;

    public Record(String reference, String record) {
        this.reference = reference;
        this.record = record;
    }

    public String getReference() {
        return reference;
    }

    public String getRecord() {
        return record;
    }
}
