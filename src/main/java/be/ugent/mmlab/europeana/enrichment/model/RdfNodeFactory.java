package be.ugent.mmlab.europeana.enrichment.model;

import com.hp.hpl.jena.rdf.model.*;

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


    private final Literal todoLiteral;
    private final Resource agentResource;

    private static RdfNodeFactory instance;

    private RdfNodeFactory() {
        final Model dummyModel = ModelFactory.createDefaultModel();
        commentProperty = dummyModel.createProperty(COMMENT.getUri());
        sameAsProperty = dummyModel.createProperty(SAME_AS.getUri());
        typeProperty = dummyModel.createProperty(TYPE.getUri());
        todoLiteral = dummyModel.createTypedLiteral("__TODO__");
        creatorProperty = dummyModel.createProperty(CREATOR.getUri());
        agentResource = dummyModel.createResource(AGENT.getUri());

        birthDateProperty = dummyModel.createProperty(DBP_BIRTH_DATE.getUri());
        dateOfBirthProperty = dummyModel.createProperty(DBP_DATE_OF_BIRTH.getUri());
        deathDateProperty = dummyModel.createProperty(DBP_DEATH_DATE.getUri());
        dateOfDeathProperty = dummyModel.createProperty(DBP_DATE_OF_DEATH.getUri());
        beginProperty = dummyModel.createProperty(EDM_BEGIN.getUri());
        endProperty = dummyModel.createProperty(EDM_END.getUri());
        dbpRedirectProperty = dummyModel.createProperty(DBP_REDIRECT.getUri());
        rdfsLabelProperty = dummyModel.createProperty(RDFS_LABEL.getUri());
        prefLabelProperty = dummyModel.createProperty(PREF_LABEL.getUri());
        skosNoteProperty = dummyModel.createProperty(SKOS_NOTE.getUri());
        dbpAbstractPropery = dummyModel.createProperty(DBP_ABSTRACT.getUri());

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
}
