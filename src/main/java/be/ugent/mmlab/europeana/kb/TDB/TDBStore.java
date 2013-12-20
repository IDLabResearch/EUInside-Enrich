package be.ugent.mmlab.europeana.kb.TDB;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ghaesen
 * Date: 10/28/13
 * Time: 9:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class TDBStore {

    private final Dataset dataset;

    public TDBStore (final String tdbDirectory) {
        dataset = TDBFactory.createDataset(tdbDirectory);
    }

    /*public Model getModel() {
        return dataset.getDefaultModel();
    } */

    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Read rdf, triples, turle, ... statements from the given file and put them in the current model.
     * This is done in one transaction: either everything is ok and added, either nothing happened.
     * Mind that this is slower than bulk-loading from the command-line.
     * @param fileName  The name of the input file. Files can be plain text or compressed with gzip, bzip2, xz.
     * @param lang      The type (language) of the data. Can be "RDF/XML", "N-TRIPLE",
     *                  "TURTLE" (or "TTL") and "N3"
     * @exception IOException   Reading from the given file went wrong. No data is added.
     */
    public void addFromFile(final String fileName, final String lang) throws IOException {
        InputStream cin = null;
        try {
            try {
                cin = new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            } catch (CompressorException e) {
                cin = new BufferedInputStream(new FileInputStream(fileName));
            }
            dataset.begin(ReadWrite.WRITE);
            dataset.getDefaultModel().read(cin, null, lang);
            dataset.commit();
        } finally {
            if (cin != null) {
                cin.close();
            }
            dataset.end();
        }
    }


    public void sparqSelectlQuery(final String sparqlQuery, final ResultProcessor resultProcessor) {
        final QueryExecution qExec = QueryExecutionFactory.create(sparqlQuery, dataset);
        try {
            dataset.begin(ReadWrite.READ);
            ResultSet resultSet = qExec.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.next();
                Map<String, RDFNode> varToNode = new HashMap<>();
                Iterator<String> varNames = solution.varNames();
                while (varNames.hasNext()) {
                    String varName = varNames.next();
                    RDFNode node = solution.get(varName);
                    varToNode.put(varName, node);
                }
                resultProcessor.process(varToNode);
            }
        } finally {
            qExec.close();
            dataset.end();
        }
    }
}
