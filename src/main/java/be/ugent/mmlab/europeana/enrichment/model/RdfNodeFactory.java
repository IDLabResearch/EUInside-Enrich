package be.ugent.mmlab.europeana.enrichment.model;

import com.hp.hpl.jena.rdf.model.*;

import static be.ugent.mmlab.europeana.enrichment.model.Names.*;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/20/14.
 */
public class RdfNodeFactory {
    private final Property todoProperty;
    private final Property sameAsProperty;
    private final Property typeProperty;
    private final Property creatorProperty;
    private final Property birthDateProperty;
    private final Property dateOfBirthProperty;
    private final Property deathDateProperty;
    private final Property dateOfDeathProperty;
    private final Property beginProperty;
    private final Property endProperty;


    private final Literal trueLiteral;
    private final Resource agentResource;

    private static RdfNodeFactory instance;

    private RdfNodeFactory() {
        final Model dummyModel = ModelFactory.createDefaultModel();
        todoProperty = dummyModel.createProperty(TODO.getUri());
        sameAsProperty = dummyModel.createProperty(SAME_AS.getUri());
        typeProperty = dummyModel.createProperty(TYPE.getUri());
        trueLiteral = dummyModel.createTypedLiteral(true);
        creatorProperty = dummyModel.createProperty(CREATOR.getUri());
        agentResource = dummyModel.createResource(AGENT.toString());

        birthDateProperty = dummyModel.createProperty(DBP_BIRTH_DATE.getUri());
        dateOfBirthProperty = dummyModel.createProperty(DBP_DATE_OF_BIRTH.getUri());
        deathDateProperty = dummyModel.createProperty(DBP_DEATH_DATE.getUri());
        dateOfDeathProperty = dummyModel.createProperty(DBP_DATE_OF_DEATH.getUri());
        beginProperty = dummyModel.createProperty(EDM_BEGIN.getUri());
        endProperty = dummyModel.createProperty(EDM_END.getUri());
    }

    public static RdfNodeFactory getInstance(){
        if (instance == null) {
            instance = new RdfNodeFactory();
        }
        return instance;
    }

    public Property getTodoProperty() {
        return todoProperty;
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

    public Literal getTrueLiteral() {
        return trueLiteral;
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
}
