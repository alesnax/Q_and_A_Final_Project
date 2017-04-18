package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Command has method that redirects user to default command and put list of short info about categories
 * as an attribute of session, that used into choice of categories while adding new question.
 * Also method checks header for regional locale, if it's contain info about russian language,
 * russian locale will be set, if value of locale already exists it won't be changed.
 * Method is called from index.jsp page.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoFirstPageCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFirstPageCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String LOCALE = "locale";
    private static final String ACCEPT_LANG = "Accept-Language";
    private static final String RU_LANG = "ru";
    private static final String EN_LANG = "en";

    /**
     * Keys of error message and category_info attributes that are located in config.properties file
     */
    private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Key of command that is located in config.properties file
     */
    private static final String WELCOME_PAGE_PATH = "path.page.welcome";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * method redirects user to default command and put list of short info about categories
     * as an attribute of session, that used into choice of categories while adding new question.
     * Also method checks header for regional locale, if it's contain info about russian language,
     * russian locale will be set, if value of locale already exists it won't be changed.
     * Method is called from index.jsp page. Can be redirected to error500 page if ServiceError will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of go_to_categories command or error page if exception will be caught
     */
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
                if (acceptLanguage != null && acceptLanguage.contains(RU_LANG)) {
                    session.setAttribute(LOCALE, RU_LANG);
                } else {
                    session.setAttribute(LOCALE, EN_LANG);
                }
            }
            List<CategoryInfo> categoriesInfo = postService.takeShortCategoriesList();
            String shortCategoriesAttr = configurationManager.getProperty(SHORT_CATEGORIES_ATTR);
            session.setAttribute(shortCategoriesAttr, categoriesInfo);
            User user = (User) session.getAttribute(USER);
            if(user == null){
                String welcomePagePath = configurationManager.getProperty(WELCOME_PAGE_PATH);
                page = REQUEST_TYPE+ TYPE_PAGE_DELIMITER + welcomePagePath;
            } else {
                String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
            }
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
