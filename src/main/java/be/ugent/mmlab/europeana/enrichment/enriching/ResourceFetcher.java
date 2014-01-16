package be.ugent.mmlab.europeana.enrichment.enriching;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/16/14.
 */
public class ResourceFetcher {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    private final CloseableHttpClient httpClient;

    public ResourceFetcher() {
        httpClient = HttpClients.createMinimal();
    }

    public String get(String uri) throws IOException {
        logger.debug("Fetching page at [{}]", uri);
        String body = "";
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (status == 200) {
                entity.writeTo(out);
                body = out.toString("utf-8");
            } else {
                logger.warn("Failed to fetch [{}]: {}", uri, response.getStatusLine().getReasonPhrase());
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null) response.close();
        }
        return body;
    }
}
