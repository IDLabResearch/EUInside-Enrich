package be.ugent.mmlab.europeana.enrichment.enriching;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/16/14.
 */
public class Extender {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final ResourceFetcher resourceFetcher = new ResourceFetcher();

    public void extend(final String type, final String uri) {
        String n3Uri = toDPPediaN3(uri);
        try {
            String body = resourceFetcher.get(uri);

            if (!body.isEmpty()) {
                // read contents into model
                Model newModel = toModel(body);
                switch (type) {
                    case "AGENT":
                        extendAgent(newModel);
                        break;
                }
            }
        } catch (IOException e) {
            logger.warn("Could not fetch data from [{}].", uri, e);
        }


    }

    private void extendAgent(final Model newModel) {
        int i = 0;
    }

    private String toDPPediaN3(final String uri) {
        String newUri = uri;
        if (uri.contains("dbpedia.org/resource")) {
            newUri = newUri.replace("resource", "data") + ".n3";
        }
        return newUri;
    }

    private Model toModel(final String n3) throws IOException {
        Model newModel = ModelFactory.createDefaultModel();
        try (InputStream in = new ByteArrayInputStream(n3.getBytes("utf-8"))) {
            newModel.read(in, null, "N3");
        }
        return newModel;
    }
}
