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
 * Class contains method that processes taking questions of definite category
 * and returns it as attribute set into request. If parameter of category id incorrect method added error message
 * into session and returns value of go_to_main page command
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoCategoryCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String CATEGORY_ID_ATTR = "cat_id";

    /**
     * Keys of error messages, page number attributes that are located in config.properties file
     */
    private static final String CATEGORY_QUESTIONS_ATTR = "attr.request.questions";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Keys of error or success messages in loc.properties file
     */
    private static final String NO_SUCH_CATEGORY_MESSAGE = "category.message.no_such_category_msg";
    private static final String PARAMETER_NOT_FOUND_MESSAGE = "error.error_msg.parameter_not_found";

    /**
     * Keys of commands or pages that are located in config.properties file
     */
    private static final String GO_TO_MAIN_PAGE_COMMAND = "command.go_to_main_page";
    private static final String CATEGORY_PAGE = "path.page.category";

    /**
     * method processes taking question list of category with id taking from request
     * and returns list as attribute set into request.
     * Method calls another method from service layer, which returns list of posts
     * and can throw ServiceException after which method returns value of error500 page.
     * Returns value of category page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of category page or error500 page if exception will be caught
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : DEFAULT_USER_ID;

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

                int startPost = START_ITEM_NO;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < FIRST_PAGE_NO) {
                        pageNo = FIRST_PAGE_NO;
                    }
                    startPost = (pageNo - FIRST_PAGE_NO) * POSTS_PER_PAGE;
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
