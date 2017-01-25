package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 12.12.2016.
 */
public class LogOutCommand implements Command {
    private static final String INDEX_PAGE = "path.page.index";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();

        QueryUtil.logQuery(request);
        HttpSession session = request.getSession(false);
        String prevLang = (String) request.getSession().getAttribute("locale");
        if (session != null) {
            session.invalidate();
            request.getSession().setAttribute("locale", prevLang);
            String indexPage = configurationManager.getProperty(INDEX_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + indexPage;
        }
        return page;
    }
}
