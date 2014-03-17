package be.ugent.mmlab.europeana.webservice.server;

import be.ugent.mmlab.europeana.enrichment2.bulk.BulkEnrichService;
import be.ugent.mmlab.europeana.enrichment2.bulk.BulkEnrichServiceImpl;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 3/11/14.
 */
@MultipartConfig
public class BulkPhaseOneServlet extends HttpServlet {
    private final Random r = new Random();
    private final BulkEnrichService bulkEnrichService = BulkEnrichServiceImpl.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getContentType().startsWith("application/gzip")) {
            String reference = process(request.getInputStream());
            IOUtils.write(reference, response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Only application/gzip content is accepted");
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

    }

    private String process(InputStream inputStream) throws IOException {

        String timestamp = Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
        String rPart = Integer.toString(r.nextInt(), Character.MAX_RADIX);
        String reference = timestamp + rPart;
        File rdfFile = new File(System.getProperty("java.io.tmpdir"), reference + ".rdf.gz");
        IOUtils.copy(inputStream, new FileOutputStream(rdfFile));
        bulkEnrichService.phaseOne(reference);
        return reference;
    }
}
