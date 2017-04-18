package by.alesnax.qanda.command;

import javax.servlet.http.HttpServletRequest;

/**
 * Command contains all data and actions to process definite operation
 *
 * @author alesnax
 * @see HttpServletRequest
 */

public interface Command {

    /**
     * Executes definite operation with data parsed from request and put processed data and messages back into request
     * and returns value of page where request will be send.
     * @param request
     * @return value of page and type of method(forward(), sendRedirect() or sendRedirect() to errorPage)
     * where request will be send.
     */
    String execute(HttpServletRequest request);
}
