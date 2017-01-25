package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 13.12.2016.
 *
 */
public class FindBestUsersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestUsersCommand.class);

    private static final String BEST_USERS_ATTR = "attr.best_users";
    private static final String BEST_USERS_PATH = "path.page.best_users";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String PAGE_NO = "attr.page_no";


    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        UserService userService = ServiceFactory.getInstance().getUserService();
        try {
            int pageNo = 1;
            int startUser = 0;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < 1) {
                    pageNo = 1;
                }
                startUser = (pageNo - 1) * USERS_PER_PAGE;
            }
            PaginatedList<Friend> bestUsers = userService.findBestUsers(startUser, USERS_PER_PAGE);
            String bestUsersAttr = configurationManager.getProperty(BEST_USERS_ATTR);
            request.setAttribute(bestUsersAttr, bestUsers);
            String bestUsersPath = configurationManager.getProperty(BEST_USERS_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestUsersPath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}