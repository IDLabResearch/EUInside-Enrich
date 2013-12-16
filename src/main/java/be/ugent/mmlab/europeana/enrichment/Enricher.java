package be.ugent.mmlab.europeana.enrichment;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by ghaesen on 12/9/13.
 */
public class Enricher {

    //private Set<String> processed = new HashSet<>();    // list of already enriched objects
    private Map<String, String> nameToResource = new HashMap<>();   // mapping (creator ) name to agent resource URI

    private final Node typeNode = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final Node agentNode = NodeFactory.createURI("http://www.europeana.eu/schemas/edm/Agent");
    private final Node foafNameNode = NodeFactory.createURI("http://xmlns.com/foaf/0.1/name");
    private final Node sameAsNode = NodeFactory.createURI("http://www.w3.org/2002/07/owl#sameAs");

    // on triple level
    public void enrich(final Dataset dataset) {
        try {
            Set<Quad> quadsToAdd = new HashSet<>();
            Set<Quad> quadsToDelete = new HashSet<>();

            dataset.begin(ReadWrite.READ);
            DatasetGraph graph = dataset.asDatasetGraph();
            Iterator<Quad> qIter = graph.find();

            while (qIter.hasNext()) {
                Quad quad = qIter.next();
                Node predicate = quad.getPredicate();
                if (predicate.isURI() && predicate.getLocalName().equals("creator")) {
                    Node object = quad.getObject();

                    // if it is a literal, convert to a (local) uri
                    if (object.isLiteral()) {
                        try {
                            addCreator(quad, quadsToAdd, quadsToDelete);
                        } catch (MalformedURLException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            dataset.end();

            dataset.begin(ReadWrite.WRITE);
            graph = dataset.asDatasetGraph();
            for (Quad quad : quadsToAdd) {
                graph.add(quad);
            }
            for (Quad quad : quadsToDelete) {
                graph.delete(quad);
            }
            dataset.commit();
            dataset.end();

            dataset.begin(ReadWrite.READ);
            dataset.getDefaultModel().write(System.out, "TURTLE");
            dataset.end();
        } finally {
            dataset.close();
        }
    }

    private void addCreator(final Quad originalQuad, final Set<Quad> quadsToAdd, final Set<Quad> quadsToDelete) throws URISyntaxException, MalformedURLException {
        String name = originalQuad.getObject().getLiteral().toString();
        Node creatorNode;
        if (!nameToResource.containsKey(name)) {
            String url = (new URI("http", "localhost", "/persons/" + name, null)).toURL().toString();   // TODO: configure domain, url
            nameToResource.put(name, url);
            creatorNode = NodeFactory.createURI(url);
            Quad typeQuad = new Quad(originalQuad.getGraph(), creatorNode, typeNode, agentNode); // type agent
            Quad nameQuad = new Quad(originalQuad.getGraph(), creatorNode, foafNameNode, NodeFactory.createLiteral(name));

            quadsToAdd.add(typeQuad);
            quadsToAdd.add(nameQuad);

            // query dbPedia
            String dbPediaNode = queryPerson(name);
            if (!dbPediaNode.isEmpty()) {
                Quad sameAsQuad = new Quad(originalQuad.getGraph(), creatorNode, sameAsNode, NodeFactory.createURI(dbPediaNode));
                quadsToAdd.add(sameAsQuad);
                addCreatorData(dbPediaNode);
            }




        } else {
            String url = nameToResource.get(name);
            creatorNode = NodeFactory.createURI(url);
        }

        // "update" old original quad
        Quad modifiedQuad = new Quad(originalQuad.getGraph(), originalQuad.getSubject(), originalQuad.getPredicate(), creatorNode);
        quadsToAdd.add(modifiedQuad);
        quadsToDelete.add(originalQuad);
    }

    private String queryPerson(final String name) {
        List<String> namePermutations = StringCombiner.combinations(name);
        List<QuerySolution> solutions = new ArrayList<>();
        for (String subName : namePermutations) {

            String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                    "PREFIX dcterms: <http://purl.org/dc/terms/> \n" +
                    //"PREFIX bif: <http://www.openlinksw.com/schema/sparql/extensions#> \n" +
                    "SELECT DISTINCT ?s ?label WHERE { \n" +
                    "            ?s rdfs:label ?label . \n" +
                    "            FILTER (lang(?label) = 'en'). \n" +
                    "            ?label <bif:contains> \"" + subName + "\" . \n" +
                    "            ?s dcterms:subject ?sub \n" +
                    "}";
            solutions.addAll(queryDBPedia(query));

        }
        System.out.println("\nResults for " + name + ":\n");
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("  " + i + ". " + solutions.get(i).getLiteral("label"));
        }
        System.out.println("\nMake your choice:\n");

        int choice = -1;
        try {
            String input = readFromInputStream();
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (choice >= 0 && choice < solutions.size()) {
            QuerySolution solution = solutions.get(choice);
            return solution.get("s").asResource().getURI();
        }
        return null;
    }

    private void addCreatorData(final String creatorURI) {
        // TODO
        System.out.println();
        String query = "SELECT DISTINCT ?predicate ?object WHERE { " +
                " <" + creatorURI + "> ?predicate ?object ." +
                "}";
        Set<QuerySolution> creatorInfo = queryDBPedia(query);
        for (QuerySolution solution : creatorInfo) {
            //solution.get()
        }
    }

    private Set<QuerySolution> queryDBPedia(final String sparqlQuery) {
        Set<QuerySolution> solutions = new HashSet<>();
        System.out.println("sparqlQuery = " + sparqlQuery);
        final QueryExecution qExec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", sparqlQuery);
        try {

            ResultSet resultSet = qExec.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.next();
                System.out.println("solution = " + solution.toString());
                solutions.add(solution);
            }
        } finally {
            qExec.close();
        }
        return solutions;
    }

    private String readFromInputStream() {
        StringBuilder str = new StringBuilder();
        try {
            int input;
            while ((input = System.in.read()) != -1) {
                char c = (char)input;
                if (c == '\n' || c == 'r') {
                    break;
                }
                str.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
