package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Command has method that uploads file to server and writes file to server storage and name of file
 * to database . After that redirects to edit_profile page if user authorised,
 * and to authorisation page otherwise.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */

public class UploadFileCommand implements Command {
    private static Logger logger = LogManager.getLogger(UploadFileCommand.class);

    /**
     * Name of user attribute from session
     */
    private static final String USER = "user";

    /**
     * Keys of error or success message attributes located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    /**
     * Key of error or success message located in loc.properties file
     */
    private static final String WRONG_COMMAND_PARAMETERS = "error.error_msg.parameter_not_found";
    private static final String WRONG_IMAGE_TYPE = "error.error_msg.wrong_image_type";
    private static final String EMPTY_FILE_FOUND = "error.error_msg.empty_file_found";
    private static final String TOO_LARGE_IMAGE_FOUND = "error.error_msg.large_image_found";
    private static final String SUCCESS_UPLOAD_AVATAR_MESSAGE = "edit_profile.message.changed_avatar_saved";

    /**
     * Key of returned go_to_edit_profile command located in config.properties file
     */
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";

    /**
     * delimiters and prefixes for creating correct file paths
     */
    private static final String DOT_DELIMITER = ".";
    private static final String UNDER_DELIMITER = "_";
    private static final String RELATIVE_PATH_PREFIX = "..";

    /**
     * attribute of temporary repository location
     */
    private static final String TEMP_DIR_ATTR = "javax.servlet.context.tempdir";

    /**
     * avatar pattern name
     */
    private static final String AVATAR_PATTERN_NAME = "img.common.avatar.pattern_name";

    /**
     * key of maximum file size in bytes located in config.properties file
     */
    private static final String MAX_FILE_SIZE = "attr.max_avatar_size_byte";

    /**
     * method uploads file to server and writes file to server storage and name of file
     * to database. Method checks if request contain multipart content, otherwise redirects to
     * edit_profile page with error message.
     * if error is caught while processing, user is redirected to error or
     * edit_profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of edit_profile page or error page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession();

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            session.setAttribute(wrongCommandMessageAttr, WRONG_COMMAND_PARAMETERS);
            return page;
        }

        User user = (User) session.getAttribute(USER);
        ServletContext servletContext = session.getServletContext();
        File repository = (File) servletContext.getAttribute(TEMP_DIR_ATTR);
        DiskFileItemFactory factory = newDiskFileItemFactory(servletContext, repository);

        ServletFileUpload upload = new ServletFileUpload(factory);
        int maxFileSize = Integer.parseInt(configurationManager.getProperty(MAX_FILE_SIZE));
        upload.setSizeMax(maxFileSize);

        try {
            List<FileItem> items = upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator();

            boolean commandDefined = false;
            String command = null;

            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    if (!commandDefined && name.equals("command")) {
                        String value = item.getString();
                        if (value.equals("upload_avatar")) {
                            command = value;
                            commandDefined = true;
                        }
                    }
                } else {
                    if (commandDefined && command.equals("upload_avatar")) {
                        String fileName = item.getName();
                        long sizeInBytes = item.getSize();
                        String contentType = item.getContentType();
                        if (sizeInBytes == 0) {
                            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, EMPTY_FILE_FOUND);
                            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                            return page;
                        }
                        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                            session.setAttribute(wrongCommandMessageAttr, WRONG_IMAGE_TYPE);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                            return page;
                        }

                        String avatarPath;
                        String shortFilePath;
                        String directoryPath = session.getServletContext().getRealPath("");
                        String avatarPatternName = configurationManager.getProperty(AVATAR_PATTERN_NAME);
                        // Write the file
                        File file;
                        if (fileName.lastIndexOf(DOT_DELIMITER) >= 0) {
                            shortFilePath = avatarPatternName + user.getId() + UNDER_DELIMITER + new Date().getTime() + fileName.substring(fileName.lastIndexOf(DOT_DELIMITER));
                            avatarPath = directoryPath + shortFilePath;
                            file = new File(avatarPath);
                        } else {
                            shortFilePath = avatarPatternName + user.getId() + fileName.substring(fileName.lastIndexOf(DOT_DELIMITER) + 1);
                            avatarPath = directoryPath + shortFilePath;
                            file = new File(avatarPath);
                        }
                        item.write(file);
                        shortFilePath = RELATIVE_PATH_PREFIX + shortFilePath;
                        UserService userService = ServiceFactory.getInstance().getUserService();
                        userService.uploadUserAvatar(user.getId(), shortFilePath);
                        user.setAvatar(shortFilePath);
                    }
                }
            }
            String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
            session.setAttribute(successChangeMessageAttr, SUCCESS_UPLOAD_AVATAR_MESSAGE);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;

        } catch (FileUploadBase.SizeLimitExceededException e) {
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, TOO_LARGE_IMAGE_FOUND);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        } catch (FileUploadException | ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        } catch (Exception e) {// exception thrown by write() method
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }

    private DiskFileItemFactory newDiskFileItemFactory(ServletContext context, File repository) {
        FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);
        DiskFileItemFactory factory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository);
        factory.setFileCleaningTracker(fileCleaningTracker);
        return factory;
    }
}
