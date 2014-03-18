package be.ugent.mmlab.europeana.enrichment.dataset;

import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/18/14.
 *
 * This interface represents access to a data set.
 *
 */
public interface Dataset {

    /**
     * Search for a given subject.
     * @param subject A subject to search. E.g. "George Adams Junior".
     * @return Subjects related to (in a syntactical way) the given sear subject.
     */
    public Set<String> searchSubject(final String subject);
}
