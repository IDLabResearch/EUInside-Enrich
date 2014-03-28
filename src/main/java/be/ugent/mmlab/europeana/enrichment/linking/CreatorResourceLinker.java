package be.ugent.mmlab.europeana.enrichment.linking;

import be.ugent.mmlab.europeana.enrichment.dataset.Dataset;
import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 12/17/13.
 */
public class CreatorResourceLinker extends AbstractResourceLinker {

    public CreatorResourceLinker(final String host, final String path, final Dataset dataset) {
        super(dataset, host, path);
    }

    public void link(final Model model) {
        CommonModelOperations modelOperations = new CommonModelOperations(model);
        Set<Triple> creatorTriples = modelOperations.getCreatorTriples();
        creatorTriples.stream().filter(creatorTriple -> creatorTriple.getObject().isLiteral()).forEach(creatorTriple -> {
            try {
                addCreator(creatorTriple);
            } catch (MalformedURLException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        mergeResult(model);
    }

    @Override
    public void link(final Triple input) {
        Node predicate = input.getPredicate();
        if (predicate.isURI() && predicate.getLocalName().equals("creator")) {
            Node object = input.getObject();

            // if it is a literal, convert to a (local) uri
            if (object.isLiteral()) {
                try {
                    addCreator(input);
                } catch (MalformedURLException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addCreator(final Triple originalTriple) throws URISyntaxException, MalformedURLException {

        LiteralLabel nameLiteral = originalTriple.getObject().getLiteral();
        String name = nameLiteral.getValue().toString();
        String uri = getURI(name);
        Resource creatorNode = addModel.createResource(uri);

        // check if already in model
        if (!addModelOps.hasType(creatorNode)) {

            // add "<creator name> <type> <agent>"
            addModelOps.addAgentType(creatorNode);
            addModelOps.addTodo(creatorNode);
            List<String> dbPediaUris = dataset.searchSubject(name);
            // TODO: add foaf name? Or first check dbPedia for preferred label?


            for (String dbPediaUri : dbPediaUris) {
                // add "<creator name> sameAs <dbPediaUri>"
                addModelOps.addSameAs(creatorNode, dbPediaUri);
            }

            // replace original literal with new resource
            String originalSubjectUri = originalTriple.getSubject().getURI();
            addModelOps.addCreator(originalSubjectUri, creatorNode);
            subModelOps.addCreatorToRemove(originalSubjectUri, nameLiteral);
        }
    }

}
