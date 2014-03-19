package be.ugent.mmlab.europeana.enrichment.model;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import static be.ugent.mmlab.europeana.enrichment.model.Names.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/20/14.
 */
public class RdfNodeFactory {
    private final Property commentProperty;
    private final Property sameAsProperty;
    private final Property typeProperty;
    private final Property creatorProperty;
    private final Property birthDateProperty;
    private final Property dateOfBirthProperty;
    private final Property deathDateProperty;
    private final Property dateOfDeathProperty;
    private final Property beginProperty;
    private final Property endProperty;
    private final Property dbpRedirectProperty;
    private final Property rdfsLabelProperty;
    private final Property prefLabelProperty;
    private final Property skosNoteProperty;
    private final Property dbpAbstractPropery;
    private final Property dctermsSubject;
    private final Property edmIsRelatedTo;
    private final Property rdfsCommentProperty;


    private final Literal todoLiteral;
    private final Resource agentResource;

    private static RdfNodeFactory instance;

    private RdfNodeFactory() {
        commentProperty = ResourceFactory.createProperty(COMMENT.getUri());
        sameAsProperty = ResourceFactory.createProperty(SAME_AS.getUri());
        typeProperty = ResourceFactory.createProperty(TYPE.getUri());
        todoLiteral = ResourceFactory.createTypedLiteral("__TODO__");
        creatorProperty = ResourceFactory.createProperty(CREATOR.getUri());
        agentResource = ResourceFactory.createResource(AGENT.getUri());

        birthDateProperty = ResourceFactory.createProperty(DBP_BIRTH_DATE.getUri());
        dateOfBirthProperty = ResourceFactory.createProperty(DBP_DATE_OF_BIRTH.getUri());
        deathDateProperty = ResourceFactory.createProperty(DBP_DEATH_DATE.getUri());
        dateOfDeathProperty = ResourceFactory.createProperty(DBP_DATE_OF_DEATH.getUri());
        beginProperty = ResourceFactory.createProperty(EDM_BEGIN.getUri());
        endProperty = ResourceFactory.createProperty(EDM_END.getUri());
        dbpRedirectProperty = ResourceFactory.createProperty(DBP_REDIRECT.getUri());
        rdfsLabelProperty = ResourceFactory.createProperty(RDFS_LABEL.getUri());
        prefLabelProperty = ResourceFactory.createProperty(PREF_LABEL.getUri());
        rdfsCommentProperty = ResourceFactory.createProperty(RDFS_COMMENT.getUri());
        skosNoteProperty = ResourceFactory.createProperty(SKOS_NOTE.getUri());
        dbpAbstractPropery = ResourceFactory.createProperty(DBP_ABSTRACT.getUri());
        dctermsSubject = ResourceFactory.createProperty(DCTERMS_SUBJECT.getUri());
        edmIsRelatedTo = ResourceFactory.createProperty(EDM_IS_RELATED_TO.getUri());

    }

    public static RdfNodeFactory getInstance(){
        if (instance == null) {
            instance = new RdfNodeFactory();
        }
        return instance;
    }

    public Property getCommentProperty() {
        return commentProperty;
    }

    public Property getSameAsProperty() {
        return sameAsProperty;
    }

    public Property getTypeProperty() {
        return typeProperty;
    }

    public Property getCreatorProperty() {
        return creatorProperty;
    }

    public Literal getTodoLiteral() {
        return todoLiteral;
    }

    public Resource getAgentResource() {
        return agentResource;
    }

    public Property getBirthDateProperty() {
        return birthDateProperty;
    }

    public Property getDeathDateProperty() {
        return deathDateProperty;
    }

    public Property getBeginProperty() {
        return beginProperty;
    }

    public Property getEndProperty() {
        return endProperty;
    }

    public Property getDateOfBirthProperty() {
        return dateOfBirthProperty;
    }

    public Property getDateOfDeathProperty() {
        return dateOfDeathProperty;
    }

    public Property getDbpRedirectProperty() {
        return dbpRedirectProperty;
    }

    public Property getRdfsLabelProperty() {
        return rdfsLabelProperty;
    }

    public Property getPrefLabelProperty() {
        return prefLabelProperty;
    }

    public Property getSkosNoteProperty() {
        return skosNoteProperty;
    }

    public Property getDbpAbstractPropery() {
        return dbpAbstractPropery;
    }

    public Property getDctermsSubject() {
        return dctermsSubject;
    }

    public Property getEdmIsRelatedTo() {
        return edmIsRelatedTo;
    }

    public Property getRdfsCommentProperty() {
        return rdfsCommentProperty;
    }
}
