package be.ugent.mmlab.europeana.enrichment.linking;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by ghaesen on 12/16/13.
 */
public abstract class AbstractResourceLinker implements ResourceLinker {
    protected final Model addModel;
    protected final Model substractModel;

    // part of uri that represents resource
    private final String host;
    private final String path;

    protected AbstractResourceLinker(String host, String path) {
        this.host = host;
        this.path = path;
        addModel = ModelFactory.createDefaultModel();
        substractModel = ModelFactory.createDefaultModel();

    }

    @Override
    public void mergeResult(final Model model) {
        model.begin();
        model.add(addModel);
        model.remove(substractModel);
        model.commit();
    }

    protected String getURI(final String localName) throws URISyntaxException, MalformedURLException {
        return (new URI("http", host, path + localName, null)).toURL().toString();
    }
}
