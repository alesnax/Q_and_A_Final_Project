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

    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moder";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ATTR = "user";

    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String QUESTION_VALIDATION_FAILED_ATTR = "attr.question_validation_failed";
    private static final String WARN_LOGIN_BEFORE_ADD = "common.add_new_question.error_msg.login_before_add";
    private static final String QUEST_ADDED_STATUS_ATTR = "attr.question_added_status";
    private static final String QUEST_ADDED_STATUS = "common.add_new_question.status_added";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_MAIN_COMMAND = "path.command.go_to_main_page";

    @Override
    public String execute(HttpServletRequest request) {
        String title = request.getParameter(TITLE);
        String category = request.getParameter(CATEGORY);
        String description = request.getParameter(DESCRIPTION);

        String page = null;
        HttpSession session = request.getSession(true);

        QueryUtil.logQuery(request);

        PostValidation postValidation = new PostValidation();
        List<String> validationErrors = postValidation.validateQuestion(title, category, description);

        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null) {
                String role = user.getRole().getValue();
                switch (role) {
                    case USER_ROLE:
                    case MODERATOR_ROLE:
                    case ADMIN_ROLE:
                        PostService postService = ServiceFactory.getInstance().getPostService();
                        try {
                            postService.addNewQuestion(user.getId(), category, title, description);
                            session.removeAttribute(TITLE);
                            session.removeAttribute(CATEGORY);
                            session.removeAttribute(DESCRIPTION);
                            String questionAddedAttr = ConfigurationManager.getProperty(QUEST_ADDED_STATUS_ATTR);
                            session.setAttribute(questionAddedAttr, QUEST_ADDED_STATUS);
                            //session.setAttribute("show_page_point", "#ask_form");
                            String nextCommand = QueryUtil.getPreviousQuery(request);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand/* + "#ask_form"*/;
                        } catch (ServiceException e) {
                            logger.log(Level.ERROR, e);
                            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                            request.setAttribute(errorMessageAttr, e.getMessage());
                            page = ERROR_REQUEST_TYPE;
                        }
                        break;
                    default:
                        session.setAttribute(TITLE, title);
                        session.setAttribute(CATEGORY, category);
                        session.setAttribute(DESCRIPTION, description);
                        String notRegUserAttr = ConfigurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                        session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                        String nextCommand = ConfigurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                }
            } else {
                session.setAttribute(TITLE, title);
                session.setAttribute(CATEGORY, category);
                session.setAttribute(DESCRIPTION, description);
                String notRegUserAttr = ConfigurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_ADD);
                String nextCommand = ConfigurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding question failed.");
            session.setAttribute(TITLE, title);
            session.setAttribute(CATEGORY, category);
            session.setAttribute(DESCRIPTION, description);
            String questionValidationFailedAttr = ConfigurationManager.getProperty(QUESTION_VALIDATION_FAILED_ATTR);
            session.setAttribute(questionValidationFailedAttr, validationErrors);

            //session.setAttribute("show_page_point", "#ask_form");
            String previousQuery = QueryUtil.getPreviousQuery(request);
           // String nextCommand = ConfigurationManager.getProperty(GO_TO_MAIN_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery/* + "#ask_form"*/;
        }
        return page;
    }
}
