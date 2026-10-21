package ru.otus.webserver;

import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.webserver.services.TemplateProcessor;
import ru.otus.webserver.services.TemplateProcessorImpl;

import org.hibernate.SessionFactory;

public class MainWebServer {

    private static final int PORT = 8080;
    private static final String DB_URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String DB_USER = "usr";
    private static final String DB_PASSWORD = "pwd";

    public static void main(String[] args) throws Exception {
        // Flyway + Hibernate
        MigrationsExecutorFlyway migrationsExecutorFlyway = new MigrationsExecutorFlyway(DB_URL, DB_USER, DB_PASSWORD);
        migrationsExecutorFlyway.executeMigrations();
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        // Services + Web server
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        DBServiceClient dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        TemplateProcessor templateProcessor = new TemplateProcessorImpl("/templates");
        ClientsWebServer webServer = new ClientsWebServer(PORT, templateProcessor, dbServiceClient);
        webServer.start();
        webServer.join();
    }
}
