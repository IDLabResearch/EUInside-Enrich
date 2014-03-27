package be.ugent.mmlab.europeana.enrichment.enriching;

import be.ugent.mmlab.europeana.enrichment.config.Config;
import be.ugent.mmlab.europeana.enrichment.dataset.Dataset;
import be.ugent.mmlab.europeana.enrichment.misc.CountArray;
import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import be.ugent.mmlab.europeana.enrichment.model.RdfNodeFactory;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/16/14.
 */
public class Extender {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final Dataset dataset;
    private final RdfNodeFactory nodeFactory = RdfNodeFactory.getInstance();

    public Extender(Dataset dataset) {
        this.dataset = dataset;
    }

    public Model extend(final String type, final Resource subject, final String uri) {
        Model result = null;
        Model newModel = dataset.getModelFor(uri);
        if (newModel != null) {
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
        agentModelOps.addRelated(subject, getRelated(newModelOps.getSubjects()));


        return agentModel;
    }

    private List<String> getRelated(final List<Resource> subjects) {
        CountArray<String> relatedSubjects = new CountArray<>();
        for (Resource subject : subjects) {
            String uri = subject.getURI();
            if (!uri.endsWith("unknown") && !uri.endsWith("missing") && !uri.endsWith("births") && !uri.endsWith("deaths")) {

                // the subject becomes object of the triples ti find.
                Collection<String> rSubjects = dataset.searchSubject(nodeFactory.getDctermsSubject().getURI(), uri);
                relatedSubjects.addAll(rSubjects);
            }
        }

        return relatedSubjects.getSortedByCountMax(Config.getInstance().getMaxSearchResults());
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
