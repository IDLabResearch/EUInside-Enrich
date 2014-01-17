package be.ugent.mmlab.europeana.enrichment.misc;

import com.hp.hpl.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;

import static be.ugent.mmlab.europeana.enrichment.misc.Names.AGENT;
import static be.ugent.mmlab.europeana.enrichment.misc.Names.CREATOR;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/17/14.
 */
public class CommonModelOperations {
    private final Model model;
    private final Property todoProperty;
    private final Property sameAsProperty;
    private final Property typeProperty;
    private final Property creatorProperty;
    private final Literal trueLiteral;


    public CommonModelOperations(final Model model) {
        this.model = model;
        todoProperty = model.createProperty(Names.TODO.getUri());
        sameAsProperty = model.createProperty(Names.SAME_AS.getUri());
        typeProperty = model.createProperty(Names.TYPE.getUri());
        trueLiteral = model.createTypedLiteral(true);
        creatorProperty = model.createProperty(CREATOR.getUri());
    }

    public void addSameAs(final Resource resource, final String uri) {
        model.add(resource, sameAsProperty, model.createResource(uri));
    }

    public StmtIterator getSameAs(final Resource subject) {
        return ((ModelCon) model).listStatements(subject, sameAsProperty, null); // last parameter = null gives function overloading error without casting. Design bug in Jena?
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
        return model.listSubjectsWithProperty(todoProperty);
    }

    public void addTodo(final Resource subject) {
        model.add(subject, todoProperty, trueLiteral);
    }

    public void removeTodo(final Resource subject) {
        model.remove(subject, todoProperty, trueLiteral);  // null as last parameter also crashes?? again a design bug in Jena??
    }

    public String getType(final Resource subject) {
        String type;
        StmtIterator typeStmt = ((ModelCon) model).listStatements(subject, typeProperty, null);
        if (typeStmt.hasNext()) {
            Statement statement = typeStmt.nextStatement();
            type = statement.getObject().toString();
        } else {
            type = null;
        }
        return type;
    }

    public boolean hasType(final Resource subject) {
        return model.contains(subject, typeProperty);
    }

    public void addAgentType(final Resource agent) {
        model.add(agent, typeProperty, model.createResource(AGENT.toString()));
    }

    public void addCreator(final String subjectUri, final Resource creator) {
        Resource subject = model.createResource(subjectUri);
        model.add(subject, creatorProperty, creator);
    }

    public void addCreatorToRemove(final String subjectUri, final String creatorName) {
        Resource subject = model.createResource(subjectUri);
        model.add(subject, creatorProperty, model.createLiteral(creatorName));
    }

}
