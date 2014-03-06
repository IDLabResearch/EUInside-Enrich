package be.ugent.mmlab.europeana.webservice.client;

import be.ugent.mmlab.europeana.enrichment2.PhaseOneResult;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class WebClient {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final CloseableHttpClient client;
    private final String baseUri;

    private final ContentType rdfXmlContenType;

    private final Gson gson = new Gson();

    public WebClient(final String baseUri) {
        this.baseUri = baseUri;
        client = HttpClients.createDefault();
        rdfXmlContenType = ContentType.create("application/rdf+xml", Consts.UTF_8);
    }

    public PhaseOneResult postOneRecordPhaseOne(final String edmRecord) {
        logger.debug("Posting record: {}", edmRecord);
        StringEntity entity = new StringEntity(edmRecord, rdfXmlContenType);  // the the record is expected to be rdf in xml format
        String response = postOneRecord(entity, "/record");
        return gson.fromJson(response, PhaseOneResult.class);
    }

    public String postOneRecordPhaseOne(final File file) {
        logger.debug("Posting record from file {}", file);
        FileEntity entity = new FileEntity(file, rdfXmlContenType);
        return postOneRecord(entity, "/record");
    }

    public String postOneRecordPhaseTwo(final long reference, final Map<String, String> objectToURIMap) {
        if (objectToURIMap != null && !objectToURIMap.isEmpty()) {
            String json = gson.toJson(objectToURIMap);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            return postOneRecord(entity, "/record/" + reference);
        } else {
            return null;
        }
    }

    private String postOneRecord(final HttpEntity entity, final String relativeUri) {
        String uri = baseUri + relativeUri;
        final HttpPost post = new HttpPost(uri);
        post.setEntity(entity);

        String result = null;

        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                logger.warn("Could not send record: error {}: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            } else {
                StringWriter stringWriter = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), stringWriter);
                result = stringWriter.toString();
            }
        } catch (IOException e) {
            logger.warn("Could not send record!", e);
        } finally {
            if (response != null) try {response.close();} catch (IOException e) {/* Oh, how I love boilerplate code! */}
        }

        return result;
    }

    public void close() {
        try {client.close();} catch (IOException e) {/* It's a lovely day. */}
    }

}
