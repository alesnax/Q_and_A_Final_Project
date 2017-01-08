package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 22.12.2016.
 */
public class GotoProfileCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoProfileCommand.class);

    private static final String USER = "user";
    private static final String USER_ID = "user_id";
    private static final String USER_PROFILE_PATH = "path.page.profile";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String QUESTIONS_ATTR = "attr.request.questions";

    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String USER_ID_NOT_NUMBER = "profile.error.message.user_id_not_number";




    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = ConfigurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = ConfigurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            PostService postService = ServiceFactory.getInstance().getPostService();
            UserService userService = ServiceFactory.getInstance().getUserService();


            String requestUserId = request.getParameter(USER_ID);
            int profileUserId = user.getId();
            if (requestUserId != null && !requestUserId.isEmpty()) {
                try {
                    profileUserId = Integer.parseInt(requestUserId);
                } catch (NumberFormatException e) {
                    logger.log(Level.ERROR, e);
                    String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, USER_ID_NOT_NUMBER);
                }
            }
            try {
                //добавить вывод статистики о пользователе
                // проверить что будет если ид некорректный например 777
                User showedUser = userService.findUserById(profileUserId, user.getId());
                request.setAttribute(USER, showedUser);

                List<Post> posts = postService.findQuestionsByUserId(profileUserId, user.getId());
                String questionsAttr = ConfigurationManager.getProperty(QUESTIONS_ATTR);
                request.setAttribute(questionsAttr, posts);


                // положить посты в реквест и достать их, решить затем проблему с выбором вопросов или ответов юзера
                // запрос статистики юзера
                String userProfilePath = ConfigurationManager.getProperty(USER_PROFILE_PATH);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + userProfilePath;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}

