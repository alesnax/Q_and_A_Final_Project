package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 08.12.2016.
 */

public class GotoMainPageCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoMainPageCommand.class);

    // для гостя переброс на список категорий для юзера на главную

    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moder";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ATTR = "user";

    private static final String GO_TO_CATEGORIES_COMMAND = "command.go_to_quest_categories";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String USER_MAIN_PAGE = "path.page.profile";
    private static final String LOW_LIMIT = "low_limit";
    private static final String HIGH_LIMIT = "high_limit";

   // private static final String SHORT_CATEGORIES_ATTR = "attr.request.categories_info";

   // private static final String ERROR_MESSAGE_ATTR = "attr.error_msg_1";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        String userMainPage = ConfigurationManager.getProperty(USER_MAIN_PAGE);


        HttpSession session = request.getSession(true);

        //  было сохранение превкьюери стало лог
        QueryUtil.logQuery(request);
        PostService postService = ServiceFactory.getInstance().getPostService();

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String gotoCategoriesCommand = ConfigurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoCategoriesCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case USER_ROLE:
                case MODERATOR_ROLE:
                case ADMIN_ROLE:
                    String gotoProfileCommand = ConfigurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
                default:
                    String gotoCategoriesCommand = ConfigurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoCategoriesCommand;
                    break;
            }
        }
        return page;
    }
}