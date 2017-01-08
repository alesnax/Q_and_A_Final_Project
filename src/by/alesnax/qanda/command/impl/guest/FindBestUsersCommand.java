package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 13.12.2016.
 */
public class FindBestUsersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestUsersCommand.class);

    private static final String LOW_LIMIT = "attr.low_limit";
    private static final String HIGH_LIMIT = "attr.high_limit";

    private static final String BEST_USERS_ATTR = "attr.best_users";
    private static final String BEST_USERS_PATH = "path.page.best_users";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();

       // List<Post> bestUsers = postService.findBestUsers(lowLimit, highLimit);//расширить пост инфой про юзеров

        String bestUsersAttr = ConfigurationManager.getProperty(BEST_USERS_ATTR);
        //request.setAttribute(bestUsersAttr, bestUsers);

        String bestUsersPath = ConfigurationManager.getProperty(BEST_USERS_PATH);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestUsersPath;

        return page;
    }
}
