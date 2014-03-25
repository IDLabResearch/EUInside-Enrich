package be.ugent.mmlab.europeana.enrichment.dataset;

import java.util.Collections;
import java.util.List;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/25/14.
 */
public abstract class AbstractDataset implements Dataset {
    /**
     * Implementation that uses an index, if available. Subclasses that wish to use an index search only should not
     * override this method. However, as the presence of an index is not mandatory, it is safer to invoke this using
     * super.searchSubject() from an own implementation.
     *
     * @param subject A subject to search. E.g. "George Adams Junior".
     * @return A list with resources matching this search.
     */
    @Override
    public List<String> searchSubject(String subject) {
        LabelIndex index = LabelIndexFactory.get();
        if (index != null) {
            return index.searchSubject(subject);
        }
        return Collections.emptyList();
    }
}
