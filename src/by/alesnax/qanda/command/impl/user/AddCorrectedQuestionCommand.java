package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.validation.PostValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;


/**
 * Class process adding new question. Access for authorised users, otherwise user will redirected to
 * authorisation page. If validation failed, user will be redirected to previous page with error message as an attribute.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddCorrectedQuestionCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddCorrectedQuestionCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID = "post_id";
    private static final String CORRECTED_POST_CATEGORY = "corrected_post_category";
    private static final String CORRECTED_POST_TITLE = "corrected_post_title";
    private static final String CORRECTED_QUESTION_DESCRIPTION = "corrected_question_description";
    private static final String USER_ATTR = "user";

    /**
     * Key of edit_post_id  attribute that are located in config.properties file and added in session if validation failed
     */
    private static final String EDIT_POST_ID_ATTR = "attr.edit_post_id";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String CORRECT_QUESTION_VALIDATION_FAILED_ATTR = "attr.correct_question_validation_failed";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";

    /**
     * Key of error message located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_answer.error_msg.login_before_add";
    private static final String USER_BANNED_FOR_QUESTION_ERROR = "common.add_corrected_question.user_banned_for_add";
    private static final String CATEGORY_CLOSED_ERROR = "common.add_new_answer.error_msg.category_closed";

    /**
     * Key of command that is located in config.properties file
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
        String postId = request.getParameter(POST_ID);
        String categoryId = request.getParameter(CORRECTED_POST_CATEGORY);
        String correctedTitle = request.getParameter(CORRECTED_POST_TITLE);
        String description = request.getParameter(CORRECTED_QUESTION_DESCRIPTION);

        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateQuestion(correctedTitle, categoryId, description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null && !user.isBanned()) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    int questionId = Integer.parseInt(postId);
                    int catId = Integer.parseInt(categoryId);
                    String status = postService.addCorrectedQuestion(user.getId(), questionId, catId, correctedTitle, description);
                    if (USER_BANNED.equals(status)) {
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, USER_BANNED_FOR_QUESTION_ERROR);
                        user.setBanned(true);
                    } else if(!OPERATION_PROCESSED.equals(status)){
                        List<CategoryInfo> categoriesInfo = postService.takeShortCategoriesList();
                        String shortCategoriesAttr = configurationManager.getProperty(SHORT_CATEGORIES_ATTR);
                        session.setAttribute(shortCategoriesAttr, categoriesInfo);
                        String editPostIdAttr = configurationManager.getProperty(EDIT_POST_ID_ATTR);
                        session.setAttribute(editPostIdAttr, postId);
                        session.setAttribute(CORRECTED_POST_TITLE, correctedTitle);
                        session.setAttribute(CORRECTED_QUESTION_DESCRIPTION, description);
                        String questionValidationFailedAttr = configurationManager.getProperty(CORRECT_QUESTION_VALIDATION_FAILED_ATTR);
                        session.setAttribute(questionValidationFailedAttr, CATEGORY_CLOSED_ERROR);
                    }
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding question failed.");
            String questionValidationFailedAttr = configurationManager.getProperty(CORRECT_QUESTION_VALIDATION_FAILED_ATTR);
            session.setAttribute(questionValidationFailedAttr, validationErrors);
            String editPostIdAttr = configurationManager.getProperty(EDIT_POST_ID_ATTR);
            session.setAttribute(editPostIdAttr, postId);
            session.setAttribute(CORRECTED_POST_CATEGORY, categoryId);
            session.setAttribute(CORRECTED_POST_TITLE, correctedTitle);
            session.setAttribute(CORRECTED_QUESTION_DESCRIPTION, description);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}