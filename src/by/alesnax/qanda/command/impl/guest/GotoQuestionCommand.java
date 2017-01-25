package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
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
 * Created by alesnax on 04.01.2017.
 */
public class GotoQuestionCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoQuestionCommand.class);

    private static final String USER = "user";
    private static final String QUESTION_PAGE = "path.page.question";
    private static final String QUESTION_ATTR = "attr.request.question";
    private static final String QUESTION_ID_ATTR = "question_id";
    private static final String NO_SUCH_QUESTION_MESSAGE = "question.message.no_such_question_msg";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String PARAMETER_NOT_FOUND_MESSAGE = "error.error_msg.parameter_not_found";
    private static final String GO_TO_MAIN_PAGE_COMMAND = "command.go_to_main_page";


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        int userId = (user != null) ? user.getId() : 0;

        String questionId = request.getParameter(QUESTION_ID_ATTR);
        if (questionId == null || questionId.isEmpty()) {
            logger.log(Level.ERROR, "Wrong parameter, question_id expected, but wasn't found.");
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            String nextCommand = configurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
            session.setAttribute(wrongCommandMessageAttr, PARAMETER_NOT_FOUND_MESSAGE);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            PostService postService = ServiceFactory.getInstance().getPostService();
            try {
                int postId = Integer.parseInt(questionId);
                List<Post> question = postService.findQuestionWithAnswersById(postId, userId);
                if (question == null || question.isEmpty()) {
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    String nextCommand = configurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
                    session.setAttribute(wrongCommandMessageAttr, NO_SUCH_QUESTION_MESSAGE);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } else {
                    String questionAttr = configurationManager.getProperty(QUESTION_ATTR);
                    request.setAttribute(questionAttr, question);
                    String questionPath = configurationManager.getProperty(QUESTION_PAGE);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + questionPath;
                }
            } catch (NumberFormatException | ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
