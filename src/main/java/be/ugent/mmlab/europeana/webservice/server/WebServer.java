package be.ugent.mmlab.europeana.webservice.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
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
        context.setContextPath("/enrich/");
        context.setTempDirectory(new File("/tmp/jetty"));
        context.setResourceBase("/tmp/jetty");
        context.addServlet(new ServletHolder(new OneRecordPhaseOneServlet()), "/record");
        context.addServlet(new ServletHolder(new OneRecordPhaseTwoServlet()), "/record/*");
        //// END dirty hack

        jetty.setHandler(context);
        jetty.start();
        jetty.join();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;    // TODO argument
        WebServer webService = new WebServer(port);
        webService.start();

        /*Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Shutting down record handler...");
                RecordHandler recordHandler = RecordHandlerFactory.create();
                recordHandler.close();
            }
        }));*/
    }
}
