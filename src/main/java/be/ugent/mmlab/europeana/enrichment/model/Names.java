package be.ugent.mmlab.europeana.enrichment.model;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 12/17/13.
 */
public enum Names {
    AGENT("http://www.europeana.eu/schemas/edm/Agent"),
    CREATOR("http://purl.org/dc/elements/1.1/creator"),
    NAME("http://xmlns.com/foaf/0.1/name"),
    SAME_AS("http://www.w3.org/2002/07/owl#sameAs"),
    TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),

    DBP_BIRTH_DATE("http://dbpedia.org/ontology/birthDate"),
    DBP_DATE_OF_BIRTH("http://dbpedia.org/property/dateOfBirth"),
    DBP_DEATH_DATE("http://dbpedia.org/ontology/deathDate"),
    DBP_DATE_OF_DEATH("http://dbpedia.org/property/dateOfDeath"),
    DBP_REDIRECT("http://dbpedia.org/ontology/wikiPageRedirects"),
    RDFS_LABEL("http://www.w3.org/2000/01/rdf-schema#label"),
    PREF_LABEL("http://www.w3.org/2004/02/skos/core#prefLabel"),
    DBP_ABSTRACT("http://dbpedia.org/ontology/abstract"),
    SKOS_NOTE("http://www.w3.org/2004/02/skos/core#note"),
    DCTERMS_SUBJECT("http://purl.org/dc/terms/subject"),

    EDM_BEGIN("http://www.europeana.eu/schemas/edm/begin"),  // begin (corporate) or birth (person)
    EDM_END("http://www.europeana.eu/schemas/edm/end"),      // end (corporate) or death (person)
    EDM_IS_RELATED_TO("http://www.europeana.eu/schemas/edm/isRelatedTo"),

    //RG_BIRTH_DATE("http://RDVocab.info/ElementsGr2/"),

    // own temporary resources
    COMMENT("http://www.w3.org/2000/01/rdf-schema#comment")
    ;

    private String uri;

    Names(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
