package by.alesnax.qanda.controller;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.factory.CommandFactory;
import by.alesnax.qanda.resource.ConfigurationManager;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

@WebServlet(name = "Controller",
        urlPatterns = {"/Controller"})

public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String GO_TO_CATEGORIES_COMMAND = "command.go_to_quest_categories";
    private static final String NULL_PAGE_ATTR = "attr.null_page";
    private static final String ERROR_MESSAGE_NULL_PAGE = "error.error_msg.null_page";

    private static final String ERROR_PAGE_500 = "path.page.error500";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        CommandFactory client = new CommandFactory();
        Command command = client.defineCommand(request, response);
        page = command.execute(request);
        if (page != null) {
            String[] typePage = page.split(TYPE_PAGE_DELIMITER);
            switch (typePage[0]) {
                case REQUEST_TYPE: {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(typePage[1]);
                    dispatcher.forward(request, response);
                    break;
                }
                case RESPONSE_TYPE:
                    response.sendRedirect(request.getContextPath() + typePage[1]);
                    break;
                default: {
                    String errorPage = configurationManager.getProperty(ERROR_PAGE_500);
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(errorPage);
                    dispatcher.forward(request, response);
                    break;
                }
            }
        } else {
            page = configurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
            String nullPageAttr = configurationManager.getProperty(NULL_PAGE_ATTR);
            request.getSession().setAttribute(nullPageAttr, ERROR_MESSAGE_NULL_PAGE);
            response.sendRedirect(request.getContextPath() + page);
        }
    }
}