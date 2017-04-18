package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.CategoryValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

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
    private static final String TITLE_EN = "title_en";
    private static final String TITLE_RU = "title_ru";
    private static final String DESCRIPTION_EN = "description_en";
    private static final String DESCRIPTION_RU = "description_ru";

    /**
     * Names of attributes of returned parameters to session if validation failed
     */
    private static final String RETURNED_TITLE_EN = "created_title_en";
    private static final String RETURNED_TITLE_RU = "created_title_ru";
    private static final String RETURNED_DESCRIPTION_EN = "created_description_en";
    private static final String RETURNED_DESCRIPTION_RU = "created_description_ru";

    /**
     * Keys of error messages and page_no attributes in loc.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String SUCCESS_CREATE_ATTR = "attr.success_category_create_message";
    private static final String ERROR_CATEGORY_VALIDATION_ATTR = "attr.category_validation_error";
    private static final String PAGE_NO = "attr.page_no";
    private static final String PAGE_NO_QUERY = "command.page_query_part";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String SUCCESS_CREATE_MSG = "category.success_create_msg";
    private static final String SHOW_CATEGORY_CREATION_ATTR = "show_category_creation";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_MODERATED_CATEGORIES_COMMAND = "command.go_to_moderated_categories";

    /**
     * Process creating new category, method checks if attribute user exists in session,
     * and it's role is ADMIN, calls method from service for processing creating,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        String titleEn = request.getParameter(TITLE_EN);
        String titleRu = request.getParameter(TITLE_RU);
        String descriptionEn = request.getParameter(DESCRIPTION_EN);
        String descriptionRu = request.getParameter(DESCRIPTION_RU);
        int pageNo = FIRST_PAGE_NO;
        String pageNoQuery = configurationManager.getProperty(PAGE_NO_QUERY);
        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
        if (request.getParameter(pageNoAttr) != null) {
            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
            if (pageNo < FIRST_PAGE_NO) {
                pageNo = FIRST_PAGE_NO;
            }
        }
        CategoryValidation categoryValidation = new CategoryValidation();
        List<String> validationErrors = categoryValidation.validateNewCategory(titleEn, titleRu, descriptionEn, descriptionRu);

        if (validationErrors.isEmpty()) {
            AdminService adminService = ServiceFactory.getInstance().getAdminService();
            try {
                adminService.createNewCategory(user.getId(), titleEn, titleRu, descriptionEn, descriptionRu);
                String successCreateMessage = configurationManager.getProperty(SUCCESS_CREATE_ATTR);
                session.setAttribute(successCreateMessage, SUCCESS_CREATE_MSG);
                String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQuery + pageNo;
            } catch (ServiceException | NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of creating category failed.");
            String errorCategoryValidationAttr = configurationManager.getProperty(ERROR_CATEGORY_VALIDATION_ATTR);
            session.setAttribute(SHOW_CATEGORY_CREATION_ATTR, true);
            session.setAttribute(RETURNED_TITLE_EN, titleEn);
            session.setAttribute(RETURNED_TITLE_RU, titleRu);
            session.setAttribute(RETURNED_DESCRIPTION_EN, descriptionEn);
            session.setAttribute(RETURNED_DESCRIPTION_RU, descriptionRu);
            session.setAttribute(errorCategoryValidationAttr, validationErrors);
            String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQuery + pageNo;
        }
        return page;
    }
}