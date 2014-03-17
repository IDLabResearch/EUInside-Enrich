package be.ugent.mmlab.europeana.enrichment.enriching;

import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import be.ugent.mmlab.europeana.enrichment.model.RdfNodeFactory;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.http.client.fluent.Request;
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

    public Model extend(final String type, final Resource subject, final String uri) {
        String n3Uri = toDPPediaN3(uri);
        Model result = null;
        try {
            String body = Request.Get(n3Uri).execute().returnContent().asString();
            if (!body.isEmpty()) {
                // read contents into model
                Model newModel = toModel(body);

                // if page redirects, get that info in stead
                // e.g.: dbpedia:George_B._Adams @dbpedia-owl:wikiPageRedirects dbpedia:George_Burton_Adams;
                String redirectURI = checkForRedirect(newModel);
                if (redirectURI != null && !redirectURI.equals(uri)) {
                    return extend(type, subject, redirectURI);
                }

                switch (type) {
                    case "http://www.europeana.eu/schemas/edm/Agent":
                        result = extendAgent(subject, newModel);
                        break;
                }
            }
        } catch (IOException e) {
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
        agentModelOps.addPrefLabel(subject, newModelOps.getRdfsLabel());
        agentModelOps.addSkosNote(subject, newModelOps.getAbstract());

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

    private String checkForRedirect(final Model model) {
        NodeIterator nodeIter = model.listObjectsOfProperty(RdfNodeFactory.getInstance().getDbpRedirectProperty());
        if (nodeIter.hasNext()) {
            RDFNode objectNode = nodeIter.next();
            return objectNode.asResource().getURI();
        } else {
            return null;
        }
    }
}
