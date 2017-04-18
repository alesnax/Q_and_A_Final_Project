package by.alesnax.qanda.controller;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.factory.CommandFactory;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.alesnax.qanda.constant.CommandConstants.*;

// static import

/**
 * Class-Servlet. This class process request methods (GET, POST) and sends response back.
 * This class extends HttpServlet and overrides doGet() and doPost() methods.
 * Both of them are processed in processRequest() method,
 * which checked and chooses implementation of Command, and forms response with new attributes,
 * definite address and type of sending methods(forward() or sendRedirect()).
 *
 * @author Aliaksandr Nakhankou
 * @see javax.servlet.http.HttpServlet
 */


@WebServlet(name = "Controller",
        urlPatterns = {"/Controller"},
        initParams = {
                @WebInitParam(name = "uploadFilesPath", value = "/img/")
        })

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

    /**
     * Process request methods (GET, POST) and sends response back.
     * Method checks and chooses implementation of Command which process request and forms response with new attributes,
     * definite address and type of sending methods(forward() or sendRedirect()).
     *
     * @param request
     *        Processed HttpServletRequest
     *
     * @param response
     *        Processed HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        CommandFactory client = new CommandFactory();
        Command command = client.defineCommand(request);
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