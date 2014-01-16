package be.ugent.mmlab.europeana.enrichment.linking;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import be.ugent.mmlab.europeana.enrichment.sparql.QueryEndpoint;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static be.ugent.mmlab.europeana.enrichment.misc.Names.*;
/**
 * Created by ghaesen on 12/17/13.
 */
public class CreatorResourceLinker extends AbstractResourceLinker {

    private final Property typeNode = addModel.createProperty(TYPE.getUri());
    private final Property sameAsNode = addModel.createProperty(SAME_AS.getUri());
    private final Property todoNode = addModel.createProperty(TODO.getUri());

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
        if (!addModel.contains(creatorNode, typeNode)) {

            // add "<creator name> <type> <agent>"
            addModel.add(creatorNode, typeNode, addModel.createResource(AGENT.toString()));
            addModel.add(creatorNode, todoNode, addModel.createTypedLiteral(true));

            // TODO: add foaf name? Or first check dbPedia for preferred label?

            // query dbPedia
            List<String> nameCombinations = StringCombiner.combinations(name);
            for (String nameCombination : nameCombinations) {
                Set<String> dbPediaUris = QueryEndpoint.queryDBPediaForLabel(nameCombination);
                for (String dbPediaUri : dbPediaUris) {
                    // add "<creator name> sameAs <dbPediaUri>"
                    addModel.add(creatorNode, sameAsNode, addModel.createResource(dbPediaUri));
                }
            }

            // replace original literal with new resource
            String originalSubjectUri = originalQuad.getSubject().getURI();
            Resource originalSubject = addModel.createResource(originalSubjectUri);
            Property creatorPredicate = addModel.createProperty(CREATOR.getUri());
            addModel.add(originalSubject, creatorPredicate, creatorNode);

            substractModel.add(originalSubject, creatorPredicate, substractModel.createLiteral(name));
        }
    }



}
