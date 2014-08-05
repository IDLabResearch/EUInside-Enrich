package be.ugent.mmlab.europeana.enrichment.oneRecord;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/26/14.
 */
public class ModelCache {
    private final Logger logger = LogManager.getLogger(this.getClass());

    // TODO this can be enhanced a lot; e.g. a key-value store, snappy compression, ...
    private static ModelCache instance;
    private Map<Long, String> referenceToModel = new ConcurrentHashMap<>();
    private final long cleanUpDelay = 24 * 3600 * 1000; // 1 day

    // clears cache every 10 minutes
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ModelCache() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.debug("Clearing cache");
                long now = System.currentTimeMillis();
                referenceToModel.keySet().stream().filter(timestamp -> timestamp < now - cleanUpDelay).forEach(timestamp -> {
                    logger.debug("Deleting cached model with reference {}", timestamp);
                    referenceToModel.remove(timestamp);
                });
            }
        }, cleanUpDelay, cleanUpDelay, TimeUnit.MILLISECONDS);
    }

    public static synchronized ModelCache getInstance() {
        if (instance == null) {
            instance = new ModelCache();
        }
        return instance;
    }

    public long add(final Model model) {
        long timestamp = System.currentTimeMillis();
        referenceToModel.put(timestamp, serialize(model));
        return timestamp;
    }

    public Model get(final long reference) {
        String serializedModel = referenceToModel.get(reference);
        if (serializedModel != null) {
            StringReader stringReader = new StringReader(serializedModel);
            Model model = ModelFactory.createDefaultModel();
            model.read(stringReader, null, "N3");
            return model;
        } else {
            return null;
        }
    }

    public void put(final Model model, final long reference) {
        referenceToModel.put(reference, serialize(model));
    }

    private String serialize(final Model model) {
        StringWriter stringWriter = new StringWriter();
        model.write(stringWriter, "N3");
        return stringWriter.toString();
    }
}
