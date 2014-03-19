package be.ugent.mmlab.europeana.enrichment2.oneRecord;

import be.ugent.mmlab.europeana.enrichment.dataset.Dataset;
import be.ugent.mmlab.europeana.enrichment.dataset.HDTDataset;
import be.ugent.mmlab.europeana.enrichment.enriching.Extender;
import be.ugent.mmlab.europeana.enrichment.linking.CreatorResourceLinker;
import be.ugent.mmlab.europeana.enrichment.linking.ResourceLinker;
import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import com.hp.hpl.jena.rdf.model.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/24/14.
 */
public class EnrichServiceImpl implements EnrichService {

    @Override
    public PhaseOneResult phaseOne(final String record) throws IOException {
        Set<ResourceLinker> resourceLinkers = initResourceLinkers();
        Model model = ModelFactory.createDefaultModel();
        model.read(new StringReader(record), null);
        for (ResourceLinker resourceLinker : resourceLinkers) {
            resourceLinker.link(model);
        }

        long modelReference = ModelCache.getInstance().add(model);
        return getPossibleCandidates(model, modelReference);
    }

    private Set<ResourceLinker> initResourceLinkers() {
        // TODO do this in factory, later on
        final Set<ResourceLinker> resourceLinkers = new HashSet<>();
        resourceLinkers.add(new CreatorResourceLinker("localhost", "/agents/"));
        return resourceLinkers;
    }

    private PhaseOneResult getPossibleCandidates(final Model model, final long reference) {
        PhaseOneResult phaseOneResult = new PhaseOneResult(reference);
        CommonModelOperations modelOperations = new CommonModelOperations(model);
        ResIterator subjects = modelOperations.getTodoSubjects();
        while (subjects.hasNext()) {
            Resource subject = subjects.nextResource();
            String subjectStr = subject.getURI();
            List<String> possibleCandidates = modelOperations.getSameAs(subject);
            phaseOneResult.add(subjectStr, possibleCandidates);
        }
        return phaseOneResult;
    }

    @Override
    public String phaseTwo(final long reference, final Map<String, String> subjectToURI) {
        ModelCache cache = ModelCache.getInstance();
        Model model = cache.get(reference);
        if (model != null) {
            CommonModelOperations modelOperations = new CommonModelOperations(model);
            Dataset dataset = HDTDataset.getInstance();
            //Dataset dataset = new Virtuoso();
            Extender extender = new Extender(dataset);

            for (Map.Entry<String, String> subjectToSameAs : subjectToURI.entrySet()) {
                String subjectStr = subjectToSameAs.getKey();
                Resource subject = ResourceFactory.createResource(subjectStr);
                String candidateObject = subjectToSameAs.getValue();

                // remove other candidates, and __TODO__
                List<String> candidateObjectsToRemove = modelOperations.getSameAs(subject);
                candidateObjectsToRemove.remove(candidateObject);
                for (String candidateObjectToRemove : candidateObjectsToRemove) {
                    modelOperations.removeSameAs(subject, ResourceFactory.createResource(candidateObjectToRemove));
                }
                modelOperations.removeTodo(subject);

                // perform real enrichment
                String type = modelOperations.getType(subject);
                if (type != null) {
                    Model extensionModel = extender.extend(type, subject, candidateObject);
                    if (extensionModel != null) {
                        model.add(extensionModel);
                    }
                }
            }

            // update cache with new model
            cache.put(model, reference);

            return asString(model);
        } else {
            return null;
        }
    }

    public String getFromCache(final long reference) {
        ModelCache cache = ModelCache.getInstance();
        Model model = cache.get(reference);
        if (model != null) {
            return asString(model);
        } else {
            return null;
        }
    }

    private String asString(final Model model) {
        // return model as string
        StringWriter out = new StringWriter();
        model.write(out);
        return out.toString();
    }

}
