package be.ugent.mmlab.europeana.enrichment.linking;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by ghaesen on 12/16/13.
 */
public interface ResourceLinker {
    public void link(final Model model);
    public void link(final Triple input);
    public void mergeResult(final Model model);
}
