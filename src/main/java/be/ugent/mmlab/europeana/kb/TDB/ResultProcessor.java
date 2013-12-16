package be.ugent.mmlab.europeana.kb.TDB;

import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ghaesen
 * Date: 10/28/13
 * Time: 2:07 PM
 */
public interface ResultProcessor {

    void process(Map<String, RDFNode> varToNode);
}
