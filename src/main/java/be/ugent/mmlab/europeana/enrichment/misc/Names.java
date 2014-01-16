package be.ugent.mmlab.europeana.enrichment.misc;

/**
 * Created by ghaesen on 12/17/13.
 */
public enum Names {
    AGENT("http://www.europeana.eu/schemas/edm/Agent"),
    CREATOR("http://purl.org/dc/elements/1.1/creator"),
    NAME("http://xmlns.com/foaf/0.1/name"),
    SAME_AS("http://www.w3.org/2002/07/owl#sameAs"),
    TYPE("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),

    // own temporary resources
    TODO("http://TODO")
    ;
    private String uri;

    Names(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
