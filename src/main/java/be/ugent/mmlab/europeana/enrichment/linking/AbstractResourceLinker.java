package be.ugent.mmlab.europeana.enrichment.linking;

import be.ugent.mmlab.europeana.enrichment.model.CommonModelOperations;
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

    protected final CommonModelOperations addModelOps;
    protected final CommonModelOperations subModelOps;

    // part of uri that represents resource
    private final String host;
    private final String path;

    protected AbstractResourceLinker(String host, String path) {
        this.host = host;
        this.path = path;
        addModel = ModelFactory.createDefaultModel();
        substractModel = ModelFactory.createDefaultModel();
        addModelOps = new CommonModelOperations(addModel);
        subModelOps = new CommonModelOperations(substractModel);
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
