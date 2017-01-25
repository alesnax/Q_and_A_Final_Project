package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.PostValidation;
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
public class AddQuestionCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddQuestionCommand.class);

    private static final String TITLE = "question_title";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";

    private static final String USER_ATTR = "user";

    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String QUESTION_VALIDATION_FAILED_ATTR = "attr.question_validation_failed";
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_question.error_msg.login_before_add";
    private static final String QUEST_ADDED_STATUS_ATTR = "attr.question_added_status";
    private static final String QUEST_ADDED_STATUS = "common.add_new_question.status_added";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    @Override
    public String execute(HttpServletRequest request) {
        String title = request.getParameter(TITLE);
        String category = request.getParameter(CATEGORY);
        String description = request.getParameter(DESCRIPTION);

        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateQuestion(title, category, description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    postService.addNewQuestion(user.getId(), category, title, description);
                    session.removeAttribute(TITLE);
                    session.removeAttribute(CATEGORY);
                    session.removeAttribute(DESCRIPTION);
                    String questionAddedAttr = configurationManager.getProperty(QUEST_ADDED_STATUS_ATTR);
                    session.setAttribute(questionAddedAttr, QUEST_ADDED_STATUS);
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                    request.setAttribute(errorMessageAttr, e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                session.setAttribute(TITLE, title);
                session.setAttribute(CATEGORY, category);
                session.setAttribute(DESCRIPTION, description);
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding question failed.");
            session.setAttribute(TITLE, title);
            session.setAttribute(CATEGORY, category);
            session.setAttribute(DESCRIPTION, description);
            String questionValidationFailedAttr = configurationManager.getProperty(QUESTION_VALIDATION_FAILED_ATTR);
            session.setAttribute(questionValidationFailedAttr, validationErrors);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}
