package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class contains method that process finding best users and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class FindBestUsersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestUsersCommand.class);

    /**
     * Keys of error messages, page number and best_users attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String PAGE_NO = "attr.page_no";
    private static final String BEST_USERS_ATTR = "attr.best_users";

    /**
     * Key of page that is located in config.properties file
     */
    private static final String BEST_USERS_PATH = "path.page.best_users";

    /**
     * method processes finding best users and returns it as attribute set into request.
     * Method calls method from service layer, which returns list of posts which is set as attribute into request
     * and can throw ServiceException after which method returns value of error500 page.
     * Returns value of best_users page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of best_users page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        UserService userService = ServiceFactory.getInstance().getUserService();
        try {
            int startUser = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startUser = (pageNo - FIRST_PAGE_NO) * USERS_PER_PAGE;
            }
            PaginatedList<Friend> bestUsers = userService.findBestUsers(startUser, USERS_PER_PAGE);
            String bestUsersAttr = configurationManager.getProperty(BEST_USERS_ATTR);
            request.setAttribute(bestUsersAttr, bestUsers);
            String bestUsersPath = configurationManager.getProperty(BEST_USERS_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestUsersPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}