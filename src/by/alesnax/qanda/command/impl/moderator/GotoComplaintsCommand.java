package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static imports
import static by.alesnax.qanda.constant.CommandConstants.*;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Class contains method that process taking list of complaints and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoComplaintsCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoComplaintsCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    /**
     * Keys of error messages attributes and page_no attribute that are located in config.properties file
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String COMPLAINTS_ATTR = "attr.request.complaints";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of returned commands or page that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String COMPLAINTS_PAGE = "path.page.complaints";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * method processes taking list of complaints and returns it as attribute set into request.
     * Access for users with role ADMIN or MODERATOR. Method calls method from service layer,
     * which returns list of posts which is set as attribute into request and can
     * throw ServiceException after which method returns value of error500 page.
     * Returns value of complaints page string or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of complaints page or error page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case MODERATOR_ROLE:
                    try {
                        ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                        int pageNo = FIRST_PAGE_NO;
                        int startComplaint = START_ITEM_NO;
                        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                        if (request.getParameter(pageNoAttr) != null) {
                            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                            if (pageNo < FIRST_PAGE_NO) {
                                pageNo = FIRST_PAGE_NO;
                            }
                            startComplaint = (pageNo - FIRST_PAGE_NO) * COMPLAINTS_PER_PAGE;
                        }
                        PaginatedList<Complaint> complaints = moderatorService.findComplaintsByModeratorId(userId, startComplaint, COMPLAINTS_PER_PAGE);
                        String complaintsAttr = configurationManager.getProperty(COMPLAINTS_ATTR);
                        String complaintsPath = configurationManager.getProperty(COMPLAINTS_PAGE);
                        request.setAttribute(complaintsAttr, complaints);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + complaintsPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case ADMIN_ROLE:
                    try {
                        AdminService adminService = ServiceFactory.getInstance().getAdminService();
                        int pageNo = FIRST_PAGE_NO;
                        int startComplaint = START_ITEM_NO;
                        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                        if (request.getParameter(pageNoAttr) != null) {
                            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                            if (pageNo < FIRST_PAGE_NO) {
                                pageNo = FIRST_PAGE_NO;
                            }
                            startComplaint = (pageNo - FIRST_PAGE_NO) * COMPLAINTS_PER_PAGE;
                        }
                        PaginatedList<Complaint> complaints = adminService.findComplaints(startComplaint, COMPLAINTS_PER_PAGE);
                        String complaintsAttr = configurationManager.getProperty(COMPLAINTS_ATTR);
                        String complaintsPath = configurationManager.getProperty(COMPLAINTS_PAGE);
                        request.setAttribute(complaintsAttr, complaints);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + complaintsPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case USER_ROLE:
                default:
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
            }
        }
        return page;
    }
}
