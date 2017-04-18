package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//satic import

/**
 * Class has method that deletes user(update state to 'deleted'). Access for authorised users.
 * Returns to previous page or to error page if exception will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class DeleteAccountCommand implements Command {
    private static Logger logger = LogManager.getLogger(DeletePostCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String PASSWORD = "password";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String WRONG_PASSWORD = "warn.delete_account.wrong_password";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";
    private static final String LOG_OUT_COMMAND = "command.log_out";

    /**
     * updates user state to deleted, calls processing method from service layer,
     * if success scenario - returns to start page,
     * otherwise returns with error messages to previous page or error page.
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
            int userId = user.getId();
            UserService userService = ServiceFactory.getInstance().getUserService();
            String password = request.getParameter(PASSWORD);
            boolean deleted = userService.deleteAccount(userId, password);
            if (deleted) {
                String logOutCommand = configurationManager.getProperty(LOG_OUT_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + logOutCommand;
            } else {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                session.setAttribute(wrongCommandMessageAttr, WRONG_PASSWORD);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            }
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
