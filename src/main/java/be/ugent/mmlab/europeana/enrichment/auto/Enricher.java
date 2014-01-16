package be.ugent.mmlab.europeana.enrichment.auto;

import be.ugent.mmlab.europeana.enrichment.enriching.Extender;
import be.ugent.mmlab.europeana.enrichment.linking.CreatorResourceLinker;
import be.ugent.mmlab.europeana.enrichment.linking.ResourceLinker;
import be.ugent.mmlab.europeana.enrichment.misc.Names;
import be.ugent.mmlab.europeana.enrichment.selecting.UserInterface;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

import java.util.*;

import static be.ugent.mmlab.europeana.enrichment.misc.Names.TYPE;

/**
 * Created by ghaesen on 12/9/13.
 */
public class Enricher {

    //private Set<String> processed = new HashSet<>();    // list of already enriched objects
    private Map<String, String> nameToResource = new HashMap<>();   // mapping (creator ) name to agent resource URI

    /*private final Node typeNode = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final Node agentNode = NodeFactory.createURI("http://www.europeana.eu/schemas/edm/Agent");
    private final Node foafNameNode = NodeFactory.createURI("http://xmlns.com/foaf/0.1/name");
    private final Node sameAsNode = NodeFactory.createURI("http://www.w3.org/2002/07/owl#sameAs");*/

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
        //Model modelToDelete =
        try {
            model.begin();

            Property todoProperty = model.createProperty(Names.TODO.getUri());
            Property sameAsProperty = model.createProperty(Names.SAME_AS.getUri());
            Property typeProperty = model.createProperty(TYPE.getUri());

            ResIterator subjectsWithSelection = model.listSubjectsWithProperty(todoProperty);
            while (subjectsWithSelection.hasNext()) {
                // TODO get ... ... <subject>, to get and display original context (subject, predicate), e.g. to know that Jos Bosmans is a creator of a proxy
                Resource subject = subjectsWithSelection.nextResource();

                // now get all sameAs objects
                StmtIterator possibleCandidateStatements = ((ModelCon) model).listStatements(subject, sameAsProperty, null); // last parameter = null gives function overloading error without casting. Design bug in Jena?
                Map<String, Statement> statementMap = objectToStatement(possibleCandidateStatements);
                // send objects to select to user interface
                String selectedUri = userInterface.makeSelection(subject.getURI(), statementMap.keySet());
                if (selectedUri == null) {
                    break;
                }

                // remove all statements except the one to preserve
                statementMap.remove(selectedUri);
                for (Statement statement : statementMap.values()) {
                    model.remove(statement.getSubject(), statement.getPredicate(), statement.getObject());
                }
                model.remove(subject, todoProperty, model.createTypedLiteral(true));  // null as last parameter also crashes?? again a design bug in Jena??
                model.commit();

                // add real enrichment
                // first get type; get "<subject> a <object>" triple
                StmtIterator typeStmt = ((ModelCon) model).listStatements(subject, typeProperty, null);
                if (typeStmt.hasNext()) {
                    Statement statement = typeStmt.nextStatement();
                    String type = statement.getObject().toString();
                    extender.extend(type, selectedUri);
                }
            }
            model.write(System.out, "TURTLE");
        } finally {
            model.close();
        }

    }

    private Map<String, Statement> objectToStatement(final StmtIterator stmtIterator) {
        Map<String, Statement> results = new HashMap<>();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.nextStatement();
            results.put(statement.getObject().asResource().getURI(), statement);
        }
        return results;
    }

 /*   private void addCreator(final Quad originalQuad, final Set<Quad> quadsToAdd, final Set<Quad> quadsToDelete) throws URISyntaxException, MalformedURLException {
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
    } */
}
