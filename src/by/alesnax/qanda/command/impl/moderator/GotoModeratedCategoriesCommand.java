package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;

/**
 * Command has method that takes moderated categories list from service layer ,
 * put it as an attribute to request and returns value of categories page
 * or error_page if exception will be caught. Access for users with ADMIN or MODERATOR role
 * otherwise user will be redirected to authorization or profile page with error message
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoModeratedCategoriesCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoModeratedCategoriesCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    /**
     * Keys of attributes in config.properties file, used for pagination, showing categories list and error messages
     */
    private static final String FULL_CATEGORIES_ATTR = "attr.request.full_categories";
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String MODERATED_CATEGORIES_PAGE = "path.page.moderated_categories";

    /**
     * Process redirecting to moderated_categories.jsp and putting attribute into session which contains category list,
     * which was took from service layer. Method checks if attribute user exists in session,
     * and it's role is ADMIN or MODERATOR,
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
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case MODERATOR_ROLE:
                    try {
                        PostService postService = ServiceFactory.getInstance().getPostService();
                        int pageNo = FIRST_PAGE_NO;
                        int startCategory = START_ITEM_NO;
                        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                        if (request.getParameter(pageNoAttr) != null) {
                            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                            if (pageNo < FIRST_PAGE_NO) {
                                pageNo = FIRST_PAGE_NO;
                            }
                            startCategory = (pageNo - FIRST_PAGE_NO) * CATEGORIES_PER_PAGE;
                        }
                        PaginatedList<Category> moderatedCategories = postService.takeModeratedCategoriesList(userId, startCategory, CATEGORIES_PER_PAGE);
                        String moderatedCategoriesAttr = configurationManager.getProperty(FULL_CATEGORIES_ATTR);
                        String moderatedCategoriesPath = configurationManager.getProperty(MODERATED_CATEGORIES_PAGE);
                        request.setAttribute(moderatedCategoriesAttr, moderatedCategories);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + moderatedCategoriesPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case ADMIN_ROLE:
                    try {
                        PostService postService = ServiceFactory.getInstance().getPostService();
                        int pageNo = FIRST_PAGE_NO;
                        int startCategory = START_ITEM_NO;
                        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                        if (request.getParameter(pageNoAttr) != null) {
                            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                            if (pageNo < FIRST_PAGE_NO) {
                                pageNo = FIRST_PAGE_NO;
                            }
                            startCategory = (pageNo - FIRST_PAGE_NO) * CATEGORIES_PER_PAGE;
                        }
                        PaginatedList<Category> moderatedCategories = postService.takeCategoriesList(startCategory, CATEGORIES_PER_PAGE);
                        String moderatedCategoriesAttr = configurationManager.getProperty(FULL_CATEGORIES_ATTR);
                        String moderatedCategoriesPath = configurationManager.getProperty(MODERATED_CATEGORIES_PAGE);
                        request.setAttribute(moderatedCategoriesAttr, moderatedCategories);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + moderatedCategoriesPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case USER_ROLE:
                default:
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
            }
        }
        return page;
    }
}
