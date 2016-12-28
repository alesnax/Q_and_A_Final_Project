package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 13.12.2016.
 */
public class CleanQuestionFormCommand implements Command {
    private static final String TITLE = "question_title";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

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