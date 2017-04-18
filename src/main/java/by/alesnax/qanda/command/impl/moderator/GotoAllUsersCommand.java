package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class contains method that process taking list of all users and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoAllUsersCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoAllUsersCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String ALL_USERS = "all_users";

    /**
     * Keys of error messages attributes and page_no attribute that are located in config.properties file
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of returned commands or page that are located in config.properties file
     */
    private static final String ALL_USERS_PATH = "path.page.all_users";

    /**
     * method processes taking list of all users and returns it as attribute set into request. Access for users with role
     * ADMIN or MODERATOR. Method calls method from service layer, which returns list of posts
     * which is set as attribute into request and can throw ServiceException after which method returns value of error500 page.
     * Returns value of all_users page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of all_users page or error page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        try {
            ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
            int startUser = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startUser = (pageNo - 1) * USERS_PER_PAGE;
            }
            PaginatedList<Friend> allUsers = moderatorService.findAllUsers(startUser, USERS_PER_PAGE);
            request.setAttribute(ALL_USERS, allUsers);
            String allUsersPath = configurationManager.getProperty(ALL_USERS_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + allUsersPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
