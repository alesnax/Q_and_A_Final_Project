package by.alesnax.qanda.command.util;

import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Class QueryUtil process query from request and put it into session or return previous query from session,
 * that can point address for redirecting for some commands.
 *
 * @author alesnax
 * @see HttpServletRequest
 */
public class QueryUtil {
    private static Logger logger = LogManager.getLogger(QueryUtil.class);

    private static final String USER_ATTR = "user";
    private static final String GUEST = "guest";
    private static final String PREV_QUERY = "prev_query";
    private static final char QUERY_START_SEPARATOR = '?';
    private static final char PARAMETER_SEPARATOR = '&';
    private static final char VALUE_SEPARATOR = '=';
    private static final String INDEX_PAGE = "path.page.index";

    /**
     * Don't let anyone instantiate this class.
     */
    private QueryUtil() {
    }

    /**
     * Save query to session as REV_QUERY attribute, so it could help to find previous query for
     * commands which should be redirected to the same page after processing some actions
     *
     * @param request
     */
    public static void savePreviousQueryToSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String query = logQuery(request);
        session.setAttribute(PREV_QUERY, query);
    }

    /**
     * Return attribute PREV_QUERY from session which contains query of previous request.
     * Check if previous query exists and returns query with index page in such case.
     *
     * @param request
     * @return query value of previous request or query with index page if query doesn't exist
     */
    public static String getPreviousQuery(HttpServletRequest request) {
        ConfigurationManager configurationManager = new ConfigurationManager();
        String prevQuery = (String) request.getSession(false).getAttribute(PREV_QUERY);
        if (prevQuery == null) {
            prevQuery = configurationManager.getProperty(INDEX_PAGE);
        }
        return prevQuery;
    }

    /**
     * Makes logging of query of request and returns it for following using
     *
     * @param request
     * @return value request query
     */
    public static String logQuery(HttpServletRequest request) {
        String query = createHttpQueryString(request);
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            logger.log(Level.INFO, GUEST + " called " + query);
        } else {
            String login = user.getLogin();
            String role = user.getRole().getValue();
            int id = user.getId();
            logger.log(Level.INFO, role + " " + login + " (id=" + id +") called " + query);
        }
        return query;
    }

    /**
     * creates query from request
     * @param request
     * @return request query
     */
    private static String createHttpQueryString(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        StringBuffer query = new StringBuffer();

        Enumeration<String> params = request.getParameterNames();

        String key;
        String value;
        while (params.hasMoreElements()) {
            key = params.nextElement();
            value = request.getParameter(key);
            query = query.append(PARAMETER_SEPARATOR).append(key).append(VALUE_SEPARATOR).append(value);
        }

        String result;
        if (query.length() == 0) {
            result = url.toString();
        } else {
            query.deleteCharAt(0);
            result = url.append(QUERY_START_SEPARATOR).append(query).toString();
        }
        return result;
    }

}
