package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 13.12.2016.
 */
public class SearchAnswerQuestCommand implements Command {
    private static Logger logger = LogManager.getLogger(SearchAnswerQuestCommand.class);

    private static final String AUTHORIZATION_PAGE = "path.page.user_authorization";

    @Override
    public String execute(HttpServletRequest request) {
        String page;
        // temporary
        String path = ConfigurationManager.getProperty(AUTHORIZATION_PAGE);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + path;
        return page;
    }
}
