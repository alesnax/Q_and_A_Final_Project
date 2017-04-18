package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.ComplaintValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class has method processing adding new complaint decision.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class AddComplaintDecisionCommand implements Command {
    private static Logger logger = LogManager.getLogger(AddComplaintDecisionCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String POST_ID = "post_id";
    private static final String AUTHOR_ID = "author_id";
    private static final String COMPLAINT_DECISION = "complaint_decision";
    private static final String COMPLAINT_STATUS = "status";
    private static final String PROCESSED_POST_ID_ATTR = "process_post_id";
    private static final String PROCESSED_USER_ID_ATTR = "process_author_id";
    private static final String INVALIDATED_DECISION = "invalidated_decision";
    private static final String USER_ATTR = "user";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String COMPLAINT_VALIDATION_FAILED_ATTR = "attr.complaint_validation_failed";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * process adding new complaint decision. Checks if attribute user exists in session and validates decision content,
     * calls processing method from service layer, if success scenario - returns to previous page, otherwise
     * returns to error , authorisation or previous page with error message
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        String decision = request.getParameter(COMPLAINT_DECISION);
        ComplaintValidation complaintValidation = new ComplaintValidation();
        List<String> validationErrors = complaintValidation.validateComplaintDecision(decision);
        String postId = request.getParameter(POST_ID);
        String authorId = request.getParameter(AUTHOR_ID);
        if (validationErrors.isEmpty()) {
            User user = (User) session.getAttribute(USER_ATTR);
            ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
            try {
                int complaintPostId = Integer.parseInt(postId);
                int complaintAuthorId = Integer.parseInt(authorId);
                int status = Integer.parseInt(request.getParameter(COMPLAINT_STATUS));
                moderatorService.addComplaintDecision(user.getId(), complaintPostId, complaintAuthorId, decision, status);
                String nextCommand = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            } catch (ServiceException | NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "Validation of adding complaint decision failed.");
            String complaintFailedAttr = configurationManager.getProperty(COMPLAINT_VALIDATION_FAILED_ATTR);
            session.setAttribute(complaintFailedAttr, validationErrors);
            session.setAttribute(PROCESSED_POST_ID_ATTR, postId);
            session.setAttribute(PROCESSED_USER_ID_ATTR, authorId);
            session.setAttribute(INVALIDATED_DECISION, decision);
            String previousQuery = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + previousQuery;
        }
        return page;
    }
}
