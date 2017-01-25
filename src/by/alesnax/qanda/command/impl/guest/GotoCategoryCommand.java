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
 * Created by alesnax on 21.12.2016.
 */
public class GotoCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoCategoryCommand.class);

    private static final String USER = "user";
    private static final String CATEGORY_PAGE = "path.page.category";
    private static final String CATEGORY_QUESTIONS_ATTR = "attr.request.questions";
    private static final String CATEGORY_ID_ATTR = "cat_id";
    private static final String NO_SUCH_CATEGORY_MESSAGE = "category.message.no_such_category_msg";
    private static final String PAGE_NO = "attr.page_no";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String PARAMETER_NOT_FOUND_MESSAGE = "error.error_msg.parameter_not_found";
    private static final String GO_TO_MAIN_PAGE_COMMAND = "command.go_to_main_page";


    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : 0;

        String categoryId = request.getParameter(CATEGORY_ID_ATTR);
        if (categoryId == null || categoryId.isEmpty()) {
            logger.log(Level.ERROR, "Wrong parameter, cat_id expected, but wasn't found.");
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            String nextCommand = configurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
            session.setAttribute(wrongCommandMessageAttr, PARAMETER_NOT_FOUND_MESSAGE);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            try {
                PostService postService = ServiceFactory.getInstance().getPostService();
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
                PaginatedList<Post> questions = postService.findQuestionsByCategoryList(categoryId, userId, startPost, POSTS_PER_PAGE);
                if (questions.getItems() == null || questions.getItems().isEmpty()) {
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    String nextCommand = configurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
                    session.setAttribute(wrongCommandMessageAttr, NO_SUCH_CATEGORY_MESSAGE);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } else {
                    String categoryQuestionsAttr = configurationManager.getProperty(CATEGORY_QUESTIONS_ATTR);
                    String categoryPath = configurationManager.getProperty(CATEGORY_PAGE);
                    request.setAttribute(categoryQuestionsAttr, questions);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + categoryPath;
                }
            } catch (ServiceException | NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
