package be.ugent.mmlab.europeana.webservice.client;

import be.ugent.mmlab.europeana.enrichment2.oneRecord.PhaseOneResult;
import com.google.gson.Gson;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class WebClient {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final String baseUri;

    private final ContentType rdfXmlContenType;

    private final Gson gson = new Gson();

    public WebClient(final String baseUri) {
        this.baseUri = baseUri;
        rdfXmlContenType = ContentType.create("application/rdf+xml", Consts.UTF_8);
    }

    public PhaseOneResult postOneRecordPhaseOne(final String edmRecord) throws IOException {
        logger.debug("Posting record: {}", edmRecord);
        String response = Request.Post(baseUri + "/record")
                .bodyString(edmRecord, rdfXmlContenType)
                .execute().returnContent().asString();
        return gson.fromJson(response, PhaseOneResult.class);
    }

    public String postOneRecordPhaseTwo(final long reference, final Map<String, String> objectToURIMap) throws IOException {
        if (objectToURIMap != null && !objectToURIMap.isEmpty()) {
            String json = gson.toJson(objectToURIMap);
            return Request.Post(baseUri + "/record/" + reference)
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();
        } else {
            return null;
        }
    }
}
