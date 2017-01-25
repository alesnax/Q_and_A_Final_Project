package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.CategoryInfo;
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

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 17.12.2016.
 */
public class GotoFirstPageCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFirstPageCommand.class);

    private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String GO_TO_CATEGORIES_COMMAND = "command.go_to_quest_categories";
    private static final String LOCALE = "locale";
    private static final String ACCEPT_LANG = "Accept-Language";
    private static final String RU_LANG = "ru";
    private static final String EN_LANG = "en";


    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(false);
        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            String locale = (String) session.getAttribute(LOCALE);
            if (locale == null || locale.isEmpty()) {
                String acceptLanguage = request.getHeader(ACCEPT_LANG);
                if (acceptLanguage.contains(RU_LANG)) {
                    session.setAttribute(LOCALE, RU_LANG);
                } else {
                    session.setAttribute(LOCALE, EN_LANG);
                }
            }
            List<CategoryInfo> categoriesInfo = postService.takeShortCategoriesList();
            String shortCategoriesAttr = configurationManager.getProperty(SHORT_CATEGORIES_ATTR);
            session.setAttribute(shortCategoriesAttr, categoriesInfo);

            String gotoCategoriesCommand = configurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoCategoriesCommand;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
