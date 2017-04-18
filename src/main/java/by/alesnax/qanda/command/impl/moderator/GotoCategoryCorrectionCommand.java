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
 * Command has method that redirects user to go_to_moderated_categories command and put attribute into session which
 * opens category correction block, access for command is only for users with ADMIN or MODERATOR role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoCategoryCorrectionCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoCategoryCorrectionCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String CAT_ID = "category_id";

    /**
     * Keys of attributes in config.properties file, used for pagination and opening category correction block
     */
    private static final String SHOW_CATEGORY_CORRECTION_ATTR = "attr.show_category_correction";
    private static final String PAGE_NO = "attr.page_no";
    private static final String PAGE_NO_QUERY_PART = "command.page_query_part";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_MODERATED_CATEGORIES = "command.go_to_moderated_categories";

    /**
     * Process redirecting to moderated_categories.jsp and putting attribute into session which shows category correction block,
     * method checks if attribute user exists in session, and it's role is ADMIN or MODERATOR,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to moderated_categories page if success scenario or error or authorization page or profile page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);
        try {
            int pageNo = FIRST_PAGE_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
            }
            String catId = request.getParameter(CAT_ID);
            String showCategoryCorrectionAttr = configurationManager.getProperty(SHOW_CATEGORY_CORRECTION_ATTR);
            session.setAttribute(showCategoryCorrectionAttr, catId);
            String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);
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
