package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
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
 * Class contains method that processes finding best answers and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class FindBestAnswersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestAnswersCommand.class);

    /**
     * User attributes taking from session
     */
    private static final String USER = "user";

    /**
     * Keys of error messages, page number and best_answers attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String BEST_ANSWERS_ATTR = "attr.best_answers";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Key of page that is located in config.properties file
     */
    private static final String BEST_ANSWERS_PATH = "path.page.best_answers";

    /**
     * method processes finding best answers and returns it as attribute set into request.
     * Method calls method from service layer, which returns list of posts which is set as attribute into request
     * and can throw ServiceException after which method returns value of error500 page.
     * Returns value of best_answers page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of best_answers page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : DEFAULT_USER_ID;

        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            int startPost = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startPost = (pageNo - FIRST_PAGE_NO) * POSTS_PER_PAGE;
            }
            PaginatedList<Post> bestAnswers = postService.findBestAnswers(userId, startPost, POSTS_PER_PAGE);
            String bestAnswersAttr = configurationManager.getProperty(BEST_ANSWERS_ATTR);
            String bestAnswersPath = configurationManager.getProperty(BEST_ANSWERS_PATH);
            request.setAttribute(bestAnswersAttr, bestAnswers);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestAnswersPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}