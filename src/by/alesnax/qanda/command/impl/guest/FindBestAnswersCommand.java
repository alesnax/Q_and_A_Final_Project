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
public class FindBestAnswersCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestAnswersCommand.class);

    private static final String LOW_LIMIT = "attr.low_limit";
    private static final String HIGH_LIMIT = "attr.high_limit";

    private static final String BEST_ANSWERS_ATTR = "attr.best_answers";
    private static final String BEST_ANSWERS_PATH = "path.page.best_answers";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();
        String lowLimit = ConfigurationManager.getProperty(LOW_LIMIT);
        String highLimit = ConfigurationManager.getProperty(HIGH_LIMIT);
        List<Post> bestAnswers = postService.findBestAnswers(lowLimit, highLimit);//расширить пост инфой про юзеров
        String bestAnswersAttr = ConfigurationManager.getProperty(BEST_ANSWERS_ATTR);
        request.setAttribute(bestAnswersAttr, bestAnswers);

        String bestAnswersPath = ConfigurationManager.getProperty(BEST_ANSWERS_PATH);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestAnswersPath;

        return page;
    }
}
