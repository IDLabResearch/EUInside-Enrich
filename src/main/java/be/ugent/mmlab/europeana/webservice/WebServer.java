package be.ugent.mmlab.europeana.webservice;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

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

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(new HttpServletDispatcher());
        servletHolder.setInitParameter("javax.ws.rs.Application", "be.ugent.mmlab.europeana.webservice.EnrichService");
        context.addServlet(servletHolder, "/*");

        jetty.setHandler(context);

        jetty.start();
        jetty.join();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;    // TODO argument
        WebServer webService = new WebServer(port);
        webService.start();
    }
}
