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
public class FindBestQuestionsCommand implements Command {
    private static Logger logger = LogManager.getLogger(FindBestQuestionsCommand.class);

    private static final String LOW_LIMIT = "attr.low_limit";
    private static final String HIGH_LIMIT = "attr.high_limit";

    private static final String BEST_QUESTIONS_ATTR = "attr.best_questions";
    private static final String BEST_QUESTIONS_PATH = "path.page.best_questions";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        QueryUtil.savePreviousQueryToSession(request);

        PostService postService = ServiceFactory.getInstance().getPostService();

        String lowLimit = ConfigurationManager.getProperty(LOW_LIMIT);
        String highLimit = ConfigurationManager.getProperty(HIGH_LIMIT);
        List<Post> bestQuestions = postService.findBestQuestions(lowLimit, highLimit);//расширить пост инфой про юзеров
        String bestQuestionsAttr = ConfigurationManager.getProperty(BEST_QUESTIONS_ATTR);
        request.setAttribute(bestQuestionsAttr, bestQuestions);

        String bestQuestionsPath = ConfigurationManager.getProperty(BEST_QUESTIONS_PATH);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + bestQuestionsPath;

        return page;
    }
}
