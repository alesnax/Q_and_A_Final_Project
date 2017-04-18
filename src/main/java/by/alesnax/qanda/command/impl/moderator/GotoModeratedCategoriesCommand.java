package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

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
    private static final String MODERATOR_ROLE = "moderator";


    /**
     * Keys of attributes in config.properties file, used for pagination, showing categories list and error messages
     */
    private static final String FULL_CATEGORIES_ATTR = "attr.request.full_categories";
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of commands that are located in config.properties file
     */
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

        String role = user.getRole().getValue();
        int userId = user.getId();

        PaginatedList<Category> moderatedCategories;
        try {
            int startCategory = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startCategory = (pageNo - FIRST_PAGE_NO) * CATEGORIES_PER_PAGE;
            }

            if (MODERATOR_ROLE.equals(role)) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                moderatedCategories = postService.takeModeratedCategoriesList(userId, startCategory, CATEGORIES_PER_PAGE);
            } else {
                PostService postService = ServiceFactory.getInstance().getPostService();
                moderatedCategories = postService.takeCategoriesList(startCategory, CATEGORIES_PER_PAGE);
            }

            String moderatedCategoriesAttr = configurationManager.getProperty(FULL_CATEGORIES_ATTR);
            request.setAttribute(moderatedCategoriesAttr, moderatedCategories);
            String moderatedCategoriesPath = configurationManager.getProperty(MODERATED_CATEGORIES_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + moderatedCategoriesPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
