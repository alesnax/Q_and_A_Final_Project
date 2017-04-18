package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ModeratorService;
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
 * Command has method that interrupts term of user ban and redirects user back to
 * banned users page.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class StopUserBanCommand implements Command {
    private static Logger logger = LogManager.getLogger(StopUserBanCommand.class);

    /**
     * Names of attributes taking from session or request
     */
    private static final String USER_ATTR = "user";
    private static final String BAN_ID = "ban_id";
    private static final String MODERATOR_USER_ID = "moderator_user_id";
    private static final String MODERATOR_ROLE = "moderator";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String ILLEGAL_OPERATION = "warn.illegal_operation_on_other_profile";

    /**
     * Keys of returned command and page that are located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_BANS_COMMAND = "command.go_to_banned_users";

    /**
     * method interrupts term of user ban and redirects user back to banned users
     * or error_page if exception will be caught.
     * Access to command only for user with MODERATOR or ADMIN role.
     *
     * @param request Processed HttpServletRequest
     * @return value of go_to_banned_post command or authorisation or error page
     * if exception will be caught.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);

        try {
            ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
            String role = user.getRole().getValue();
            int banId = Integer.parseInt(request.getParameter(BAN_ID));
            String nextCommand;
            if (MODERATOR_ROLE.equals(role)) {
                int moderatorUserId = Integer.parseInt(request.getParameter(MODERATOR_USER_ID));
                if (moderatorUserId == user.getId()) {
                    moderatorService.stopUserBan(banId);
                    nextCommand = configurationManager.getProperty(GO_TO_BANS_COMMAND);
                } else {
                    logger.log(Level.WARN, "illegal try to stop user block from user id=" + user.getId() + ", on ban id=" + banId);
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
                    nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                }
            } else {
                moderatorService.stopUserBan(banId);
                nextCommand = configurationManager.getProperty(GO_TO_BANS_COMMAND);
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