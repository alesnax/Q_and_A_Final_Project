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
 * Class process adding new question. Access for authorised users, otherwise user will redirected to
 * authorisation page. If validation failed, user will be redirected to previous page with error message as an attribute.
 * If user unregistered or validation failed, content of question will be saved in session as attributes.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddQuestionCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddQuestionCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String TITLE = "question_title";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";
    private static final String USER_ATTR = "user";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String QUESTION_VALIDATION_FAILED_ATTR = "attr.question_validation_failed";
    private static final String QUEST_ADDED_STATUS_ATTR = "attr.question_added_status";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of error or success messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_question.error_msg.login_before_add";
    private static final String QUEST_ADDED_STATUS = "common.add_new_question.status_added";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * process adding new question. Checks if attribute user exists in session and validates question content,
     * calls process method from service layer, if success scenario - returns to previous page, otherwise
     * returns to error , authorisation or previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String title = request.getParameter(TITLE);
        String category = request.getParameter(CATEGORY);
        String description = request.getParameter(DESCRIPTION);

        String page;
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
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
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
