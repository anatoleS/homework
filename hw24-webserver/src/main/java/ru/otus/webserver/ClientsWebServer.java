package ru.otus.webserver;

import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.webserver.servlet.AuthorizationFilter;
import ru.otus.webserver.servlet.ClientsServlet;
import ru.otus.webserver.servlet.IndexServlet;
import ru.otus.webserver.servlet.LoginServlet;
import ru.otus.webserver.services.TemplateProcessor;

public class ClientsWebServer {

    private static final String LOGIN_PATH = "/login";
    private static final String CLIENTS_PATH = "/clients";

    private final TemplateProcessor templateProcessor;
    private final DBServiceClient dbServiceClient;
    private final Server server;
    private final AuthorizationFilter authorizationFilter = new AuthorizationFilter();

    public ClientsWebServer(int port, TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
        this.server = new Server(port);
    }

    public void start() throws Exception {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        server.start();
    }

    public void join() throws Exception {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private void initContext() {
        ServletContextHandler servletContextHandler = createServletContextHandler();

        server.setHandler(applySecurity(servletContextHandler, CLIENTS_PATH));
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        for (String path : paths) {
            servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null);
        }
        return servletContextHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new IndexServlet(templateProcessor)), "/");
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, new ru.otus.webserver.services.UserAuthServiceImpl())), LOGIN_PATH);
        servletContextHandler.addServlet(new ServletHolder(new ClientsServlet(templateProcessor, dbServiceClient)), CLIENTS_PATH);
        return servletContextHandler;
    }
}
