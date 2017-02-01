package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ModeratorServiceImpl;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.ComplaintValidation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

//static import
import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Class has method processing adding new complaint decision.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddComplaintDecisionCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddComplaintDecisionCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID = "post_id";
    private static final String AUTHOR_ID = "author_id";
    private static final String COMPLAINT_DECISION = "complaint_decision";
    private static final String COMPLAINT_STATUS = "status";
    private static final String PROCESSED_POST_ID_ATTR = "process_post_id";
    private static final String PROCESSED_USER_ID_ATTR = "process_author_id";
    private static final String INVALIDATED_DECISION = "invalidated_decision";
    private static final String USER_ATTR = "user";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String COMPLAINT_VALIDATION_FAILED_ATTR = "attr.complaint_validation_failed";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * process adding new complaint decision. Checks if attribute user exists in session and validates decision content,
     * calls processing method from service layer, if success scenario - returns to previous page, otherwise
     * returns to error , authorisation or previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        String decision = request.getParameter(COMPLAINT_DECISION);
        ComplaintValidation complaintValidation = new ComplaintValidation();
        List<String> validationErrors = complaintValidation.validateComplaintDecision(decision);
        String postId = request.getParameter(POST_ID);
        String authorId = request.getParameter(AUTHOR_ID);
        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null) {
                ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                try {
                    int complaintPostId = Integer.parseInt(postId);
                    int complaintAuthorId = Integer.parseInt(authorId);
                    int status = Integer.parseInt(request.getParameter(COMPLAINT_STATUS));
                    moderatorService.addComplaintDecision(user.getId(), complaintPostId, complaintAuthorId, decision, status);
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceException | NumberFormatException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_MAKE_OPERATION);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding complaint decision failed.");
            String complaintFailedAttr = configurationManager.getProperty(COMPLAINT_VALIDATION_FAILED_ATTR);
            session.setAttribute(complaintFailedAttr, validationErrors);
            session.setAttribute(PROCESSED_POST_ID_ATTR, postId);
            session.setAttribute(PROCESSED_USER_ID_ATTR, authorId);
            session.setAttribute(INVALIDATED_DECISION, decision);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}













/*
    private static Logger logger = LogManager.getLogger(StopUserBanCommand.class);

    private static final String USER_ATTR = "user";
    private static final String BAN_ID = "ban_id";
    private static final String MODERATOR_USER_ID = "moderator_user_id";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String ILLEGAL_OPERATION = "warn.illegal_operation_on_other_profile";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String notRegUserAttr = ConfigurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_MAKE_OPERATION);
            String nextCommand = ConfigurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            try {
                ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                String role = user.getRole().getValue();
                int banId = Integer.parseInt(request.getParameter(BAN_ID));
                String nextCommand = null;
                switch (role) {
                    case MODERATOR_ROLE:
                        int moderatorUserId = Integer.parseInt(request.getParameter(MODERATOR_USER_ID));
                        if (moderatorUserId == user.getId()) {
                            moderatorService.stopUserBan(banId);
                            nextCommand = QueryUtil.getPreviousQuery(request);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        } else {
                            logger.log(Level.WARN, "illegal try to stop user block from user id=" + user.getId() + ", on ban id=" + banId);
                            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                            nextCommand = ConfigurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        }
                        break;
                    case ADMIN_ROLE:
                        moderatorService.stopUserBan(banId);
                        nextCommand = QueryUtil.getPreviousQuery(request);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                    case USER_ROLE:
                    default:
                        logger.log(Level.WARN, "illegal try to stop user block from user id=" + user.getId() + ", on ban id=" + banId);
                        String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                        nextCommand = ConfigurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                }
            } catch (NumberFormatException | ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}*/
