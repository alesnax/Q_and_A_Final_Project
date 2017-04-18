package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

// static import

/**
 * Class process changing locale for current session and returning to previous page.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class ChangeLanguageCommand implements Command {

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String LANGUAGE = "language";
    private static final String LOCALE = "locale";
    private static final String ENGLISH = "en";
    private static final String RUSSIAN = "ru";

    /**
     * Container for available locales
     */
    private ArrayList<String> supportedLanguages = new ArrayList<>();

    /**
     * Constructor, there is putting in supportedLanguages list available locale values
     */
    public ChangeLanguageCommand() {
        supportedLanguages.add(ENGLISH);
        supportedLanguages.add(RUSSIAN);
    }

    /**
     * changes current locale into session and returns value of previous query
     *
     * @param request Processed HttpServletRequest
     * @return value of previous page where processed request will be send back
     */
    @Override
    public String execute(HttpServletRequest request) {
        String language = request.getParameter(LANGUAGE);

        QueryUtil.logQuery(request);

        HttpSession session = request.getSession(true);
        if (language != null) {
            if (!supportedLanguages.contains(language)) {
                language = ENGLISH;
            }
            session.setAttribute(LOCALE, language);
        }
        String previousQuery = QueryUtil.getPreviousQuery(request);

        return RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
    }
}
