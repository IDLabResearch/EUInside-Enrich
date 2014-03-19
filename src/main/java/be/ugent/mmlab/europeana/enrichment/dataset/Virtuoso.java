package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 */
public class Virtuoso implements Dataset {

    private final String sparqlEndpoint = "http://dbpedia.org/sparql";  // TODO: via config
    //private final String sparqlEndpoint = "http://restdesc.org:8891/sparql";

    private Set<QuerySolution> queryDBPedia(final String sparqlQuery) {
        Set<QuerySolution> solutions = new HashSet<>();
        System.out.println("sparqlQuery = " + sparqlQuery);
        final QueryExecution qExec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);
        //final QueryExecution qExec = QueryExecutionFactory.sparqlService("http://restdesc.org:8891/sparql", sparqlQuery);
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

    @Override
    public List<String> searchSubject(final String subject) {

        // prepare query for bif:contains
        final String nameCombinationsOr = StringCombiner.combinations(subject);  // this returns names concatenated with 'or'
        final String nameCombinationsAnd = nameCombinationsOr.replaceAll(" or ", " and ");

        // first try "and"
        List<String> dbPediaUris = queryDBPediaForLabel(nameCombinationsAnd);
        if (dbPediaUris.isEmpty()) {
            // then try "or"
            dbPediaUris = queryDBPediaForLabel(nameCombinationsOr);
        }

        return dbPediaUris;
    }

    private List<String> queryDBPediaForLabel(final String label) {
        // escape single quotes?
        String newLabel = label.replaceAll("-", "_");
        newLabel = newLabel.replaceAll("'", " and ");

        List<String> solutions = new ArrayList<>();
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX dcterms: <http://purl.org/dc/terms/> \n" +
                //"PREFIX bif: <http://www.openlinksw.com/schema/sparql/extensions#> \n" +
                "SELECT DISTINCT ?s ?label WHERE { \n" +
                "            ?s rdfs:label ?label . \n" +
                "            FILTER (lang(?label) = 'en'). \n" +
                "            ?label <bif:contains> '" + newLabel + "' . \n" +
                //"            ?s dcterms:subject ?sub \n" +
                "}";
        Set<QuerySolution> querySolutions = queryDBPedia(query);
        for (QuerySolution querySolution : querySolutions) {
            solutions.add(querySolution.get("s").asResource().getURI());
        }
        return solutions;
    }
}
