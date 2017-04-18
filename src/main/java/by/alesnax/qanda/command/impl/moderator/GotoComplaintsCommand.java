package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static imports

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
    private static final String MODERATOR_ROLE = "moderator";

    /**
     * Keys of error messages attributes and page_no attribute that are located in config.properties file
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String COMPLAINTS_ATTR = "attr.request.complaints";

    /**
     * Keys of returned commands or page that are located in config.properties file
     */
    private static final String COMPLAINTS_PAGE = "path.page.complaints";

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

        String role = user.getRole().getValue();
        int userId = user.getId();

        PaginatedList<Complaint> complaints;
        try {
            int startComplaint = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startComplaint = (pageNo - FIRST_PAGE_NO) * COMPLAINTS_PER_PAGE;
            }
            if (MODERATOR_ROLE.equals(role)) {
                ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                complaints = moderatorService.findComplaintsByModeratorId(userId, startComplaint, COMPLAINTS_PER_PAGE);
            } else {
                AdminService adminService = ServiceFactory.getInstance().getAdminService();
                complaints = adminService.findComplaints(startComplaint, COMPLAINTS_PER_PAGE);
            }

            String complaintsAttr = configurationManager.getProperty(COMPLAINTS_ATTR);
            request.setAttribute(complaintsAttr, complaints);
            String complaintsPath = configurationManager.getProperty(COMPLAINTS_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + complaintsPath;
        } catch (ServiceException | NumberFormatException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
