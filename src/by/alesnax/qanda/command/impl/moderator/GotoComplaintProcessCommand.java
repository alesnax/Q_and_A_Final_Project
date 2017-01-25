package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.POSTS_PER_PAGE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 15.01.2017.
 *
 */
public class GotoComplaintProcessCommand implements Command {

    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String POST_ID_ATTR = "post_id";
    private static final String USER_ID_ATTR = "user_id";
    private static final String PROCESSED_POST_ID_ATTR = "process_post_id";
    private static final String PROCESSED_USER_ID_ATTR = "process_author_id";
    private static final String PAGE_NO = "attr.page_no";
    private static final String PAGE_NO_QUERY_PART = "command.page_query_part";


    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_COMPLAINTS = "command.go_to_complaints";

    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";


    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case ADMIN_ROLE:
                case MODERATOR_ROLE:
                    String postId = request.getParameter(POST_ID_ATTR);
                    String authorId = request.getParameter(USER_ID_ATTR);
                    int pageNo = 1;
                    String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                    if (request.getParameter(pageNoAttr) != null) {
                        pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                        if (pageNo < 1) {
                            pageNo = 1;
                        }
                    }
                    session.setAttribute(PROCESSED_POST_ID_ATTR, postId);
                    session.setAttribute(PROCESSED_USER_ID_ATTR, authorId);
                    String nextCommand = configurationManager.getProperty(GO_TO_COMPLAINTS);
                    String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
                    break;
                case USER_ROLE:
                default:
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
            }
        }
        return page;
    }
}
