package be.ugent.mmlab.europeana.enrichment.sparql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ghaesen on 12/17/13.
 */
public class QueryEndpoint {

    public static Set<QuerySolution> queryDBPedia(final String sparqlQuery) {
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

    public static Set<String> queryDBPediaForLabel(final String label) {
        Set<String> solutions = new HashSet<>();
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX dcterms: <http://purl.org/dc/terms/> \n" +
                //"PREFIX bif: <http://www.openlinksw.com/schema/sparql/extensions#> \n" +
                "SELECT DISTINCT ?s ?label WHERE { \n" +
                "            ?s rdfs:label ?label . \n" +
                "            FILTER (lang(?label) = 'en'). \n" +
                "            ?label <bif:contains> \"" + label + "\" . \n" +
                "            ?s dcterms:subject ?sub \n" +
                "}";
        Set<QuerySolution> querySolutions = queryDBPedia(query);
        for (QuerySolution querySolution : querySolutions) {
            solutions.add(querySolution.get("s").asResource().getURI());
        }
        return solutions;
    }
}
