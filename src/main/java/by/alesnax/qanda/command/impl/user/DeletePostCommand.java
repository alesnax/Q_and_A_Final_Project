package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class has method that deletes post. Access for authorised users.
 * Returns to previous page or to error page if exception will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class DeletePostCommand implements Command {
    private static Logger logger = LogManager.getLogger(DeletePostCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String POST_USER_ID = "post_user_id";
    private static final String POST_ID = "post_id";
    private static final String POST_MODERATOR_ID = "post_moderator_id";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String ILLEGAL_OPERATION = "warn.illegal_operation_on_other_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * deletes post, calls processing method from service layer,
     * if success scenario - returns to previous page with success message,
     * otherwise returns with error messages to previous page or error page.
     * Method checks user role and lets to admin delete all posts, to moderator delete post from
     * moderated categories or to user his own posts.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        try {
            String role = user.getRole().getValue();
            int userId = user.getId();
            PostService postService = ServiceFactory.getInstance().getPostService();
            int postId = Integer.parseInt(request.getParameter(POST_ID));
            int postUserId = Integer.parseInt(request.getParameter(POST_USER_ID));
            String nextCommand;
            switch (role) {
                case MODERATOR_ROLE:
                    int postModeratorId = Integer.parseInt(request.getParameter(POST_MODERATOR_ID));
                    if (postModeratorId == user.getId()) {
                        postService.deletePost(postId);
                        nextCommand = QueryUtil.getPreviousQuery(request);
                    } else {
                        logger.log(Level.WARN, "illegal try to delete post of other people, owner id=" + postUserId + ", post id=" + postId + ", from user id=" + user.getId());
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                        session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                    }
                    break;
                case ADMIN_ROLE:
                    postService.deletePost(postId);
                    nextCommand = QueryUtil.getPreviousQuery(request);
                    break;
                case USER_ROLE:
                    if (postUserId == user.getId()) {
                        postService.deletePost(postId);
                        nextCommand = QueryUtil.getPreviousQuery(request);
                    } else {
                        logger.log(Level.WARN, "illegal try to delete post of other people, owner id=" + postUserId + ", post id=" + postId + ", from user id=" + user.getId());
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                        nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                    }
                    break;
                default:
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                    nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    break;
            }
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } catch (NumberFormatException | ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
