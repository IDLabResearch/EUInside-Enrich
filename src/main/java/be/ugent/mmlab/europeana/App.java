package be.ugent.mmlab.europeana;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.setup.DatasetBuilderStd;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class App {

    private void loadFromFile(final String fileName) {

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Ola");
        Model model = null;
        Dataset dataset = null;
        try (FileInputStream in = new FileInputStream("/home/ghaesen/data/europeana/09421a_Ag_BE_Elocal_Bokrijk.nt")){
            // create model using TDB, Jena's file-based RDF store
            dataset = TDBFactory.createDataset("/tmp/jenadb");
            //System.out.println("Reading triples");
            model = dataset.getDefaultModel();
            model.read(in, null, "N-TRIPLE");

            NodeIterator objIter = model.listObjects();
            while (objIter.hasNext()) {
                RDFNode node = objIter.next();
                System.out.println(" = " + node.toString());
            }
        } finally {
            if (model != null) model.close();
            if (dataset != null) dataset.close();
        }

    }
}
