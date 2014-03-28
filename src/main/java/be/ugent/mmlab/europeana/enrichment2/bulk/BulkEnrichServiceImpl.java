package be.ugent.mmlab.europeana.enrichment2.bulk;

//import be.ugent.mmlab.europeana.enrichment.linking.CreatorResourceLinker;
//import be.ugent.mmlab.europeana.enrichment.linking.ResourceLinker;
//import be.ugent.mmlab.europeana.kb.TDB.TDBStore;
//import com.hp.hpl.jena.rdf.model.Model;
//import org.apache.commons.io.FileUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import java.io.File;
//import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/11/14.
 */
public class BulkEnrichServiceImpl implements BulkEnrichService {
    private Logger logger = LogManager.getLogger(this.getClass());

    private static BulkEnrichServiceImpl instance;

    private final Map<String, Future<String>> reference2PhaseOneMap = new HashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static BulkEnrichService getInstance() {
        if (instance == null) {
            instance = new BulkEnrichServiceImpl();
        }
        return instance;
    }

    private BulkEnrichServiceImpl() {}

    @Override
    public void phaseOne(final String reference) {

        Future<String> future = executorService.submit(() -> processPhaseOne(reference));
        reference2PhaseOneMap.put(reference, future);
    }

    private String processPhaseOne(final String reference) {
        String result = "OK";

//        File rdfFile = new File(System.getProperty("java.io.tmpdir"), reference + ".rdf.gz");
//        File tdbDir = new File(System.getProperty("java.io.tmpdir"), reference);
//        try {
//            // make jena model, as TDB store
//            logger.debug("creating jena TDB store for {}", reference);
//            TDBStore tdbStore = new TDBStore(tdbDir.getAbsolutePath());
//            tdbStore.addFromFile(rdfFile.getAbsolutePath());
//            Model model = tdbStore.getDataset().getDefaultModel();
//            logger.debug("TDBStore for {} successfully created. Starting phase one enrichment", reference);
//
//            // enrich. TODO: create resource linkers another way!
//            ResourceLinker creatorResourceLinker = new CreatorResourceLinker("localhost", "/agents/");
//            creatorResourceLinker.link(model);
//
//            logger.debug("Phase one enrichment of {} done.", reference);
//
//            // TODO set status done
//
//        } catch (IOException e) {
//            result = "ERROR: " + e.getMessage();
//            rdfFile.delete();
//            try {FileUtils.deleteDirectory(tdbDir);} catch (IOException e1) {/* who cares */}
//            // TODO set status failed
//        }
//

//        // make hdt object
//        try {
//            HDT hdt = HDTManager.generateHDT(
//                    rdfFile.getAbsolutePath(),
//                    "http://boe",
//                    RDFNotation.RDFXML,
//                    new HDTSpecification(),
//                    null);
//
//            IteratorTripleString tripleIter = hdt.search("", "", "");
//            while (tripleIter.hasNext()) {
//                TripleString triple = tripleIter.next();
//                Triple jenaTriple = asJenaTriple(triple);
//            }
//
//            //Model model = ModelFactory.createModelForGraph(graph);
//
//        } catch (IOException | ParserException e) {
//
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            rdfFile.delete();
//        }

        return result;
    }

//    private Triple asJenaTriple(final TripleString hdtTriple) {
//        return null;
//    }
}
