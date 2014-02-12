package be.ugent.mmlab.europeana.webservice;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/12/14.
 */
@SuppressWarnings("unused")
public class EnrichService extends Application {

    private Set<Object> services = new HashSet<>();

    public EnrichService() {
        services.add(new PhaseOneService());
    }

    @Override
    public Set<Object> getSingletons() {
        return services;
    }
}
