package by.alesnax.qanda.command.impl.user;

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
 * Class process adding corrected answer. Access for authorised users, otherwise user will redirected to
 * authorisation page. If validation failed, user will be redirected to previous page with error message as an attribute.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddCorrectedAnswerCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddCorrectedAnswerCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID = "post_id";
    private static final String ANSWER_DESCRIPTION = "answer_description";
    private static final String CORRECTED_ANSWER_DESCRIPTION = "corrected_answer_description";
    private static final String USER_ATTR = "user";

    /**
     * Keys of error messages attributes and edit_post_id, categories_info attributes that are located in config.properties file
     */
    private static final String EDIT_POST_ID_ATTR = "attr.edit_post_id";
    private static final String CORRECT_ANSWER_VALIDATION_FAILED_ATTR = "attr.correct_answer_validation_failed";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String USER_BANNED_FOR_QUESTION_ERROR = "common.add_corrected_question.user_banned_for_add";
    private static final String CATEGORY_CLOSED_ERROR = "common.add_new_answer.error_msg.category_closed_for_answer";

    /**
     * process adding corrected answer. Checks if attribute user exists in session and validates answer content,
     * calls process method from service layer, if success scenario - returns to previous page, otherwise
     * returns to error , authorisation or previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {

        String answerId = request.getParameter(POST_ID);
        String description = request.getParameter(ANSWER_DESCRIPTION);

        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateCorrectedAnswer(description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null && !user.isBanned()) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    int postId = Integer.parseInt(answerId);
                    String status = postService.addCorrectedAnswer(user.getId(), postId, description);

                    if (USER_BANNED.equals(status)) {
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, USER_BANNED_FOR_QUESTION_ERROR);
                        user.setBanned(true);
                    } else if(!OPERATION_PROCESSED.equals(status)){
                        String shortCategoriesAttr = configurationManager.getProperty(SHORT_CATEGORIES_ATTR);
                        List<CategoryInfo> categoriesInfo = postService.takeShortCategoriesList();
                        session.setAttribute(shortCategoriesAttr, categoriesInfo);

                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, CATEGORY_CLOSED_ERROR);
                    }
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceException | NumberFormatException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, USER_BANNED_FOR_QUESTION_ERROR);
                String nextCommand = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding answer failed.");
            String answerValidationFailedAttr = configurationManager.getProperty(CORRECT_ANSWER_VALIDATION_FAILED_ATTR);
            session.setAttribute(CORRECTED_ANSWER_DESCRIPTION, description);
            session.setAttribute(answerValidationFailedAttr, validationErrors);
            String editPostIdAttr  = configurationManager.getProperty(EDIT_POST_ID_ATTR);
            session.setAttribute(editPostIdAttr, answerId);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}