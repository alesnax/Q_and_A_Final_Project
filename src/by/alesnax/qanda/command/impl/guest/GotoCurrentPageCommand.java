package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 10.01.2017.
 *
 */
public class GotoCurrentPageCommand implements Command {

    @Override
    public String execute(HttpServletRequest request) {
        String page;
        QueryUtil.logQuery(request);
        String previousQuery = QueryUtil.getPreviousQuery(request);
        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        return page;
    }
}
