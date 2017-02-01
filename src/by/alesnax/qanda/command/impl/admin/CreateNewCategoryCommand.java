package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.CategoryValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Creates new category. Access for command is only for users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class CreateNewCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(CreateNewCategoryCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String TITLE_EN = "title_en";
    private static final String TITLE_RU = "title_ru";
    private static final String DESCRIPTION_EN = "description_en";
    private static final String DESCRIPTION_RU = "description_ru";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String SUCCESS_CREATE_ATTR = "attr.success_category_create_message";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String ERROR_CATEGORY_VALIDATION_ATTR = "attr.category_validation_error";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String SUCCESS_CREATE_MSG = "category.success_create_msg";
    private static final String SHOW_CATEGORY_CREATION_ATTR = "show_category_creation";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * Process creating new category, method checks if attribute user exists in session,
     * and it's role is ADMIN, calls method from service for processing creating,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to moderated categories page if success scenario or error, authorization or profile page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case ADMIN_ROLE:
                    String titleEn = request.getParameter(TITLE_EN);
                    String titleRu = request.getParameter(TITLE_RU);
                    String descriptionEn = request.getParameter(DESCRIPTION_EN);
                    String descriptionRu = request.getParameter(DESCRIPTION_RU);

                    CategoryValidation categoryValidation = new CategoryValidation();
                    List<String> validationErrors = categoryValidation.validateNewCategory(titleEn, titleRu, descriptionEn, descriptionRu);

                    if (validationErrors.isEmpty()) {
                        AdminService adminService = ServiceFactory.getInstance().getAdminService();
                        try {
                            adminService.createNewCategory(user.getId(), titleEn, titleRu, descriptionEn, descriptionRu);
                            String successCreateMessage = configurationManager.getProperty(SUCCESS_CREATE_ATTR);
                            session.setAttribute(successCreateMessage, SUCCESS_CREATE_MSG);
                            String previousQuery = QueryUtil.getPreviousQuery(request);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
                        } catch (ServiceException e) {
                            logger.log(Level.ERROR, e);
                            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                            page = ERROR_REQUEST_TYPE;
                        }
                    } else {
                        logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of creating category failed.");
                        String errorCategoryValidationAttr = configurationManager.getProperty(ERROR_CATEGORY_VALIDATION_ATTR);
                        session.setAttribute(SHOW_CATEGORY_CREATION_ATTR, true);
                        session.setAttribute(errorCategoryValidationAttr, validationErrors);
                        String previousQuery = QueryUtil.getPreviousQuery(request);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
                    }
                    break;
                case USER_ROLE:
                case MODERATOR_ROLE:
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