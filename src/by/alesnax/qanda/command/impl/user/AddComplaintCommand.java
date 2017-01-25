package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceDuplicatedInfoException;
import by.alesnax.qanda.service.impl.ServiceException;
import by.alesnax.qanda.validation.ComplaintValidation;
import by.alesnax.qanda.validation.PostValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 13.01.2017.
 */
public class AddComplaintCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddComplaintCommand.class);

    private static final String POST_ID = "post_id";
    private static final String COMPLAINT_DESCRIPTION = "complaint_description";
    private static final String CORRECTED_COMPLAINT_DESCRIPTION = "corrected_complaint_description";

    private static final String USER_ATTR = "user";

    private static final String COMPLAINT_ID_ATTR = "attr.complaint_id";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String COMPLAINT_VALIDATION_FAILED_ATTR = "attr.complaint_validation_failed";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String COMPLAINT_ALREADY_EXIST = "warn.complaint_already_exist";

    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        String description = request.getParameter(COMPLAINT_DESCRIPTION);
        ComplaintValidation complaintValidation = new ComplaintValidation();
        List<String> validationErrors = complaintValidation.validateComplaint(description);
        String postId = request.getParameter(POST_ID);
        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            if (user != null) {
                PostService postService = ServiceFactory.getInstance().getPostService();
                try {
                    int complaintPostId = Integer.parseInt(request.getParameter(POST_ID));
                    postService.addNewComplaint(user.getId(), complaintPostId, description);
                    String nextCommand = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                } catch (ServiceDuplicatedInfoException e) {
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, COMPLAINT_ALREADY_EXIST);
                    String previousQuery = QueryUtil.getPreviousQuery(request);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
                } catch (ServiceException | NumberFormatException e) {
                    logger.log(Level.ERROR, e);
                    String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                    request.setAttribute(errorMessageAttr, e.getMessage());
                    page = ERROR_REQUEST_TYPE;
                }
            } else {
                String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
                session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_MAKE_OPERATION);
                String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding complaint failed.");
            String complaintFailedAttr = configurationManager.getProperty(COMPLAINT_VALIDATION_FAILED_ATTR);
            session.setAttribute(CORRECTED_COMPLAINT_DESCRIPTION, description);
            session.setAttribute(complaintFailedAttr, validationErrors);
            String complaintIdAttr = configurationManager.getProperty(COMPLAINT_ID_ATTR);
            session.setAttribute(complaintIdAttr, postId);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}