package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

//static import

/**
 * Command has method that redirects user to previous command that helps to remove
 * attributes on jsp page that could open any block of codes for correcting, adding data etc.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoCurrentPageCommand implements Command {

    /**
     * method redirects user to previous command that helps to remove
     * attributes on jsp page that could open any block of codes for correcting, adding data etc.
     *
     * @param request Processed HttpServletRequest
     * @return value of previous request query saved in session
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        QueryUtil.logQuery(request);
        String previousQuery = QueryUtil.getPreviousQuery(request);
        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        return page;
    }
}
