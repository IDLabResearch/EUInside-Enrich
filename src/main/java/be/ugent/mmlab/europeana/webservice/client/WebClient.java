package be.ugent.mmlab.europeana.webservice.client;

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

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class WebClient {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final CloseableHttpClient client;
    private final String baseUri;

    private final ContentType rdfXmlContenType;

    public WebClient(final String baseUri) {
        this.baseUri = baseUri;
        client = HttpClients.createDefault();
        rdfXmlContenType = ContentType.create("application/rdf+xml", Consts.UTF_8);
    }

    public boolean postRecord(final String edmRecord, final String reference) {
        logger.debug("Posting record with reference {}: {}", reference, edmRecord);
        StringEntity entity = new StringEntity(edmRecord, rdfXmlContenType);  // the the record is expected to be rdf in xml format
        return postRecord(entity, reference);
    }

    public boolean postRecord(final File file, final String reference) {
        logger.debug("Posting record with reference {} from file {}", reference, file);
        FileEntity entity = new FileEntity(file, rdfXmlContenType);
        return postRecord(entity, reference);
    }

    private boolean postRecord(final HttpEntity entity, final String reference) {
        String uri = baseUri + "/record/" + reference;
        boolean success = false;
        final HttpPost post = new HttpPost(uri);
        post.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_NO_CONTENT) {
                logger.warn("Could not send record: error {}: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            } else {
                success = true;
            }
        } catch (IOException e) {
            logger.warn("Could not send record!", e);
        } finally {
            if (response != null) try {response.close();} catch (IOException e) {/* Oh, how I love boilerplate code! */}
        }

        return success;
    }

    public void close() {
        try {client.close();} catch (IOException e) {/* It's a lovely day. */}
    }

}
