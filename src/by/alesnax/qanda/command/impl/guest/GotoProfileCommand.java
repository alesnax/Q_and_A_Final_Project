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

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 22.12.2016.
 */
public class GotoProfileCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoProfileCommand.class);

    private static final String USER = "user";
    private static final String USER_ID = "user_id";
    private static final String USER_MAIN_PAGE = "path.page.profile";
    private static final String LOW_LIMIT = "low_limit";
    private static final String HIGH_LIMIT = "high_limit";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
        UserService userService = ServiceFactory.getInstance().getUserService();


        User user = (User) session.getAttribute(USER);
        int sessionUserId = (user != null ? user.getId() : 0);


        String requestUserId = request.getParameter(USER_ID);
        int profileUserId = 0;
        if (requestUserId != null || !requestUserId.isEmpty()) {
            profileUserId = Integer.parseInt(request.getParameter(USER_ID));
        }
        int showedUserId = 0;
        if (profileUserId != 0) {
            showedUserId = profileUserId;
        } else if (sessionUserId != 0) {
            showedUserId = sessionUserId;
        } else {
            return page;
        }


        // решить проблему атрибутов с залогиненным юзером
        // одна страница профиля
        try {


            User showedUser = userService.findUserById(showedUserId);
            List<Post> posts = null;
           /* if (sessionUserId != 0) {
                posts = postService.findQuestionsByUserId(showedUserId, sessionUserId);
            } else {*/
                posts = postService.findQuestionsByUserId(showedUserId);
          //  }
            // положить посты в реквест и достать их, решить затем проблему с выбором вопросов или ответов юзера
            request.setAttribute(USER, showedUser);
            String userProfilePath = ConfigurationManager.getProperty(USER_MAIN_PAGE);
/*
            String showPagePoint = (String) session.getAttribute("show_page_point");
            if(showPagePoint != null && !showPagePoint.isEmpty()){
                userProfilePath += showPagePoint;
                session.removeAttribute("show_page_point");
            }*/

            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + userProfilePath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }

        return page;
    }
}

