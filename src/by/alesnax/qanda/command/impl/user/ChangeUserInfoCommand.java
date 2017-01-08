package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.RegisterNewUserCommand;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceDuplicatedInfoException;
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
public class ChangeUserInfoCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserInfoCommand.class);

    private static final String USER = "user";
    private static final String LOGIN = "login";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "email";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";
    private static final String BIRTH_DAY = "birth_day";
    private static final String BIRH_MONTH = "birth_month";
    private static final String BIRTH_YEAR = "birth_year";
    private static final String GENDER = "gender";
    private static final String PAGE_STATUS = "page_status";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_USER_VALIDATION_ATTR = "attr.user_validation_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    private static final String ERROR_USER_ALREADY_EXIST = "user_registration.error_msg.user_already_exists";
    private static final String SUCCESS_CHANGE_MESSAGE = "edit_profile.message.change_saved";

    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        String login = request.getParameter(LOGIN);
        String name = request.getParameter(FIRST_NAME);
        String surname = request.getParameter(LAST_NAME);
        String email = request.getParameter(EMAIL);
        String bDay = request.getParameter(BIRTH_DAY);
        String bMonth = request.getParameter(BIRH_MONTH);
        String bYear = request.getParameter(BIRTH_YEAR);
        String sex = request.getParameter(GENDER);
        String country = request.getParameter(COUNTRY);
        String city = request.getParameter(CITY);
        String status = request.getParameter(PAGE_STATUS);

        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateUserMainData(login, name, surname, email,
                bDay, bMonth, bYear, sex, country, city);

        User user = (User) session.getAttribute(USER);

        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                User updatedUser = userService.changeUserInfo(user.getId(), login, name, surname, email,
                        bDay, bMonth, bYear, sex, country, city, status);
                session.setAttribute(USER, updatedUser);
                logger.log(Level.INFO, "User " + login + " has successfully change his profile information");
                String successChangeMessageAttr = ConfigurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                request.getSession(true).setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_MESSAGE);

                String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            } catch (ServiceDuplicatedInfoException e) {
                logger.log(Level.WARN, e);
                validationErrors.add(ERROR_USER_ALREADY_EXIST);
                String errorUserValidationAttr = ConfigurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);// try-catch
                request.getSession(true).setAttribute(errorUserValidationAttr, validationErrors);
                String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of user information failed.");
            String errorUserValidationAttr = ConfigurationManager.getProperty(ERROR_USER_VALIDATION_ATTR);// try-catch
            request.getSession(true).setAttribute(errorUserValidationAttr, validationErrors);
            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        }

        return page;
    }
}
