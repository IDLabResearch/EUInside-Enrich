package be.ugent.mmlab.europeana.enrichment.enriching;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 1/16/14.
 */
public class ResourceFetcher {

    public String get(final String uri) throws URISyntaxException, IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader response = new BufferedReader(new InputStreamReader(new URL(uri).openStream()))) {
            String line;
            while ((line = response.readLine()) != null) {
                body.append(line).append('\n');
            }
        }
        return body.toString();
    }
}
