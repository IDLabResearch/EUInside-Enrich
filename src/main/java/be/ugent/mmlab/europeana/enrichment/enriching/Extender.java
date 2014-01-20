package be.ugent.mmlab.europeana.enrichment.enriching;

import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/16/14.
 */
public class Extender {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final ResourceFetcher resourceFetcher = new ResourceFetcher();

    public Model extend(final String type, final Resource subject, final String uri) {
        String n3Uri = toDPPediaN3(uri);
        Model result = null;
        try {
            String body = resourceFetcher.get(n3Uri);
            if (!body.isEmpty()) {
                // read contents into model
                Model newModel = toModel(body);
                switch (type) {
                    case "AGENT":
                        result = extendAgent(subject, newModel);
                        break;
                }
            }
        } catch (IOException | URISyntaxException e) {
            logger.warn("Could not fetch data from [{}].", uri, e);
        }

        return result;
    }

    private Model extendAgent(final Resource subject, final Model newModel) {
        // TODO: check if person or corporate. Search for info accordingly
        final Model agentModel = ModelFactory.createDefaultModel();   // create model to add extra agent statements
        CommonModelOperations newModelOps = new CommonModelOperations(newModel);
        CommonModelOperations agentModelOps = new CommonModelOperations(agentModel);

        // look for begin- and end dates
        agentModelOps.addBeginDate(subject, newModelOps.getBeginDate());
        agentModelOps.addEndDate(subject, newModelOps.getEndDate());

        // TODO further enrichments

        return agentModel;
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
