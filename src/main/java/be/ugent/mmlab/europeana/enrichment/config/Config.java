package be.ugent.mmlab.europeana.enrichment.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public class Config {
    private static Config instance;

    private final Properties properties;
    private final Logger logger = LogManager.getLogger(getClass());

    private Config(final String propertyFile) {
        properties = new Properties();
        properties.setProperty("tmp.dir", System.getProperty("java.io.tmpdir"));
        properties.setProperty("creator.uri.host", "localhost");
        properties.setProperty("creator.uri.path", "/agents/");
        properties.setProperty("dataset", "sparql");
        properties.setProperty("dataset.sparql.endpoint", "http://dbpedia.org/sparql");
        properties.setProperty("dataset.hdt.file", "DBPedia-3.9-en.hdt");
        properties.setProperty("index.enabled", "false");
        properties.setProperty("index.label.dir", "labelIndex");
        properties.setProperty("index.search.maxresults", "100");
        try (FileReader reader = new FileReader(propertyFile)) {
            properties.load(reader);
        } catch (IOException e) {
            logger.warn("!!!!");
            logger.warn("Cannot load configuration file {}. Using default configuration!!");
            logger.warn("Error message: {}", e.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("*** Using following properties: ");
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                logger.debug("    {} = {}", objectObjectEntry.getKey(), objectObjectEntry.getValue());
            }
        }
    }

    public static void init(final String propertyFile) {
        if (instance == null) {
            instance = new Config(propertyFile);
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            init("_______________");
        }
        return instance;
    }

    public String getTmpDir() {
        return properties.getProperty("tmp.dir");
    }

    public String getCreatorHost() {
        return properties.getProperty("creator.uri.host");
    }

    public String getCreatorPath() {
        return properties.getProperty("creator.uri.path");
    }

    public String getDataset() {
        return properties.getProperty("dataset");
    }

    public String getSparqlEndpoint() {
        return properties.getProperty("dataset.sparql.endpoint");
    }

    public String getHDTFile() {
        return properties.getProperty("dataset.hdt.file");
    }

    public boolean indexEnabled() {
        return Boolean.parseBoolean(properties.getProperty("index.enabled"));
    }

    public String getIndexDir() {
        return properties.getProperty("index.label.dir");
    }

    public int getMaxSearchResults() {
        return Integer.parseInt(properties.getProperty("index.search.maxresults"));
    }

}
