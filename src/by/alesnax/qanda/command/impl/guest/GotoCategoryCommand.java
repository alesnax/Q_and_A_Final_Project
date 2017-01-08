package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
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
    private static final String USER_ID = "user_id";
    private static final String CATEGORY_PAGE = "path.page.category";
    private static final String CATEGORY_QUESTIONS_ATTR = "attr.request.questions";
    private static final String CATEGORY_ID_ATTR = "cat_id";
    private static final String NO_SUCH_CATEGORY_MESSAGE = "category.message.no_such_category_msg";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String PARAMETER_NOT_FOUND_MESSAGE = "error.error_msg.parameter_not_found";
    private static final String GO_TO_MAIN_PAGE_COMMAND = "command.go_to_main_page";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : 1;

        String categoryId = request.getParameter(CATEGORY_ID_ATTR);
        if (categoryId == null || categoryId.isEmpty()) {
            logger.log(Level.ERROR, "Wrong parameter, cat_id expected, but wasn't found.");
            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, PARAMETER_NOT_FOUND_MESSAGE);
            String nextCommand = ConfigurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            PostService postService = ServiceFactory.getInstance().getPostService();
            try {
                List<Post> questions = postService.takeQuestionsByCategoryList(categoryId, userId);
                if (questions == null || questions.isEmpty()) {
                    String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, NO_SUCH_CATEGORY_MESSAGE);
                    String nextCommand = ConfigurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } else {
                    String categoryQuestionsAttr = ConfigurationManager.getProperty(CATEGORY_QUESTIONS_ATTR);
                    request.setAttribute(categoryQuestionsAttr, questions);
                    String categoryPath = ConfigurationManager.getProperty(CATEGORY_PAGE);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + categoryPath;
                }
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
