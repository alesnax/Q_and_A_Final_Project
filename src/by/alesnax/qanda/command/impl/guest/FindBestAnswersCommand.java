package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
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
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 13.12.2016.
 */
public class FindBestAnswersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestAnswersCommand.class);

    private static final String USER = "user";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String BEST_ANSWERS_ATTR = "attr.best_answers";
    private static final String BEST_ANSWERS_PATH = "path.page.best_answers";
    private static final String PAGE_NO = "attr.page_no";

    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : 0;

        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            int pageNo = 1;
            int startPost = 0;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < 1) {
                    pageNo = 1;
                }
                startPost = (pageNo - 1) * POSTS_PER_PAGE;
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