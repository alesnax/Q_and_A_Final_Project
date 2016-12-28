package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Category;
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
import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 13.12.2016.
 */


public class GotoQuestCategoriesCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoQuestCategoriesCommand.class);

    private static final String QUEST_CATEGORIES_PAGE = "path.page.categories";
    private static final String FULL_CATEGORIES_ATTR = "attr.request.full_categories";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            List<Category> categories = postService.takeCategoriesList();
            String fullCategoriesAttr = ConfigurationManager.getProperty(FULL_CATEGORIES_ATTR);
            String questionCategoriesPath = ConfigurationManager.getProperty(QUEST_CATEGORIES_PAGE);

           /* String showPagePoint = (String) session.getAttribute("show_page_point");
            if(showPagePoint != null && !showPagePoint.isEmpty()){
                questionCategoriesPath += showPagePoint;
                session.removeAttribute("show_page_point");
            }*/
            request.setAttribute(fullCategoriesAttr, categories);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + questionCategoriesPath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}

