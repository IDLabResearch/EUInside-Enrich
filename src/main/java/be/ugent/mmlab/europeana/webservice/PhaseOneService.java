package be.ugent.mmlab.europeana.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/12/14.
 */
@Path("/")
public class PhaseOneService {

    @GET
    @Produces("text/html")
    @SuppressWarnings("unused")
    public Response index(/*@PathParam("input") final String input*/) {
        return Response.ok("boe").build();
    }

    // just to test
    @GET
    @Path("/hello")
    @SuppressWarnings("unused")
    public Response  helloGet() {
        return Response.status(200).entity("HTTP GET method called").build();
    }

}
