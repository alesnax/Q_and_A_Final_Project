package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
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
 * Command has method that takes post by id from service layer , put it as an attribute to request and returns
 * value of post page or error_page if exception will be caught. Access to method for users with ADMIN or MODERATOR
 * role, otherwise user will be redirected to authorisation or profile page.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoPostCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoPostCommand.class);

    /**
     * Names of attributes taking from session or request
     */
    private static final String POST_ID_ATTR = "post_id";
    private static final String BACK_PAGE_ATTR = "back_page";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Key of post attribute that are located in config.properties file
     */
    private static final String POST_ATTR = "attr.request.post";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String POST_WAS_DELETED = "error.error_msg.post_was_deleted";

    /**
     * Keys of returned command and page that are located in config.properties file
     */
    private static final String SHOW_BANNED_POST_PAGE = "path.page.banned_post";

    /**
     * method takes post from service layer , put it as an attribute to request and returns
     * value of post page.
     * If post id is incorrect or exception will be caught, user will be redirected error page.
     *
     * @param request Processed HttpServletRequest
     * @return value of post page or authorization page if user not authorised or error page
     * if exception will be caught.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        try {
            int postId = Integer.parseInt(request.getParameter(POST_ID_ATTR));
            String backPage = request.getParameter(BACK_PAGE_ATTR);
            if (backPage != null && !backPage.isEmpty()) {
                request.setAttribute(BACK_PAGE_ATTR, backPage);
            }
            PostService postService = ServiceFactory.getInstance().getPostService();
            Post post = postService.findPostById(postId);
            if (post != null) {
                String postAttr = configurationManager.getProperty(POST_ATTR);
                request.setAttribute(postAttr, post);
            } else {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, POST_WAS_DELETED);
            }
            String bannedPostPath = configurationManager.getProperty(SHOW_BANNED_POST_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bannedPostPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}