package be.ugent.mmlab.europeana.webservice.server;

import be.ugent.mmlab.europeana.enrichment.config.Config;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/12/14.
 */
public class WebServer {
    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // setup: http://news-anand.blogspot.be/2012/05/today-i-am-going-tell-you-how-to-create.html
        Server jetty = new Server(port);

       //// BEGIN dirty hackery to get multipart posts working in jetty. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=395000
       String[] configurationClasses = {
                "org.eclipse.jetty.webapp.WebInfConfiguration",
                "org.eclipse.jetty.webapp.WebXmlConfiguration",
                "org.eclipse.jetty.webapp.MetaInfConfiguration",
                "org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration",
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.webapp.TagLibConfiguration"
        };
        WebAppContext context = new WebAppContext();
        context.setConfigurationClasses(configurationClasses);
        //// END dirty hack

        String tmpDir = Config.getInstance().getTmpDir();
        context.setContextPath("/enrich/");
        context.setTempDirectory(new File(tmpDir, "jetty"));
        context.setResourceBase(tmpDir + "/jetty");

        // set gzip compression support.
        FilterHolder gzipFilter = new FilterHolder(new GzipFilter());
        gzipFilter.setInitParameter("methods", "GET,POST");
        gzipFilter.setInitParameter("mimeTypes", "text/html,text/plain,text/xml,application/xhtml+xml,text/css,application/javascript,image/svg+xml,application/rdf+xml,application/json");
        context.addFilter(gzipFilter, "/*", null);

        // servlets that handle enriching a single record
        context.addServlet(new ServletHolder(new OneRecordPhaseOneServlet()), "/record");
        context.addServlet(new ServletHolder(new OneRecordPhaseTwoServlet()), "/record/*");

        // servlets that handle enriching a lot of records
        context.addServlet(new ServletHolder(new BulkPhaseOneServlet()), "/bulk/*");

        jetty.setHandler(context);
        jetty.start();
        jetty.join();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "Print this help message.");
        options.addOption(OptionBuilder
                .withArgName("port number")
                .withLongOpt("port")
                .withDescription("The port the web server listens at.")
                .isRequired(true)
                .hasArg().create('p')
        );
        options.addOption(OptionBuilder
                .withArgName("configuration file")
                .withLongOpt("config")
                .withDescription("The configuration file.")
                .isRequired(true)
                .hasArg().create('c')
        );

        boolean printHelp = false;
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                printHelp = true;
                return;
            }
            int port = Integer.parseInt(commandLine.getOptionValue('p'));
            String configFile = commandLine.getOptionValue('c');

            Config.init(configFile);
            WebServer webService = new WebServer(port);
            webService.start();

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp = true;
        } finally {
            if (printHelp) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(WebServer.class.getSimpleName(), "Starts a web interface to the enrich service.", options, "");
            }
        }
    }

    private static void printHelp() {

    }
}
