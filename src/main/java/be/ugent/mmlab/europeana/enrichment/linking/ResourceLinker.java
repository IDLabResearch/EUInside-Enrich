package be.ugent.mmlab.europeana.enrichment.linking;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * Created by ghaesen on 12/16/13.
 */
public interface ResourceLinker {
    public void link(final Quad input);
    public void mergeResult(final Model model);
}
