package be.ugent.mmlab.europeana;

import be.ugent.mmlab.europeana.kb.TDB.ResultProcessor;
import be.ugent.mmlab.europeana.kb.TDB.TDBStore;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: ghaesen
 * Date: 10/28/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class TDBStoreTest {
    private final File tempTDBDir;

    public TDBStoreTest() {
        tempTDBDir = new File(System.getProperty("java.io.tmpdir"), "___TestTDBStore");
        System.out.println("Temporary TDB store: " + tempTDBDir);
    }

    @Test
    public void testLoadFromFile() throws IOException, CompressorException {
        if (tempTDBDir.exists()) {
            deleteRecursive(tempTDBDir);
        }

        System.out.println("Creating temporary TDB store");
        TDBStore store = new TDBStore(tempTDBDir.getAbsolutePath());

        String tripleFile = "bokrijk.nt.xz";
        URL tripleFileUrl = getClass().getClassLoader().getResource(tripleFile);
        assertNotNull("Resource " + tripleFile + " not found!", tripleFileUrl);
        System.out.println("Reading triples from " + tripleFile);
        store.addFromFile(tripleFileUrl.getFile(), "N-TRIPLE");
        System.out.println("Triples loaded!");

    }

    @Test
    public void testSparqlQueryOnType() throws IOException, CompressorException {
        if (!tempTDBDir.exists()) {
            testLoadFromFile();
        }

        // get objects whose dc:type is "moerbeugel" (2)
        String query =
                "PREFIX edm: <http://www.europeana.eu/schemas/edm/>\n" +
                        "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                        "PREFIX ore: <http://www.openarchives.org/ore/terms/>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "SELECT ?proxy ?resource ?description ?type ?edmType" +
                        " WHERE {" +
                        " ?proxy ore:proxyFor ?resource." +
                        " ?proxy dc:title ?title." +
                        " ?proxy dc:description ?description." +
                        " ?proxy dc:type \"moerbeugel\"." +
                        " ?proxy edm:type ?edmType." +
                        "}";

        queryAndPrintResults(query);
    }

    @Test
    public void testSparqlQueryOnImages() throws IOException, CompressorException {
        if (!tempTDBDir.exists()) {
            testLoadFromFile();
        }

        // get objects whose edm:type is "IMAGE"
        String query =
                "PREFIX edm: <http://www.europeana.eu/schemas/edm/>\n" +
                        "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                        "PREFIX ore: <http://www.openarchives.org/ore/terms/>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "SELECT ?proxy ?resource ?description ?dcType" +
                        " WHERE {" +
                        " ?proxy ore:proxyFor ?resource." +
                        " ?proxy dc:title ?title." +
                        " ?proxy dc:description ?description." +
                        " ?proxy dc:type ?dcType." +
                        " ?proxy edm:type \"IMAGE\"." +
                        "}";
        queryAndPrintResults(query);

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

    private void queryAndPrintResults(final String query) {
        TDBStore store = new TDBStore(tempTDBDir.getAbsolutePath());
        System.out.println("Query: " + query);
        store.sparqSelectlQuery(query, new ResultProcessor() {
            @Override
            public void process(Map<String, RDFNode> varToNode) {
                for (Map.Entry<String, RDFNode> stringRDFNodeEntry : varToNode.entrySet()) {
                    System.out.println(stringRDFNodeEntry.getKey() + ": " + stringRDFNodeEntry.getValue());
                }
                System.out.println();
            }
        });
    }
}
