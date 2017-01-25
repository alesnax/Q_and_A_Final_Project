package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Category;
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
import java.util.List;

// static import
import static by.alesnax.qanda.constant.CommandConstants.*;


/**
 * Created by alesnax on 13.12.2016.
 */


public class GotoQuestCategoriesCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoQuestCategoriesCommand.class);

    private static final String QUEST_CATEGORIES_PAGE = "path.page.categories";
    private static final String FULL_CATEGORIES_ATTR = "attr.request.full_categories";
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";


    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            int pageNo = 1;
            int startCategory = 0;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < 1) {
                    pageNo = 1;
                }
                startCategory = (pageNo - 1) * CATEGORIES_PER_PAGE;
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

