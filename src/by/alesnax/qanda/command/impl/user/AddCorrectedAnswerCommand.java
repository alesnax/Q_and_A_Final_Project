package by.alesnax.qanda.command.impl.user;

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

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 10.01.2017.
 */
public class AddCorrectedAnswerCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddCorrectedAnswerCommand.class);

    private static final String POST_ID = "post_id";
    private static final String ANSWER_DESCRIPTION = "answer_description";
    private static final String CORRECTED_ANSWER_DESCRIPTION = "corrected_answer_description";

    private static final String USER_ATTR = "user";

    private static final String EDIT_POST_ID_ATTR = "attr.edit_post_id";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String CORRECT_ANSWER_VALIDATION_FAILED_ATTR = "attr.correct_answer_validation_failed";
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_answer.error_msg.login_before_add";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    @Override
    public String execute(HttpServletRequest request) {

        String answerId = request.getParameter(POST_ID);
        String description = request.getParameter(ANSWER_DESCRIPTION);

        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateCorrectedAnswer(description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    int postId = Integer.parseInt(answerId);
                    postService.addCorrectedAnswer(postId, description);
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                    request.setAttribute(errorMessageAttr, e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
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