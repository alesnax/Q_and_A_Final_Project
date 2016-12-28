package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 21.12.2016.
 */
public class GotoCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoCategoryCommand.class);

    private static final String CATEGORY_PAGE = "path.page.category";
    private static final String CATEGORY_QUESTIONS_ATTR = "attr.request.cat_questions";
    private static final String CATEGORY_ID_ATTR = "cat_id";
    private static final String NO_QUESTIONS_ATTR = "attr.request.category.no_questions";
    private static final String NO_QUESTIONS_YET_MESSAGE = "category.message.no_questions";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        String categoryId = request.getParameter(CATEGORY_ID_ATTR);
        PostService postService = ServiceFactory.getInstance().getPostService();
        try {
            List<Post> questions = postService.takeQuestionsByCategoryList(categoryId);
            if (questions == null || questions.isEmpty()) {
                String noQuestionsAttr = ConfigurationManager.getProperty(NO_QUESTIONS_ATTR);
                request.setAttribute(noQuestionsAttr, NO_QUESTIONS_YET_MESSAGE);
            } else {
                String categoryQuestionsAttr = ConfigurationManager.getProperty(CATEGORY_QUESTIONS_ATTR);
                request.setAttribute(categoryQuestionsAttr, questions);
            }
            String categoryPath = ConfigurationManager.getProperty(CATEGORY_PAGE);


          /*  String showPagePoint = (String) session.getAttribute("show_page_point");
            if(showPagePoint != null && !showPagePoint.isEmpty()){
                categoryPath += showPagePoint;
                session.removeAttribute("show_page_point");
            }
*/

            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + categoryPath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
