package be.ugent.mmlab.europeana.enrichment.linking;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import be.ugent.mmlab.europeana.enrichment.sparql.QueryEndpoint;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 12/17/13.
 */
public class CreatorResourceLinker extends AbstractResourceLinker {

    public CreatorResourceLinker(final String host, final String path) {
        super(host, path);
    }

    @Override
    public void link(Quad input) {
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

    private void addCreator(final Quad originalQuad) throws URISyntaxException, MalformedURLException {
        String name = originalQuad.getObject().getLiteral().toString();
        String uri = getURI(name);
        Resource creatorNode = addModel.createResource(uri);

        // check if already in model
        if (!addModelOps.hasType(creatorNode)) {

            // add "<creator name> <type> <agent>"
            addModelOps.addAgentType(creatorNode);
            addModelOps.addTodo(creatorNode);

            // TODO: add foaf name? Or first check dbPedia for preferred label?

            // query dbPedia
            final String nameCombinations = StringCombiner.combinations(name);

            // add uri's in a kind of ranked order: first the more relevant ones.
            Set<String> dbPediaUris = QueryEndpoint.queryDBPediaForLabel(nameCombinations);
            List<String> orderedUris = new ArrayList<>();
            for (String dbPediaUri : dbPediaUris) {
                // add "<creator name> sameAs <dbPediaUri>"
                orderedUris.add(dbPediaUri);
            }
            Collections.sort(orderedUris, new Comparator<String>() {
                @Override
                public int compare(String uri1, String uri2) {

                    return StringCombiner.score(nameCombinations, StringCombiner.combinations(uri2))
                            - StringCombiner.score(nameCombinations, StringCombiner.combinations(uri1));
                }
            });

            for (String orderedUri : orderedUris) {
                // add "<creator name> sameAs <dbPediaUri>"
                addModelOps.addSameAs(creatorNode, orderedUri);
            }

            // replace original literal with new resource
            String originalSubjectUri = originalQuad.getSubject().getURI();
            addModelOps.addCreator(originalSubjectUri, creatorNode);
            subModelOps.addCreatorToRemove(originalSubjectUri, name);
        }
    }

}
