package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

// static import
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

public class ChangeLanguageCommand implements Command {
    private static final String LANGUAGE = "language";
    private static final String LOCALE = "locale";
    private static final String ENGLISH = "en";
    private static final String RUSSIAN = "ru";

    private ArrayList<String> supportedLanguages = new ArrayList<>();

    public ChangeLanguageCommand() {
        supportedLanguages.add(ENGLISH);
        supportedLanguages.add(RUSSIAN);
    }

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
