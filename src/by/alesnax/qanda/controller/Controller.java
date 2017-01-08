package by.alesnax.qanda.controller;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.factory.CommandFactory;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

@WebServlet(name = "Controller",
        urlPatterns = {"/Controller"})

public class Controller extends HttpServlet {
    private static Logger logger = LogManager.getLogger(Controller.class);

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
        String page = null;

        CommandFactory client = new CommandFactory();
        Command command = client.defineCommand(request, response);
        page = command.execute(request);
        if (page != null) {
            String[] typePage = page.split(TYPE_PAGE_DELIMITER);
            if (typePage[0].equals(REQUEST_TYPE)) {
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(typePage[1]);
                dispatcher.forward(request, response);
            } else if (typePage[0].equals(RESPONSE_TYPE)) {
                response.sendRedirect(request.getContextPath() + typePage[1]);
            } else {
                String errorPage = ConfigurationManager.getProperty(ERROR_PAGE_500);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(errorPage);
                dispatcher.forward(request, response);
            }
        } else {
            /*try {*/
            page = ConfigurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
            String nullPageAttr = ConfigurationManager.getProperty(NULL_PAGE_ATTR);
            request.getSession().setAttribute(nullPageAttr, ERROR_MESSAGE_NULL_PAGE);
            response.sendRedirect(request.getContextPath() + page);
           /* } catch (MissingResourceException e) {
                logger.log(Level.FATAL, e);
                throw new RuntimeException(e);
            }*/
        }
    }
}