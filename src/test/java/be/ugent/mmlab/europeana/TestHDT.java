package be.ugent.mmlab.europeana;

import org.junit.Test;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;

import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/12/14.
 */
public class TestHDT {

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
}
