package be.ugent.mmlab.europeana.enrichment.dataset;

import be.ugent.mmlab.europeana.enrichment.misc.StringCombiner;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 */
public class Virtuoso extends AbstractDataset {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final String sparqlEndpoint;

    public Virtuoso(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

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
        // first try index search
        List<String> dbPediaUris = super.searchSubject(subject);

        // try on virtuoso itself if index search fails
        if (dbPediaUris.isEmpty()) {
            // prepare query for bif:contains
            final String nameCombinationsOr = StringCombiner.combinations(subject);  // this returns names concatenated with 'or'
            final String nameCombinationsAnd = nameCombinationsOr.replaceAll(" or ", " and ");

            // first try "and"
            dbPediaUris = queryDBPediaForLabel(nameCombinationsAnd);
            if (dbPediaUris.isEmpty()) {
                // then try "or"
                dbPediaUris = queryDBPediaForLabel(nameCombinationsOr);
            }

        }
        return dbPediaUris;
    }

    @Override
    public Collection<String> searchSubject(String predicate, String object) {
        List<String> solutions = new ArrayList<>();
        String query = "select ?subject\n" +
                "where {\n" +
                " ?subject <" + predicate + "> <" + object + ">\n" +
                "} LIMIT 1000";
        Set<QuerySolution> querySolutions = queryDBPedia(query);
        for (QuerySolution querySolution : querySolutions) {
            solutions.add(querySolution.get("subject").asResource().getURI());
        }
        return solutions;
    }

    @Override
    public Model getModelFor(String subjectResource) {
        String n3Uri = toDPPediaN3(subjectResource);
        try {
            String body = Request.Get(n3Uri).execute().returnContent().asString();
            if (!body.isEmpty()) {
                Model newModel = ModelFactory.createDefaultModel();
                newModel.read(new StringReader(body), null, "N3");
                return newModel;
            }
        } catch (IOException e) {
            logger.error("Could nog get resource [{}] from DBPedia", subjectResource, e);
        }
        return null;
    }

    private String toDPPediaN3(final String uri) {
        String newUri = uri;
        if (uri.contains("dbpedia.org/resource")) {
            newUri = newUri.replace("resource", "data") + ".n3";
        }
        return newUri;
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
