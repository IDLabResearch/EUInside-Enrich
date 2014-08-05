package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public class LabelIndexFactory {
    private static Logger logger = LogManager.getLogger();

    public static LabelIndex get() {
        Config config = Config.getInstance();
        if (config.indexEnabled()) {
            try {
                return LabelIndex.getInstance(Version.LUCENE_48, config.getIndexDir(), config.getMaxSearchResults());
            } catch (IOException e) {
                logger.warn("Could not create index!", e);
            }
        }
        return null;
    }
}
