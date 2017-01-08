package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
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
 * Created by alesnax on 07.01.2017.
 */
public class ChangeUserLanguageCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserLanguageCommand.class);

    private static final String USER = "user";
    private static final String LANGUAGE = "language";
    private static final String LOCALE = "locale";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    private static final String SUCCESS_CHANGE_LANG_MESSAGE = "edit_profile.message.lang_change_saved";
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        String language = request.getParameter(LANGUAGE);
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        UserService userService = ServiceFactory.getInstance().getUserService();

        try {
            userService.changeUserLanguage(user.getId(), language);
            user.setLanguage(User.Language.valueOf(language.toUpperCase()));
            session.setAttribute(LOCALE, language);
            logger.log(Level.INFO, "User " + user.getId() + " (" + user.getLogin() + ") has successfully change his used language.");
            String successChangeMessageAttr = ConfigurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
            session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_LANG_MESSAGE);
            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
