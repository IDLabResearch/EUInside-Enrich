package be.ugent.mmlab.europeana.enrichment.linking;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import be.ugent.mmlab.europeana.enrichment.sparql.QueryEndpoint;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

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
            List<String> nameCombinations = StringCombiner.combinations(name);

            for (int i = 0; i < nameCombinations.size(); i++) {
                String nameCombination = nameCombinations.get(i);
                Set<String> dbPediaUris = QueryEndpoint.queryDBPediaForLabel(nameCombination);
                for (String dbPediaUri : dbPediaUris) {
                    // add "<creator name> sameAs <dbPediaUri>"
                    addModelOps.addSameAs(creatorNode, dbPediaUri);
                }
                if (i == 0 && dbPediaUris.size() > 0) {     // we found literal match: stop iterating.
                    break;
                }
            }

            // replace original literal with new resource
            String originalSubjectUri = originalQuad.getSubject().getURI();
            addModelOps.addCreator(originalSubjectUri, creatorNode);
            subModelOps.addCreatorToRemove(originalSubjectUri, name);
        }
    }

}
