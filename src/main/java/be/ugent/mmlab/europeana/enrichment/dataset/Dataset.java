package be.ugent.mmlab.europeana.enrichment.dataset;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.List;

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
    public List<String> searchSubject(final String subject);

    public Model getModelFor(final String subjectResource);
}
