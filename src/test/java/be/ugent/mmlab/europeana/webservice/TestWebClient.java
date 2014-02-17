package be.ugent.mmlab.europeana.webservice;

import be.ugent.mmlab.europeana.webservice.client.WebClient;
import org.junit.After;
import org.junit.Test;

import java.io.File;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class TestWebClient {

    private final String baseUri = "http://localhost:8080/";
    private WebClient client = new WebClient(baseUri);

    @After
    public void closeClient() {
        client.close();
    }

    @Test
    public void testSendOneRecord() {
        client.postRecord("Dit is een test", "ref007");
    }

    @Test
    public void testSendRecordFile() {
        client.postRecord(new File("/home/ghaesen/data/europeana/edm/paul_hankar.xml"), "ref007");
    }

    @Test
    public void testSendCompressedBigRecordFile() {
        client.postRecord(new File("/home/ghaesen/data/europeana/galileo/02301_Ag_IT_MG_catalogue.rdf.xz"), "ref006");
    }

}
