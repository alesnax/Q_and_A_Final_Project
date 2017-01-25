package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.admin.CreateNewCategoryCommand;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.CategoryValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 24.01.2017.
 */
public class CorrectCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(CorrectCategoryCommand.class);

    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String NO_MODERATOR_FOR_CATEGORY = "error.error_msg.no_such_user_for_category";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String SUCCESS_UPDATE_ATTR = "attr.success_category_create_message";
    private static final String SUCCESS_UPDATE_MSG = "category.success_update_msg";
    private static final String ERROR_CATEGORY_VALIDATION_ATTR = "attr.correct_category_validation_error";
    private static final String SHOW_CATEGORY_CORRECTION_ATTR = "show_category_correction";
    private static final String GO_TO_MODERATED_CATEGORIES = "command.go_to_moderated_categories";
    private static final String PAGE_NO_QUERY_PART = "command.page_query_part";
    private static final String PAGE_NO = "attr.page_no";
    private static final String CORRECTED_TITLE_EN = "corrected_title_en";
    private static final String CORRECTED_TITLE_RU = "corrected_title_ru";
    private static final String CORRECTED_DESCRIPTION_EN = "corrected_description_en";
    private static final String CORRECTED_DESCRIPTION_RU = "corrected_description_ru";
    private static final String CORRECTED_MODERATOR = "corrected_moderator";
    private static final String CATEGORY_STATUS = "category_status";
    private static final String CATEGORY_ID = "category_id";


    @Override
    public String execute(HttpServletRequest request) {
        String page;
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
            String titleEn = request.getParameter(CORRECTED_TITLE_EN);
            String titleRu = request.getParameter(CORRECTED_TITLE_RU);
            String descriptionRu = request.getParameter(CORRECTED_DESCRIPTION_RU);
            String descriptionEn = request.getParameter(CORRECTED_DESCRIPTION_EN);
            String categoryStatus = request.getParameter(CATEGORY_STATUS);
            String categoryId = request.getParameter(CATEGORY_ID);
            int pageNo = 1;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < 1) {
                    pageNo = 1;
                }
            }
            CategoryValidation categoryValidation = new CategoryValidation();
            List<String> validationErrors = null;

            switch (role) {
                case ADMIN_ROLE:
                    String login = request.getParameter(CORRECTED_MODERATOR);
                    validationErrors = categoryValidation.validateCorrectedCategory(titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);

                    if (validationErrors.isEmpty()) {
                        AdminService adminService = ServiceFactory.getInstance().getAdminService();
                        try {
                            boolean updated = adminService.correctCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
                            if (updated) {
                                String successUpdateMessage = configurationManager.getProperty(SUCCESS_UPDATE_ATTR);
                                session.setAttribute(successUpdateMessage, SUCCESS_UPDATE_MSG);
                            } else {
                                logger.log(Level.WARN, "User id=" + user.getId() + " :Updating of correcting category failed, no moderator with login:" + login);
                                String errorCategoryValidationAttr = configurationManager.getProperty(ERROR_CATEGORY_VALIDATION_ATTR);
                                session.setAttribute(SHOW_CATEGORY_CORRECTION_ATTR, categoryId);
                                validationErrors = new ArrayList<>();
                                validationErrors.add(NO_MODERATOR_FOR_CATEGORY);
                                session.setAttribute(CORRECTED_TITLE_EN, titleEn);
                                session.setAttribute(CORRECTED_TITLE_RU, titleRu);
                                session.setAttribute(CORRECTED_DESCRIPTION_EN, descriptionEn);
                                session.setAttribute(CORRECTED_DESCRIPTION_RU, descriptionRu);
                                session.setAttribute(CORRECTED_MODERATOR, login);
                                session.setAttribute(CATEGORY_STATUS, categoryStatus);
                                session.setAttribute(errorCategoryValidationAttr, validationErrors);
                            }
                            String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);
                            String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
                        } catch (ServiceException e) {
                            logger.log(Level.ERROR, e);
                            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                            request.setAttribute(errorMessageAttr, e.getMessage());
                            page = ERROR_REQUEST_TYPE;
                        }
                    } else {
                        logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of correcting category failed.");
                        String errorCategoryValidationAttr = configurationManager.getProperty(ERROR_CATEGORY_VALIDATION_ATTR);
                        String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);

                        session.setAttribute(CORRECTED_TITLE_EN, titleEn);
                        session.setAttribute(CORRECTED_TITLE_RU, titleRu);
                        session.setAttribute(CORRECTED_DESCRIPTION_EN, descriptionEn);
                        session.setAttribute(CORRECTED_DESCRIPTION_RU, descriptionRu);
                        session.setAttribute(CORRECTED_MODERATOR, login);
                        session.setAttribute(CATEGORY_STATUS, categoryStatus);

                        session.setAttribute(SHOW_CATEGORY_CORRECTION_ATTR, categoryId);
                        session.setAttribute(errorCategoryValidationAttr, validationErrors);
                        String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
                    }
                    break;
                case MODERATOR_ROLE:
                    validationErrors = categoryValidation.validateCorrectedCategory(titleEn, titleRu, descriptionEn, descriptionRu, categoryStatus);
                    if (validationErrors.isEmpty()) {
                        ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                        try {
                            moderatorService.correctCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, categoryStatus);
                            String successUpdateMessage = configurationManager.getProperty(SUCCESS_UPDATE_ATTR);
                            session.setAttribute(successUpdateMessage, SUCCESS_UPDATE_MSG);
                            String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);
                            String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
                        } catch (ServiceException e) {
                            logger.log(Level.ERROR, e);
                            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                            request.setAttribute(errorMessageAttr, e.getMessage());
                            page = ERROR_REQUEST_TYPE;
                        }
                    } else {
                        logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of correcting category failed.");
                        String errorCategoryValidationAttr = configurationManager.getProperty(ERROR_CATEGORY_VALIDATION_ATTR);
                        session.setAttribute(SHOW_CATEGORY_CORRECTION_ATTR, categoryId);
                        session.setAttribute(errorCategoryValidationAttr, validationErrors);
                        session.setAttribute(CORRECTED_TITLE_EN, titleEn);
                        session.setAttribute(CORRECTED_TITLE_RU, titleRu);
                        session.setAttribute(CORRECTED_DESCRIPTION_EN, descriptionEn);
                        session.setAttribute(CORRECTED_DESCRIPTION_RU, descriptionRu);
                        session.setAttribute(CATEGORY_STATUS, categoryStatus);
                        String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);
                        String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
                    }
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