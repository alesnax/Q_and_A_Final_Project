package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.PostValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import


/**
 * Class process adding new answer. Access for authorised users, otherwise user will redirected to
 * authorisation page. If validation failed, user will be redirected to previous page with error message as an attribute.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddAnswerCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddAnswerCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String QUESTION_ID = "question_id";
    private static final String CATEGORY_ID = "category_id";
    private static final String ANSWER_DESCRIPTION = "answer_description";
    private static final String USER_ATTR = "user";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String ANSWER_VALIDATION_FAILED_ATTR = "attr.answer_validation_failed";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_msg";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";

    /**
     * Key of error or success message located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_answer.error_msg.login_before_add";
    private static final String SUCCESS_ADD_ANSWER_MESSAGE = "common.add_new_answer.success.added_answer_msg";
    private static final String CATEGORY_CLOSED_ERROR = "common.add_new_answer.error_msg.category_closed";
    private static final String USER_BANNED_FOR_ANSWER_ERROR = "common.add_new_answer.error_msg.user_banned";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_QUESTION_COMMAND = "command.go_to_question";

    /**
     * process adding new answer. Checks if attribute user exists in session and validates answer content,
     * calls process method from service layer, if success scenario - returns to previous page, otherwise
     * returns to error , authorisation or previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String questionId = request.getParameter(QUESTION_ID);
        String categoryId = request.getParameter(CATEGORY_ID);
        String description = request.getParameter(ANSWER_DESCRIPTION);

        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateAnswer(questionId, categoryId, description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null && !user.isBanned()) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    String status = postService.addNewAnswer(user.getId(), questionId, categoryId, description);
                    if (OPERATION_PROCESSED.equals(status)) {
                        String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                        session.setAttribute(successChangeMessageAttr, SUCCESS_ADD_ANSWER_MESSAGE);
                    } else if(USER_BANNED.equals(status)){
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, USER_BANNED_FOR_ANSWER_ERROR);
                        user.setBanned(true);
                    } else {
                        List<CategoryInfo> categoriesInfo = postService.takeShortCategoriesList();
                        String shortCategoriesAttr = configurationManager.getProperty(SHORT_CATEGORIES_ATTR);
                        session.setAttribute(shortCategoriesAttr, categoriesInfo);
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, CATEGORY_CLOSED_ERROR);
                    }
                    String nextCommand = configurationManager.getProperty(GO_TO_QUESTION_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + questionId;
                } catch (ServiceException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else if (user != null) {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, USER_BANNED_FOR_ANSWER_ERROR);
                String nextCommand = configurationManager.getProperty(GO_TO_QUESTION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + questionId;
            } else {
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding answer failed.");
            session.setAttribute(ANSWER_DESCRIPTION, description);
            String answerValidationFailedAttr = configurationManager.getProperty(ANSWER_VALIDATION_FAILED_ATTR);
            session.setAttribute(answerValidationFailedAttr, validationErrors);
            String nextCommand = configurationManager.getProperty(GO_TO_QUESTION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + questionId;
        }
        return page;
    }
}
