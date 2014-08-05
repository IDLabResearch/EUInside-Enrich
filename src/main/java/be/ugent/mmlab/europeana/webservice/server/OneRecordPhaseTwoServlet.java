package be.ugent.mmlab.europeana.webservice.server;

import be.ugent.mmlab.europeana.enrichment.oneRecord.EnrichService;
import be.ugent.mmlab.europeana.enrichment.oneRecord.EnrichServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/28/14.
 */
@MultipartConfig
public class OneRecordPhaseTwoServlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(OneRecordPhaseOneServlet.class);
    private final EnrichService enrichService = EnrichServiceFactory.create();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String referenceStr = request.getPathInfo().substring(1);
        if (!referenceStr.isEmpty()) {
            String model = enrichService.getFromCache(Long.parseLong(referenceStr));
            if (model != null) {
                response.setContentType("application/rdf+xml");
                response.setCharacterEncoding("UTF-8");
                try(PrintWriter out = response.getWriter()) {
                    out.print(model);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not cached (anymore)");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reference expected after last '/'");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contentType = request.getContentType();
        try {
            if (contentType.startsWith("application/x-www-form-urlencoded")) {
                processForm(request, response);
            } else if (contentType.startsWith("application/json")) {
                processJson(request, response);
            }
        } catch (Throwable t) {
            logger.error("Could not process record", t);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void processForm(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        // get reference
        String referenceStr = request.getPathInfo().substring(1);

        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, String> subjectToURI = new HashMap<>();
        for (Map.Entry<String, String[]> stringEntry : parameters.entrySet()) {
            subjectToURI.put(stringEntry.getKey(), stringEntry.getValue()[0]);
        }

        process(referenceStr, subjectToURI, response);
    }

    private void processJson(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String referenceStr = request.getPathInfo().substring(1);
        String choices = IOUtils.toString(request.getInputStream(), "UTF-8");
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
        Map<String, String> subjectToURI = gson.fromJson(choices, typeOfHashMap);
        process(referenceStr, subjectToURI, response);
    }

    private void process(final String referenceStr, final Map<String, String> subjectToURI, final HttpServletResponse response) throws IOException {
        String enrichedRecord = enrichService.phaseTwo(Long.parseLong(referenceStr), subjectToURI);

        response.setContentType("application/rdf+xml");
        response.setCharacterEncoding("UTF-8");

		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
			out.append(enrichedRecord);
			out.flush();
		}
    }
}
