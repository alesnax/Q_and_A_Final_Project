package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.UserAuthorizationCommand;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.UserValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 06.01.2017.
 */
public class ChangePasswordCommand implements Command {
    private static Logger logger = LogManager.getLogger(UserAuthorizationCommand.class);

    private static final String OLD_PASSWORD = "OldPasswd";
    private static final String NEW_PASSWORD = "Passwd";
    private static final String REPEATED_NEW_PASSWORD = "PasswdAgain";
    private static final String USER = "user";

    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_PASSWORD_VALIDATION_ATTR = "attr.password_validation_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    private static final String SUCCESS_CHANGE_PASSWORD_MESSAGE = "edit_profile.message.change_password_saved";
    private static final String WRONG_PASSWORD_FOUND = "edit_profile.message.wrong_password_found";

    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        String password1 = request.getParameter(OLD_PASSWORD);
        String password2 = request.getParameter(NEW_PASSWORD);
        String password3 = request.getParameter(REPEATED_NEW_PASSWORD);

        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateNewPassword(password1, password2, password3);


        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                boolean changed = userService.changePassword(user.getId(), password1, password2);
                if (changed) {
                    logger.log(Level.INFO, "User " + user.getId() + " has successfully change his profile information");
                    String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                    request.getSession(true).setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_PASSWORD_MESSAGE);
                    String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                } else {
                    logger.log(Level.WARN, "User id=" + user.getId() + " :Wrong password was found while changing password try.");
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    request.getSession().setAttribute(wrongCommandMessageAttr, WRONG_PASSWORD_FOUND);

                    String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                }
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of user passwords while changing failed.");
            String errorUserValidationAttr = configurationManager.getProperty(ERROR_PASSWORD_VALIDATION_ATTR);// try-catch
            request.getSession(true).setAttribute(errorUserValidationAttr, validationErrors);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        }
        return page;
    }
}
