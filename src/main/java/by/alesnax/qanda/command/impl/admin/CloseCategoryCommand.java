package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.*;

// static import

/**
 * Changes category status to close. Access for command is only for users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */

public class CloseCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(CloseCategoryCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String CAT_ID = "category_id";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_MODERATED_CATEGORIES_COMMAND = "command.go_to_moderated_categories";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Process changing category status to 'closed'. Method checks if attribute user exists in session,
     * and it's role is ADMIN, calls method from service for processing changes,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.logQuery(request);

        try {
            AdminService adminService = ServiceFactory.getInstance().getAdminService();
            int catId = Integer.parseInt(request.getParameter(CAT_ID));
            adminService.closeCategory(catId);
            String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } catch (NumberFormatException | ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
