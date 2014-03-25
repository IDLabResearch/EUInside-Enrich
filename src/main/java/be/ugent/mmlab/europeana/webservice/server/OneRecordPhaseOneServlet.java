package be.ugent.mmlab.europeana.webservice.server;

import be.ugent.mmlab.europeana.enrichment2.oneRecord.EnrichService;
import be.ugent.mmlab.europeana.enrichment2.oneRecord.EnrichServiceFactory;
import be.ugent.mmlab.europeana.enrichment2.oneRecord.PhaseOneResult;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.MimeTypes;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/24/14.
 */
@MultipartConfig
public class OneRecordPhaseOneServlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(OneRecordPhaseOneServlet.class);

    private final EnrichService enrichService = EnrichServiceFactory.create();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(MimeTypes.Type.TEXT_HTML_UTF_8.asString());
        response.setStatus(HttpServletResponse.SC_OK);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/postRecordForm.html")))) {
            IOUtils.copy(in, response.getOutputStream());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contentType = request.getContentType();
        try {
            if (contentType.startsWith("application/x-www-form-urlencoded")) {
                processForm(request, response);
            } else if (contentType.startsWith("application/rdf+xml")) {
                processPost(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Only application/x-www-form-urlencoded of application/rdf+xml is supported at this moment");
            }
        } catch (Throwable t) {
            logger.error("Could not process record", t);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    private void processForm(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String record = request.getParameter("record");
        if (record != null) {
            PhaseOneResult possibleCandidates = processRecord(record);

            // now offer choices to the browser (= make form)
            try (PrintWriter writer = response.getWriter()) {
                String prefix = "<!DOCTYPE html><html>\n" +
                        "<head>\n" +
                        "    <title>Enrich record</title>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "</head>\n" +
                        "\n" +
                        "<body><form action=\"/enrich/record/";
                writer.println(prefix);
                writer.print(possibleCandidates.getReference());
                writer.print("\" method=\"post\">");

                int counter = 0;
                for (Map.Entry<String, List<String>> stringListEntry : possibleCandidates.getObjectToPossibleURIs().entrySet()) {
                    writer.println("<fieldset>");
                    writer.println("<legend>" + stringListEntry.getKey() + "</legend>");
                    for (String possibleCandidate : stringListEntry.getValue()) {
                        writer.println("<input id=\"" + counter + "\" type=\"radio\" name=\"" + stringListEntry.getKey() + "\" value=\"" + possibleCandidate + "\"/>");
                        writer.println("<label for=\"" + counter + "\"><a href=\"" + possibleCandidate + "\" target=\"_blank\">" + possibleCandidate + "</a></label><br/>");
                        counter++;
                    }
                    writer.println("</fieldset>");
                }
                writer.println("<input type=\"submit\"/></form></body></html>");
            }
        }
    }

    private void processPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setCharacterEncoding("UTF-8");

        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(request.getInputStream(), stringWriter);
        String record = stringWriter.toString();

        try (PrintWriter out = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            if (record.length() > 1) {
                PhaseOneResult enrichedRecord = processRecord(record);
                String json = gson.toJson(enrichedRecord);
                out.print(json);
            }
        }
    }

    private PhaseOneResult processRecord(final String record) throws IOException {
        return enrichService.phaseOne(record);
    }
}
