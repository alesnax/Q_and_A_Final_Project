package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
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
 * Class process changing info. Access for authorised users.
 * If validation failed, user will be redirected to previous page with error message as an attribute.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class ChangeUserInfoCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserInfoCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String LOGIN = "login";
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
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_USER_VALIDATION_ATTR = "attr.user_validation_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String ERROR_USER_ALREADY_EXIST = "user_registration.error_msg.user_already_exists";
    private static final String SUCCESS_CHANGE_MESSAGE = "edit_profile.message.change_saved";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";

    /**
     * process changing user info and validates it,
     * calls process method from service layer, if success scenario - returns to previous page with
     * success message , otherwise returns with error messages to previous page or error page
     * If login or email duplicates, user will be redirected to previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();

        String login = request.getParameter(LOGIN);
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
        QueryUtil.logQuery(request);

        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateUserMainData(login, name, surname, email,
                bDay, bMonth, bYear, sex, country, city, keyWordType, keyWordValue);

        User user = (User) session.getAttribute(USER);

        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                User updatedUser = userService.changeUserInfo(user.getId(), login, name, surname, email,
                        bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
                session.setAttribute(USER, updatedUser);
                logger.log(Level.INFO, "User " + login + " has successfully change his profile information");
                String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_MESSAGE);

                String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            } catch (ServiceDuplicatedInfoException e) {
                logger.log(Level.WARN, e);
                validationErrors.add(ERROR_USER_ALREADY_EXIST);
                String errorUserValidationAttr = configurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);
                session.setAttribute(errorUserValidationAttr, validationErrors);
                String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of user information failed.");
            String errorUserValidationAttr = configurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);// try-catch
            session.setAttribute(errorUserValidationAttr, validationErrors);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        }
        return page;
    }
}
