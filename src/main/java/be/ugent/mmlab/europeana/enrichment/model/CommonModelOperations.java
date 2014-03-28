package be.ugent.mmlab.europeana.enrichment.model;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<String> getSameAs(final Resource subject) {
        List<String> objects = new ArrayList<>();
        NodeIterator sameAsObjects = model.listObjectsOfProperty(subject, rdfNodeFactory.getSameAsProperty());
        while (sameAsObjects.hasNext()) {
            RDFNode sameAsObject = sameAsObjects.next();
            objects.add(sameAsObject.asResource().getURI());
        }
        return objects;
    }

    public void removeSameAs(final Resource subject, final Resource object) {
        Statement removeStatement = new StatementImpl(subject, rdfNodeFactory.getSameAsProperty(), object);
        model.remove(removeStatement);
    }

    public ResIterator getTodoSubjects() {
        return model.listSubjectsWithProperty(rdfNodeFactory.getCommentProperty(), rdfNodeFactory.getTodoLiteral());
    }

    public void addTodo(final Resource subject) {
        model.add(subject, rdfNodeFactory.getCommentProperty(), rdfNodeFactory.getTodoLiteral());
    }

    public void removeTodo(final Resource subject) {
        model.remove(subject, rdfNodeFactory.getCommentProperty(), rdfNodeFactory.getTodoLiteral());
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

    public Set<Triple> getCreatorTriples() {
        Set<Triple> creatorTriples = new HashSet<>();
        Property creatorProperty = rdfNodeFactory.getCreatorProperty();
        ResIterator subjects = model.listSubjectsWithProperty(creatorProperty);
        while (subjects.hasNext()) {
            Resource creator = subjects.nextResource();
            NodeIterator objectIter = model.listObjectsOfProperty(creator, creatorProperty);
            while (objectIter.hasNext()) {
                RDFNode object = objectIter.next();
                Triple triple = new Triple(creator.asNode(), creatorProperty.asNode(), object.asNode());
                creatorTriples.add(triple);
            }
        }
        return creatorTriples;
    }

    public void addCreatorToRemove(final String subjectUri, final LiteralLabel creatorName) {
        Literal creatorLiteral = ResourceFactory.createLangLiteral(creatorName.getValue().toString(), creatorName.language());
        Resource subject = model.createResource(subjectUri);
        model.add(subject, rdfNodeFactory.getCreatorProperty(), creatorLiteral);
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

    public List<Literal> getRdfsLabel() {
        return getLiteral(rdfNodeFactory.getRdfsLabelProperty());
    }

    public void addPrefLabel(final Resource subject, final List<Literal> prefLabels) {
        addLiteral(subject, prefLabels, rdfNodeFactory.getPrefLabelProperty());
    }

    public List<Literal> getAbstract() {
        List<Literal> notes = getLiteral(rdfNodeFactory.getDbpAbstractPropery());
        notes.addAll(getLiteral(rdfNodeFactory.getRdfsCommentProperty()));
        return notes;
    }

    public void addSkosNote(final Resource subject, final List<Literal> skosNotes) {
        addLiteral(subject, skosNotes, rdfNodeFactory.getSkosNoteProperty());
    }

    public List<Resource> getSubjects() {
        return getResources(rdfNodeFactory.getDctermsSubject());
    }

    public List<Literal> getLiteral(final Property property) {
        final List<Literal> result = new ArrayList<>();
        NodeIterator nodes = model.listObjectsOfProperty(property);
        while (nodes.hasNext()) {
            RDFNode literalNode = nodes.next();
            result.add(literalNode.asLiteral());
        }
        return result;
    }

    public void addLiteral(final Resource subject, final List<Literal> literals, final Property property) {
        for (Literal literal : literals) {
            model.add(subject, property, literal);
        }
    }

    public List<Resource> getResources(final Property property) {
        List<Resource> result = new ArrayList<>();
        NodeIterator nodes = model.listObjectsOfProperty(property);
        while (nodes.hasNext()) {
            RDFNode literalNode = nodes.next();
            result.add(literalNode.asResource());
        }
        return result;
    }

    public void addRelated(final Resource subject, final List<String> relatedResources) {
        for (String relatedResource : relatedResources) {
            model.add(subject, rdfNodeFactory.getEdmIsRelatedTo(), ResourceFactory.createResource(relatedResource));
        }
    }

}
