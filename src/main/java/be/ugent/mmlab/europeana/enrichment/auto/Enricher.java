package be.ugent.mmlab.europeana.enrichment.auto;

import be.ugent.mmlab.europeana.enrichment.enriching.Extender;
import be.ugent.mmlab.europeana.enrichment.linking.CreatorResourceLinker;
import be.ugent.mmlab.europeana.enrichment.linking.ResourceLinker;
import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import be.ugent.mmlab.europeana.enrichment.selecting.UserInterface;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 12/9/13.
 */
public class Enricher {
    private final Set<ResourceLinker> resourceLinkers;

    public Enricher() {
        resourceLinkers = new HashSet<>();
        resourceLinkers.add(new CreatorResourceLinker("localhost", "/agents/"));
    }

    /**
     * Phase one takes an initial data set, and introduces resources for certain literals in it.
     * The resource then gets 'linked' to a number of possible entries from, e.g., dbPedia.
     * After this phase, a selection has to be made.
     *
     *  E.g.: <dc:creator>George Adams junior</dc:creator>
     * becomes <dc:creator>http://localhost/agents/George%20Adams%20junior</dc:creator>.
     *
     * This resource becomes then (in TURTLE):
     * <http://localhost/agents/George%20Adams%20junior>
     * a              <AGENT> ;
     * <http://TODO>  true ;
     * <http://www.w3.org/2002/07/owl#sameAs>
     *   <http://dbpedia.org/resource/George_Adams_(musician)> ,
     *   <http://dbpedia.org/resource/George_Adams_(football_player_and_coach)>,
     *   <http://dbpedia.org/resource/George_Adams_(optician)>
     *   ...
     *
     * The "<http://TODO>  true ;" indicates that selection still needs to be made (phase two)
     *
     * @param dataset   The data set to enrich
     */
    public void phaseOne(final Dataset dataset) {
        try {
            dataset.begin(ReadWrite.READ);
            DatasetGraph graph = dataset.asDatasetGraph();
            Iterator<Quad> qIter = graph.find();

            while (qIter.hasNext()) {
                Quad quad = qIter.next();
                for (ResourceLinker resourceLinker : resourceLinkers) {
                    resourceLinker.link(quad);
                }
            }
            dataset.end();

            dataset.begin(ReadWrite.WRITE);
            Model model = dataset.getDefaultModel();
            for (ResourceLinker resourceLinker : resourceLinkers) {
                resourceLinker.mergeResult(model);
            }
            model.write(System.out, "TURTLE");
            dataset.commit();
            dataset.end();
        } finally {
            dataset.close();
        }
    }

    public void phaseTwo(final Dataset dataset, final UserInterface userInterface) {
        // select triples that need selection
        Model model = dataset.getDefaultModel();
        Extender extender = new Extender();
        try {
            model.begin();

            CommonModelOperations modelOps = new CommonModelOperations(model);

            ResIterator subjectsWithSelection = modelOps.getTodoSubjects();
            while (subjectsWithSelection.hasNext()) {
                // TODO get ... ... <subject>, to get and display original context (subject, predicate), e.g. to know that Jos Bosmans is a creator of a proxy
                Resource subject = subjectsWithSelection.nextResource();

                // now get all sameAs objects
                Map<String, Statement> statementMap = modelOps.getSameAsObjToStmt(subject);
                // send objects to select to user interface
                String selectedUri = userInterface.makeSelection(subject.getURI(), statementMap.keySet());
                if (selectedUri == null) {
                    break;
                }

                // remove all statements except the one to preserve
                statementMap.remove(selectedUri);
                for (Statement statement : statementMap.values()) {
                    model.remove(statement);
                }
                modelOps.removeTodo(subject);
                model.commit();

                // add real enrichment
                // first get type; get "<subject> a <object>" triple
                String type = modelOps.getType(subject);
                if (type != null) {
                    Model extensionModel = extender.extend(type, subject, selectedUri);
                    if (extensionModel != null) {
                        model.add(extensionModel);
                    }
                }
            }
            model.write(System.out, "TURTLE");
        } finally {
            model.close();
        }

    }
}
