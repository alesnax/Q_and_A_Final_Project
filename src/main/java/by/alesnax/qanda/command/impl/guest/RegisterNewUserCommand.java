package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceDuplicatedInfoException;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.UserValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class has method that processes registration of  new user. Access for unauthorised users, otherwise user will redirected to
 * profile page. If validation failed, user will be redirected to previous page with error message as an attribute.
 * If validation failed, content of user info will be saved in session as attributes.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class RegisterNewUserCommand implements Command {
    private static Logger logger = LogManager.getLogger(RegisterNewUserCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String LOGIN = "login";
    private static final String PASSWORD = "Passwd";
    private static final String PASSWORD_COPY = "PasswdAgain";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "email";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";
    private static final String BIRTH_DAY = "birth_day";
    private static final String BIRTH_MONTH = "birth_month";
    private static final String BIRTH_YEAR = "birth_year";
    private static final String GENDER = "gender";
    private static final String PAGE_STATUS = "page_status";
    private static final String KEY_WORD_TYPE = "key_word";
    private static final String KEY_WORD_VALUE = "key_word_value";

    /**
     * Keys of error or welcome messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_USER_VALIDATION_ATTR = "attr.user_validation_error";
    private static final String WELCOME_MSG_ATTR = "attr.welcome_msg";

    /**
     * Keys of error or success messages in loc.properties file
     */
    private static final String ERROR_USER_ALREADY_EXIST = "user_registration.error_msg.user_already_exists";
    private static final String WELCOME_NEW_USER_MSG = "guest.user_authorization_page.welcome_new_user_msg";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_REGISTRATION_COMMAND = "path.command.go_to_registration_page";

    /**
     * method processes registration of  new user. Access for unauthorised users, otherwise user will redirected to
     * profile page. If validation failed, user will be redirected to previous page with error message as an attribute.
     * If validation failed, content of user info will be saved in session as attributes.
     *
     * @param request Processed HttpServletRequest
     * @return value of go_to_authorization command(success scenario), go_to_registration, error page or profile page otherwise.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();

        String login = request.getParameter(LOGIN);
        String password = request.getParameter(PASSWORD);
        String passwordCopy = request.getParameter(PASSWORD_COPY);
        String name = request.getParameter(FIRST_NAME);
        String surname = request.getParameter(LAST_NAME);
        String email = request.getParameter(EMAIL);
        String bDay = request.getParameter(BIRTH_DAY);
        String bMonth = request.getParameter(BIRTH_MONTH);
        String bYear = request.getParameter(BIRTH_YEAR);
        String sex = request.getParameter(GENDER);
        String country = request.getParameter(COUNTRY);
        String city = request.getParameter(CITY);
        String status = request.getParameter(PAGE_STATUS);
        String keyWordType = request.getParameter(KEY_WORD_TYPE);
        String keyWordValue = request.getParameter(KEY_WORD_VALUE);

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateNewUser(login, password, passwordCopy, name, surname, email,
                bDay, bMonth, bYear, sex, country, city, keyWordType, keyWordValue);

        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();

            try {
                userService.registerNewUser(login, password, name, surname, email,
                        bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
                logger.log(Level.INFO, "User " + login + " was successfully registered");
                String welcomeMessageAttr = configurationManager.getProperty(WELCOME_MSG_ATTR);
                session.setAttribute(welcomeMessageAttr, WELCOME_NEW_USER_MSG);
                String gotoAuthorizationCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoAuthorizationCommand;
            } catch (ServiceDuplicatedInfoException e) {
                validationErrors.add(ERROR_USER_ALREADY_EXIST);
                logger.log(Level.WARN, e);
                String errorUserValidationAttr = configurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);
                session.setAttribute(errorUserValidationAttr, validationErrors);
                String gotoRegistrationCommand = configurationManager.getProperty(GO_TO_REGISTRATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoRegistrationCommand;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "Validation of user failed.");
            String errorUserValidationAttr = configurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);
            session.setAttribute(errorUserValidationAttr, validationErrors);
            String gotoRegistrationCommand = configurationManager.getProperty(GO_TO_REGISTRATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoRegistrationCommand;
        }
        return page;
    }
}