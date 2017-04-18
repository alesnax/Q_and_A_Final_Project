package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

//static import

/**
 * Command has method that invalidates session and redirects to index.jsp page
 *
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class LogOutCommand implements Command {
    /**
     * Keys of first page located in config.properties file
     */
    private static final String INDEX_PAGE = "path.page.index";

    /**
     * locale attribute
     */
    private static final String LOCALE_ATTR = "locale";

    /**
     * method invalidates session, remembers locale of invalidated session and put it
     * as an attribute into new session
     *
     * @param request Processed HttpServletRequest
     * @return value of index.jsp page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();

        QueryUtil.logQuery(request);
        HttpSession session = request.getSession(false);
        String prevLang = (String) session.getAttribute(LOCALE_ATTR);
        if (session != null) {
            session.invalidate();
        }
        request.getSession().setAttribute(LOCALE_ATTR, prevLang);
        String indexPage = configurationManager.getProperty(INDEX_PAGE);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + indexPage;
        return page;
    }
}
