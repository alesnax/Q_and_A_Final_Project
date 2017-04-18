package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

//static import

/**
 * Class which method deletes attributes of question data, saved into session.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class CleanQuestionFormCommand implements Command {
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String TITLE = "question_title";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";


    /**
     * Removes attributes of question data from session and returns value of previous query from session.
     *
     * @param request Processed HttpServletRequest
     * @return previous query.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;

        QueryUtil.logQuery(request);
        HttpSession session = request.getSession(true);
        session.removeAttribute(TITLE);
        session.removeAttribute(CATEGORY);
        session.removeAttribute(DESCRIPTION);

        String nextCommand = QueryUtil.getPreviousQuery(request);
        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        return page;
    }
}