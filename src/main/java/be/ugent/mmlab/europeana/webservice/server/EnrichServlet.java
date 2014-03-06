package be.ugent.mmlab.europeana.webservice.server;

import be.ugent.mmlab.europeana.enrichment.auto.RecordHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/13/14.
 */
@MultipartConfig
public class EnrichServlet extends HttpServlet {
    private final RecordHandler recordHandler = new RecordHandler();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String contentType = req.getContentType();

        if (path.startsWith("/enrich/")) {
            if (contentType.startsWith("application/rdf+xml")) {
                // get reference
                String[] pathParts = path.split("/");
                if (pathParts.length == 3) {
                    String reference = pathParts[2];
                    try (BufferedReader reader = req.getReader()) {
                        addRecords(reference, reader);
                    } catch (IOException e) {
                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reference missing; use an URI like http://<host:port>/enrich/<reference>");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Only application/rdf+xml is supported at this moment");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Try an url like http://<host:port>/enrich/<reference>");
        }

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo().startsWith("/enrich")) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void addRecords(final String reference, final BufferedReader reader) throws IOException {
        StringBuilder str = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            str.append(line).append('\n');
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1);
        }
        String record = str.toString();
        recordHandler.addRecord(reference, record);

    }

}
