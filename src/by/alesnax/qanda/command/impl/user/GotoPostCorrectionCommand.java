package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 03.01.2017.
 *
 */
@SuppressWarnings("Duplicates")
public class GotoPostCorrectionCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoPostCorrectionCommand.class);

    private static final String USER = "user";
    private static final String POST_ID_ATTR = "post_id";
    private static final String EDIT_POST_ID_ATTR = "attr.edit_post_id";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String GO_TO_MAIN_PAGE_COMMAND = "command.go_to_main_page";
    private static final String ILLEGAL_OPERATION = "warn.illegal_operation_on_other_profile";

    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            logger.log(Level.WARN, "illegal try to correct post from unauthorized user");
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, ILLEGAL_OPERATION);
            String gotoMainPageCommand = configurationManager.getProperty(GO_TO_MAIN_PAGE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoMainPageCommand;
        } else {
            try {
                int postId = Integer.parseInt(request.getParameter(POST_ID_ATTR));
                String editPostIdAttr  = configurationManager.getProperty(EDIT_POST_ID_ATTR);
                session.setAttribute(editPostIdAttr, postId);
                String previousQuery = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
            } catch (NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}