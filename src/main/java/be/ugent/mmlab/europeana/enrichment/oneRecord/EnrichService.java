package be.ugent.mmlab.europeana.enrichment.oneRecord;

import java.io.IOException;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/24/14.
 */
public interface EnrichService {
    public PhaseOneResult phaseOne(final String record) throws IOException;
    public String phaseTwo(final long reference, final Map<String, String> subjectToURI);
    public String getFromCache(final long reference);
}
