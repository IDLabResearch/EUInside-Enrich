package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.enrichment.Enricher;
import be.ugent.mmlab.europeana.kb.TDB.ResultProcessor;
import be.ugent.mmlab.europeana.kb.TDB.TDBStore;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by ghaesen on 12/9/13.
 */
public class TestGalileo {

    private final File tempTDBDir;

    public TestGalileo() {
        tempTDBDir = new File(System.getProperty("java.io.tmpdir"), "___TestTDBStore");
        System.out.println("Temporary TDB store: " + tempTDBDir);
    }

    private void loadTriplesFromFile(final String tripleFile) throws IOException, CompressorException {
        if (tempTDBDir.exists()) {
            deleteRecursive(tempTDBDir);
        }

        System.out.println("Creating temporary TDB store");
        TDBStore store = new TDBStore(tempTDBDir.getAbsolutePath());

        /*URL tripleFileUrl = getClass().getClassLoader().getResource(tripleFile);
        assertNotNull("Resource " + tripleFile + " not found!", tripleFileUrl);*/
        System.out.println("Reading triples from " + tripleFile);
        String type = tripleFile.contains(".rdf") ? "RDF/XML" : "N-TRIPLE";
        store.addFromFile(tripleFile, type);
        System.out.println("Triples loaded!");
    }

    private void deleteRecursive(final File pathToRemove) {
        if (pathToRemove.isFile()) {
            if (!pathToRemove.delete()) {
                fail("Could not delete " + pathToRemove);
            }
        } else {
            File[] files = pathToRemove.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteRecursive(file);
                }
            }
            if (!pathToRemove.delete()) {
                fail("Could not delete " + pathToRemove);
            }
        }
    }

    @Test
    public void testGalileo() throws IOException, CompressorException, URISyntaxException {
        //if (!tempTDBDir.exists()) {
            loadTriplesFromFile("/home/ghaesen/data/europeana/galileo/one_example.rdf");
        //}

        TDBStore store = new TDBStore(tempTDBDir.getAbsolutePath());
        //store.iterate();
        Enricher enricher = new Enricher();
        //enricher.enrich(store.getDataset());
        enricher.enrich(store.getDataset());

        // output to file

        // get objects whose dc:type is "moerbeugel" (2)
       /* String query =
                "PREFIX edm: <http://www.europeana.eu/schemas/edm/>\n" +
                        "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                        "PREFIX ore: <http://www.openarchives.org/ore/terms/>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "SELECT ?proxy ?resource ?description ?type ?edmType" +
                        " WHERE {" +
                        " ?proxy ore:proxyFor ?resource." +
                        " ?proxy dc:title ?title." +
                        " ?proxy dc:description ?description." +
                        " ?proxy dc:creator \"George Adams junior\"." +
                        " ?proxy edm:type ?edmType." +
                        "}";

        queryAndPrintResults(query);*/
    }

    public static void main(String[] args) throws CompressorException, IOException, URISyntaxException {
        TestGalileo testGalileo = new TestGalileo();
        testGalileo.testGalileo();
    }

}
