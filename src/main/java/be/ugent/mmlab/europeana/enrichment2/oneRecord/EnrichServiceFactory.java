package be.ugent.mmlab.europeana.enrichment2.oneRecord;

import be.ugent.mmlab.europeana.enrichment.config.Config;
import be.ugent.mmlab.europeana.enrichment.dataset.Dataset;
import be.ugent.mmlab.europeana.enrichment.dataset.DatasetFactory;
import be.ugent.mmlab.europeana.enrichment.linking.CreatorResourceLinker;
import be.ugent.mmlab.europeana.enrichment.linking.ResourceLinker;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public class EnrichServiceFactory {
    public static EnrichService create() {
        Config config = Config.getInstance();
        Set<ResourceLinker> resourceLinkers = new HashSet<>();
        Dataset dataset = DatasetFactory.get();
        resourceLinkers.add(new CreatorResourceLinker(config.getCreatorHost(), config.getCreatorPath(), dataset));
        return new EnrichServiceImpl(resourceLinkers);
    }
}
