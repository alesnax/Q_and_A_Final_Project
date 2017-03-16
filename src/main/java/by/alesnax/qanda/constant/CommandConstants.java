package by.alesnax.qanda.constant;

/**
 * Class contains constants that are frequently used in Command layer
 *
 * @author alesnax
 */

public class CommandConstants {

    /**
     * parameters used for defining type of calling method
     */
    public static final String TYPE_PAGE_DELIMITER = " ";
    public static final String RESPONSE_TYPE = "response";
    public static final String REQUEST_TYPE = "request";
    public static final String ERROR_REQUEST_TYPE = "error";

    /**
     * parameters used for pagination
     */
    public static final int POSTS_PER_PAGE = 5;
    public static final int CATEGORIES_PER_PAGE = 8;
    public static final int USERS_PER_PAGE = 6;
    public static final int BANS_PER_PAGE = 6;
    public static final int COMPLAINTS_PER_PAGE = 5;
    public static final int FIRST_PAGE_NO = 1;
    public static final int START_ITEM_NO = 0;
    public static final int DEFAULT_USER_ID = 0;

    /**
     * statuses of operation
     */
    public static final String OPERATION_FAILED = "operation_failed";
    public static final String OPERATION_PROCESSED = "operation_processed";
    public static final String USER_BANNED = "user_banned_for_operation";

}
