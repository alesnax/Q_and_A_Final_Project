package by.alesnax.qanda.service;

import by.alesnax.qanda.service.impl.AdminServiceImpl;
import by.alesnax.qanda.service.impl.ModeratorServiceImpl;
import by.alesnax.qanda.service.impl.PostServiceImpl;
import by.alesnax.qanda.service.impl.UserServiceImpl;

public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private UserService userService = new UserServiceImpl();
    private AdminService adminService = new AdminServiceImpl();
    private PostService postService = new PostServiceImpl();
    private ModeratorService moderatorService= new ModeratorServiceImpl();

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

    public ModeratorService getModeratorService() { return moderatorService; }

}