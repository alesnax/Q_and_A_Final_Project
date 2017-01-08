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
public class DeletePostCommand implements Command {
    private static Logger logger = LogManager.getLogger(DeletePostCommand.class);

    private static final String USER_ATTR = "user";
    private static final String POST_USER_ID = "post_user_id";
    private static final String POST_ID = "post_id";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

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
                PostService postService = ServiceFactory.getInstance().getPostService();

                int postUserId = Integer.parseInt(request.getParameter(POST_USER_ID));
                int postId = Integer.parseInt(request.getParameter(POST_ID));
                if(postUserId == user.getId()){

                    postService.deletePost(postId);

                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } else {
                    logger.log(Level.WARN, "illegal try to delete post of other people, owner id=" + postUserId + ", post id=" + postId + ", from user id=" + user.getId());
                    String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                    String gotoProfileCommand = ConfigurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
