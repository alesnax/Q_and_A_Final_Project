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
 * Command has method that add user rate to definite post
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class RatePostCommand implements Command {
    private static Logger logger = LogManager.getLogger(RatePostCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String MARK = "mark";
    private static final String POST_ID = "post_id";

    /**
     * Keys of attributes in config.properties file, used for pagination, error messages
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";
    private static final String WRONG_PARAMETER = "common.add_new_answer.error_msg.wrong_parameter";

    /**
     * Key of go_to_authorization_page command located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * Keys of min and max rates attributes located in config.properties file
     */
    private static final String MIN_RATE_ATTR = "attr.min_rate";
    private static final String MAX_RATE_ATTR = "attr.max_rate";

    /**
     * Method add rate to definite post or changes its value if user rate to this post has already exists.
     * If user is unauthorised they will be redirected to authorisation page.
     * If exception will be caught user will be redirected to error505 page.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or error or authorization page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        try {
            PostService postService = ServiceFactory.getInstance().getPostService();
            int postId = Integer.parseInt(request.getParameter(POST_ID));
            int mark = Integer.parseInt(request.getParameter(MARK));
            int minRate = Integer.parseInt(configurationManager.getProperty(MIN_RATE_ATTR));
            int maxRate = Integer.parseInt(configurationManager.getProperty(MAX_RATE_ATTR));
            if (mark <= maxRate && mark >= minRate) {
                postService.ratePost(postId, mark, user.getId());
                String nextCommand = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            } else {
                logger.log(Level.WARN, "invalid rate value while rate post, user id=" + user.getId() + ", post id=" + postId);
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, WRONG_PARAMETER);
                String nextCommand = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } catch (NumberFormatException | ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }

        return page;
    }
}
