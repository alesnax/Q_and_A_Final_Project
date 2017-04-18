package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.user.ChangeUserInfoCommand;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.UserValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Command has method for processing password recovering
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class RecoverPasswordCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserInfoCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String EMAIL = "email";
    private static final String KEY_WORD_TYPE = "key_word";
    private static final String KEY_WORD_VALUE = "key_word_value";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String SUCCESS_CHANGE_PASSWORD_ATTR = "attr.success_password_change_msg";

    /**
     * Keys of error or success messages in loc.properties file
     */
    private static final String SUCCESS_CHANGE_PASS_MESSAGE = "pass_recov.message.change_success";
    private static final String PASS_RECOV_VALIDATION_ATTR = "attr.pass_recov_validation_error";
    private static final String FAILED_PASS_RECOVERING = "pass_recover.message.failed_recover";

    /**
     * Key of temporary password attribute that is located in config.properties file
     */
    private static final String TEMP_PASS_ATTR = "temp_password";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_PASS_RECOVERING_COMMAND = "command.go_to_password_recovery";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "command.go_to_authorization_page";

    /**
     * method process password recovering, it validates values of recovering data attributes and
     * calls method from service layer which returns new password that is put into session attribute
     *
     * @param request Processed HttpServletRequest
     * @return value of authorization page(success scenario), or password_recovering page if validation failed,
     * profile page, if attribute 'user' exists in session or error500 page if exception was caught
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        String email = request.getParameter(EMAIL);
        String keyWordType = request.getParameter(KEY_WORD_TYPE);
        String keyWordValue = request.getParameter(KEY_WORD_VALUE);

        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validatePasswordRecovData(email, keyWordType, keyWordValue);

        User user = (User) session.getAttribute(USER);

        if (user == null) {
            if (validationErrors.isEmpty()) {
                UserService userService = ServiceFactory.getInstance().getUserService();
                try {
                    String changedPassword = userService.recoverPassword(email, keyWordType, keyWordValue);
                    if(changedPassword != null && !changedPassword.isEmpty()){
                        logger.log(Level.INFO, "User with email " + email + " has successfully recovered their password.");
                        String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_PASSWORD_ATTR);
                        session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_PASS_MESSAGE);
                        session.setAttribute(TEMP_PASS_ATTR, changedPassword);
                        String gotoAuthorizationPageCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoAuthorizationPageCommand;
                    } else{
                        String passRecovValidationAttr = configurationManager.getProperty(PASS_RECOV_VALIDATION_ATTR);
                        validationErrors = new ArrayList<>();
                        validationErrors.add(FAILED_PASS_RECOVERING);
                        session.setAttribute(passRecovValidationAttr, validationErrors);
                        String gotoPassRecoveringCommand = configurationManager.getProperty(GO_TO_PASS_RECOVERING_COMMAND);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoPassRecoveringCommand;
                    }
                } catch (ServiceException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String passRecovValidationAttr = configurationManager.getProperty(PASS_RECOV_VALIDATION_ATTR);
                session.setAttribute(passRecovValidationAttr, validationErrors);
                String gotoPassRecoveringCommand = configurationManager.getProperty(GO_TO_PASS_RECOVERING_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoPassRecoveringCommand;
            }
        } else {
            String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
        }
        return page;
    }
}
