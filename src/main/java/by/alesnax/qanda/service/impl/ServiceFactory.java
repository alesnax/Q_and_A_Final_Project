package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.UserService;

/**
 * Singleton that returns instance for getting implementations of service classes
 *
 * @author Aliaksandr Nakhankou
 */
public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private UserService userService = new UserServiceImpl();
    private AdminService adminService = new AdminServiceImpl();
    private PostService postService = new PostServiceImpl();
    private ModeratorService moderatorService = new ModeratorServiceImpl();

    private ServiceFactory() {
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    public UserService getUserService() {
        return userService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public PostService getPostService() {
        return postService;
    }

    public ModeratorService getModeratorService() {
        return moderatorService;
    }
}