package by.alesnax.qanda.command.factory;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.client.CommandHelper;
import by.alesnax.qanda.command.impl.guest.GotoFirstPageCommand;
import by.alesnax.qanda.command.impl.guest.GotoMainPageCommand;
import by.alesnax.qanda.command.impl.user.UploadFileCommand;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Checks HttpRequest request command parameter , if it correct for current user and they role,
 * and if request of multiform/data format and returns related to this parameters {@code Command}
 *
 * @author alesnax
 * @see Command
 * @see by.alesnax.qanda.command.client.CommandName
 */
public class CommandFactory {
    private static Logger logger = LogManager.getLogger(CommandFactory.class);

    private static final String COMMAND = "command";
    private static final String USER = "user";
    private static final String GUEST = "guest";
    /**
     * Keys of error messages which are situated at loc.properties file
     */
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String EMPTY_COMMAND_MESSAGE = "error.error_msg.empty_command";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String ILLEGAL_SESSION_ACCESS_MESSAGE = "error.error_msg.illegal_command";

    /**
     * Defines command parameter of {@code HttpServletRequest} request and if it correct,
     * permission fore role, type if absent and return default command if command parameter
     * incorrect or absent for user role.
     * If session is out, returns default command and put error message into session
     *
     * @param request processed request
     * @return {@code Command} command
     * @throws ServletException
     * @throws IOException
     */
    public Command defineCommand(HttpServletRequest request) throws ServletException, IOException {
        Command command = new GotoMainPageCommand();
        String commandName = request.getParameter(COMMAND);
        String role = GUEST;
        try {
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
                    addWrongCommandMessage(request, ILLEGAL_SESSION_ACCESS_MESSAGE);
                    return new GotoFirstPageCommand();
                }
                command = CommandHelper.getInstance().getCommand(role, commandName);
                if (command == null) {
                    logger.log(Level.ERROR, "Illegal access, Command " + commandName + " wasn't found for role '" + role + "'");
                    addWrongCommandMessage(request, UNDEFINED_COMMAND_MESSAGE);
                    command = new GotoMainPageCommand();
                }
            } else if (ServletFileUpload.isMultipartContent(request)) {
                command = new UploadFileCommand();
            } else {
                logger.log(Level.ERROR, "Empty command found for role " + role);
                addWrongCommandMessage(request, EMPTY_COMMAND_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.ERROR, "Command " + commandName + " wasn't found for role " + role);
            addWrongCommandMessage(request, UNDEFINED_COMMAND_MESSAGE);
            command = new GotoMainPageCommand();
        }
        return command;
    }

    /**
     * Put error message into session if session is out or command parameter is incorrect for user role.
     *
     * @param request which session will be used to put error message attribute into
     * @param wrongCommandMessage message that will be put into session
     */
    private void addWrongCommandMessage(HttpServletRequest request, String wrongCommandMessage) {
        ConfigurationManager configurationManager = new ConfigurationManager();
        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
        request.getSession().setAttribute(wrongCommandMessageAttr, wrongCommandMessage);
    }
}


