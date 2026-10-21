package ru.otus.webserver.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.webserver.services.TemplateProcessor;

import java.io.IOException;
import java.util.Collections;

public class IndexServlet extends HttpServlet {

    private static final String INDEX_PAGE_TEMPLATE = "index.html";
    private final TemplateProcessor templateProcessor;

    public IndexServlet(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String page = templateProcessor.getPage(INDEX_PAGE_TEMPLATE, Collections.emptyMap());
        resp.getWriter().println(page);
    }
}
