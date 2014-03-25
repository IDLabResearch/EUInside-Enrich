package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.config.Config;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public class DatasetFactory {

    public static Dataset get() {
        Config config = Config.getInstance();
        switch (config.getDataset()) {
            case "sparql":
                return new Virtuoso(config.getSparqlEndpoint());
            case "hdt":
                return HDTDataset.getInstance(config.getHDTFile());
            default:
                return null;
        }
    }
}
