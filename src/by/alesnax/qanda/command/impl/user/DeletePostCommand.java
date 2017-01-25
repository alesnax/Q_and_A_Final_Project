package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
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

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 05.01.2017.
 */
@SuppressWarnings("Duplicates")
public class DeletePostCommand implements Command {
    private static Logger logger = LogManager.getLogger(DeletePostCommand.class);

    private static final String USER_ATTR = "user";
    private static final String POST_USER_ID = "post_user_id";
    private static final String POST_ID = "post_id";
    private static final String POST_MODERATOR_ID = "post_moderator_id";
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
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_MAKE_OPERATION);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            try {
                String role = user.getRole().getValue();
                int userId = user.getId();
                PostService postService = ServiceFactory.getInstance().getPostService();
                int postId = Integer.parseInt(request.getParameter(POST_ID));
                int postUserId = Integer.parseInt(request.getParameter(POST_USER_ID));
                String nextCommand = null;
                switch (role) {
                    case MODERATOR_ROLE:
                        int postModeratorId = Integer.parseInt(request.getParameter(POST_MODERATOR_ID));
                        if (postModeratorId == user.getId()) {
                            postService.deletePost(postId);
                            nextCommand = QueryUtil.getPreviousQuery(request);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        } else {
                            logger.log(Level.WARN, "illegal try to delete post of other people, owner id=" + postUserId + ", post id=" + postId + ", from user id=" + user.getId());
                            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                            nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        }
                        break;
                    case ADMIN_ROLE:
                        postService.deletePost(postId);
                        nextCommand = QueryUtil.getPreviousQuery(request);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                    case USER_ROLE:
                        if (postUserId == user.getId()) {
                            postService.deletePost(postId);
                            nextCommand = QueryUtil.getPreviousQuery(request);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        } else {
                            logger.log(Level.WARN, "illegal try to delete post of other people, owner id=" + postUserId + ", post id=" + postId + ", from user id=" + user.getId());
                            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                            nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        }
                        break;
                    default:
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                        nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                }
            } catch (NumberFormatException | ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
