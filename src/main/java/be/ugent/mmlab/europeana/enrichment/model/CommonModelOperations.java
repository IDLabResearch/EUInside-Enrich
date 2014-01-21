package be.ugent.mmlab.europeana.enrichment.model;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/17/14.
 */
public class CommonModelOperations {
    private final Model model;
    private final RdfNodeFactory rdfNodeFactory;

    public CommonModelOperations(final Model model) {
        this.model = model;
        rdfNodeFactory = RdfNodeFactory.getInstance();
    }

    public void addSameAs(final Resource resource, final String uri) {
        model.add(resource, rdfNodeFactory.getSameAsProperty(), model.createResource(uri));
    }

    public StmtIterator getSameAs(final Resource subject) {
        return ((ModelCon) model).listStatements(subject, rdfNodeFactory.getSameAsProperty(), null); // last parameter = null gives function overloading error without casting. Design bug in Jena?
    }

    public Map<String, Statement> getSameAsObjToStmt(final Resource subject) {
        StmtIterator sameAsStatements = getSameAs(subject);
        Map<String, Statement> results = new HashMap<>();
        while (sameAsStatements.hasNext()) {
            Statement statement = sameAsStatements.nextStatement();
            results.put(statement.getObject().asResource().getURI(), statement);
        }
        return results;
    }

    public ResIterator getTodoSubjects() {
        return model.listSubjectsWithProperty(rdfNodeFactory.getTodoProperty());
    }

    public void addTodo(final Resource subject) {
        model.add(subject, rdfNodeFactory.getTodoProperty(), rdfNodeFactory.getTrueLiteral());
    }

    public void removeTodo(final Resource subject) {
        model.remove(subject, rdfNodeFactory.getTodoProperty(), rdfNodeFactory.getTrueLiteral());  // null as last parameter also crashes?? again a design bug in Jena??
    }

    public String getType(final Resource subject) {
        String type;
        StmtIterator typeStmt = ((ModelCon) model).listStatements(subject, rdfNodeFactory.getTypeProperty(), null);
        if (typeStmt.hasNext()) {
            Statement statement = typeStmt.nextStatement();
            type = statement.getObject().toString();
        } else {
            type = null;
        }
        return type;
    }

    public boolean hasType(final Resource subject) {
        return model.contains(subject, rdfNodeFactory.getTypeProperty());
    }

    public void addAgentType(final Resource agentName) {
        model.add(agentName, rdfNodeFactory.getTypeProperty(), rdfNodeFactory.getAgentResource());
    }

    public void addCreator(final String subjectUri, final Resource creator) {
        Resource subject = model.createResource(subjectUri);
        model.add(subject, rdfNodeFactory.getCreatorProperty(), creator);
    }

    public void addCreatorToRemove(final String subjectUri, final String creatorName) {
        Resource subject = model.createResource(subjectUri);
        model.add(subject, rdfNodeFactory.getCreatorProperty(), model.createLiteral(creatorName));
    }

    public XSDDateTime getBeginDate() {

        // fist try birth date (person)
        XSDDateTime beginDate = getDate(rdfNodeFactory.getBirthDateProperty());
        if (beginDate == null) {
            beginDate = getDate(rdfNodeFactory.getDateOfBirthProperty());
        }
        return beginDate;
    }

    public XSDDateTime getEndDate() {

        // fist try birth date (person)
        XSDDateTime endDate = getDate(rdfNodeFactory.getDeathDateProperty());
        if (endDate == null) {
            endDate = getDate(rdfNodeFactory.getDateOfDeathProperty());
        }
        return endDate;
    }

    private XSDDateTime getDate(final Property dateProperty) {
        XSDDateTime date = null;
        NodeIterator dateNodes = model.listObjectsOfProperty(dateProperty);
        if (dateNodes.hasNext()) {
            RDFNode birthNode = dateNodes.next();
            Object dateObj = birthNode.asLiteral().getValue();
            if (dateObj instanceof XSDDateTime) {
                date = (XSDDateTime) dateObj;
            }
        }
        return date;
    }

    public void addBeginDate(final Resource subject, final XSDDateTime date) {
        Property beginDateProperty = rdfNodeFactory.getBeginProperty();
        addDate(subject, beginDateProperty, date);
    }

    public void addEndDate(final Resource subject, final XSDDateTime date) {
        Property endDateProperty = rdfNodeFactory.getEndProperty();
        addDate(subject, endDateProperty, date);
    }

    private void addDate(final Resource subject, final Property dateProperty, final XSDDateTime date) {
        if (date != null) {
            model.add(subject, dateProperty, date.toString(), date.getNarrowedDatatype());
        }
    }

}
