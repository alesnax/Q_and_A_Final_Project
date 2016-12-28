package by.alesnax.qanda.command.factory;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.GotoFirstPageCommand;
import by.alesnax.qanda.command.impl.guest.GotoMainPageCommand;
import by.alesnax.qanda.command.client.CommandHelper;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.MissingResourceException;

/**
 * Created by alesnax on 23.12.2016.
 */
public class CommandFactory {
    private static Logger logger = LogManager.getLogger(CommandFactory.class);

    private static final String COMMAND = "command";
    private static final String USER = "user";
    private static final String GUEST = "guest";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String EMPTY_COMMAND_MESSAGE = "error.error_msg.empty_command";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String ILLEGAL_SESSION_ACCESS_MESSAGE = "error.error_msg.illegal_command";

    public Command defineCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Command command = new GotoMainPageCommand();
        String commandName = request.getParameter(COMMAND);
        String role = GUEST;
        if (commandName != null && !commandName.isEmpty()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute(USER);
                if (user != null) {
                    role = user.getRole().getValue();
                }
            } else {
                logger.log(Level.ERROR, "Illegal access to session from client, session time is over ");
                request.getSession();
                addWrongCommandMessage(request, ILLEGAL_SESSION_ACCESS_MESSAGE, commandName);
                return new GotoFirstPageCommand();
            }
            command = CommandHelper.getInstance().getCommand(role, commandName);
            if (command == null) {
                logger.log(Level.ERROR, "Illegal access, Command " + commandName + " wasn't found for role " + role);
                addWrongCommandMessage(request, UNDEFINED_COMMAND_MESSAGE, commandName);
                command = new GotoMainPageCommand();
            }
        } else {
            logger.log(Level.ERROR, "Empty command found for role " + role);
            addWrongCommandMessage(request, EMPTY_COMMAND_MESSAGE, commandName);
        }
        return command;
    }

    private void addWrongCommandMessage(HttpServletRequest request, String wrongCommandMessage, String commandName) {
        try {
            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            request.getSession().setAttribute(wrongCommandMessageAttr, wrongCommandMessage);
        } catch (MissingResourceException e) {
            logger.log(Level.FATAL, e + " after command: " + commandName);
            throw new RuntimeException(e);
        }
    }
}


