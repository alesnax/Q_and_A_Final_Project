package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Command has method that redirects user to go_to_complaints command and put attribute into session which
 * opens complaint processing block, access for command is only for users with ADMIN or MODERATOR role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoComplaintProcessCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoComplaintProcessCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID_ATTR = "post_id";
    private static final String USER_ID_ATTR = "user_id";
    private static final String PROCESSED_POST_ID_ATTR = "process_post_id";
    private static final String PROCESSED_USER_ID_ATTR = "process_author_id";

    /**
     * Keys of error or success messages attributes and page_no attributes that are located in config.properties file
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String PAGE_NO_QUERY_PART = "command.page_query_part";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_COMPLAINTS = "command.go_to_complaints";

    /**
     * Process redirecting to complaints.jsp and putting attribute into session which shows complaints processing block,
     * method checks if attribute user exists in session, and it's role is ADMIN or MODERATOR,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of redirection to go_to_complaints command if success scenario or authorization page or profile page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        try {
            String postId = request.getParameter(POST_ID_ATTR);
            String authorId = request.getParameter(USER_ID_ATTR);

            int pageNo = FIRST_PAGE_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
            }
            session.setAttribute(PROCESSED_POST_ID_ATTR, postId);
            session.setAttribute(PROCESSED_USER_ID_ATTR, authorId);
            String nextCommand = configurationManager.getProperty(GO_TO_COMPLAINTS);
            String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
        } catch (NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
