package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.*;

// static import

/**
 * Command has method that takes categories list from service layer , put it as an attribute to request and returns
 * value of categories page or error_page if exception will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoQuestCategoriesCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoQuestCategoriesCommand.class);

    /**
     * Keys of attributes in config.properties file, used for pagination and finding list of categories at jsp page
     */
    private static final String FULL_CATEGORIES_ATTR = "attr.request.full_categories";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Key of error attribute that is located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Key of returned page that is located in config.properties file
     */
    private static final String QUEST_CATEGORIES_PAGE = "path.page.categories";

    /**
     * method takes categories list from service layer , put it as an attribute to request and returns
     * value of categories page or error_page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of categories page, or error page if exception will be caught
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
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

            PaginatedList<Category> categories = postService.takeCategoriesList(startCategory, CATEGORIES_PER_PAGE);
            String fullCategoriesAttr = configurationManager.getProperty(FULL_CATEGORIES_ATTR);
            String questionCategoriesPath = configurationManager.getProperty(QUEST_CATEGORIES_PAGE);
            request.setAttribute(fullCategoriesAttr, categories);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + questionCategoriesPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}

