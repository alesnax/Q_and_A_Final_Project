package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceException;
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
import java.util.Iterator;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 07.01.2017.
 */
@SuppressWarnings("Duplicates")
public class UploadFileCommand implements Command {
    private static Logger logger = LogManager.getLogger(UploadFileCommand.class);


    private static final String USER = "user";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String WRONG_COMMAND_PARAMETERS = "error.error_msg.parameter_not_found";
    private static final String WRONG_IMAGE_TYPE = "error.error_msg.wrong_image_type";
    private static final String EMPTY_IMAGE_FOUND = "error.error_msg.empty_image_found";
    private static final String EMPTY_FILE_FOUND = "error.error_msg.empty_file_found";
    private static final String TOO_LARGE_IMAGE_FOUND = "error.error_msg.large_image_found";
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";
    private static final String SUCCESS_UPLOAD_AVATAR_MESSAGE = "edit_profile.message.changed_avatar_saved";

    private static final String DOT_DELIMITER = ".";
    private static final String RELATIVE_PATH_PREFIX = "..";

    private static final String TEMP_DIR_ATTR = "javax.servlet.context.tempdir";
    private static final String AVATAR_PATTERN_NAME = "img.common.avatar.pattern_name";
    private static final String DIRECTORY_PATH = "path.avatar.upload_directory";
    private static final int MAX_FILE_SIZE = 500 * 1024;// change to path


    private boolean isMultipart;

    private File file;


    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        HttpSession session = request.getSession();

        isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, WRONG_COMMAND_PARAMETERS);
            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        }

        User user = (User) session.getAttribute(USER);
        ServletContext servletContext = session.getServletContext();
        File repository = (File) servletContext.getAttribute(TEMP_DIR_ATTR);
        DiskFileItemFactory factory = newDiskFileItemFactory(servletContext, repository);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(MAX_FILE_SIZE);

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
                        String fieldName = item.getFieldName();
                        String fileName = item.getName();
                        long sizeInBytes = item.getSize();
                        String contentType = item.getContentType();
                        if (sizeInBytes == 0) {
                            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, EMPTY_FILE_FOUND);
                            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                            return page;
                        }
                        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                            session.setAttribute(wrongCommandMessageAttr, WRONG_IMAGE_TYPE);
                            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
                            return page;
                        }
                        boolean isInMemory = item.isInMemory();


                        String avatarPath = "";
                        String shortFilePath = "";
                        String directoryPath = ConfigurationManager.getProperty(DIRECTORY_PATH);
                        String avatarPatternName = ConfigurationManager.getProperty(AVATAR_PATTERN_NAME);
                        // Write the file
                        if (fileName.lastIndexOf(DOT_DELIMITER) >= 0) {
                            shortFilePath = avatarPatternName + user.getId() + fileName.substring(fileName.lastIndexOf(DOT_DELIMITER));
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
            String successChangeMessageAttr = ConfigurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
            session.setAttribute(successChangeMessageAttr, SUCCESS_UPLOAD_AVATAR_MESSAGE);
            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;

        } catch (FileUploadBase.SizeLimitExceededException e) {
            String wrongCommandMessageAttr = ConfigurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, TOO_LARGE_IMAGE_FOUND);
            String gotoEditProfileCommand = ConfigurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        } catch (FileUploadException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        } catch (ServiceException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
            page = ERROR_REQUEST_TYPE;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e);
            String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
            request.setAttribute(errorMessageAttr, e.getMessage());
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
