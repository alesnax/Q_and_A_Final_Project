package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

//static import

/**
 * Command has method that redirects user to previous page and put attribute into session which
 * opens post complaint block, access for command is only for authorised users,
 * otherwise user will be redirected to authorisation page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoPostComplaintCommand implements Command {

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID = "post_id";
    private static final String USER_ATTR = "user";

    /**
     * added attribute that let block for complaint to question with definite id be opened
     */
    private static final String COMPLAINT_POST_ID_ATTR = "attr.complaint_id";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";

    /**
     * Key of returned page that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * method redirects user to previous page and put attribute into session which
     * opens post complaint block, access for command is only for authorised users,
     * otherwise user will be redirected to authorisation page.
     * If post id incorrect user wil be redirected to error500 page
     *
     * @param request Processed HttpServletRequest
     * @return value of previous query or value of authorization page(when user not authorised)
     * or error page, if post id is incorrect
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
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
            String postId = request.getParameter(POST_ID);
            String complaintAttr = configurationManager.getProperty(COMPLAINT_POST_ID_ATTR);
            session.setAttribute(complaintAttr, postId);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}
