package by.alesnax.qanda.command.client;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.ChangeLanguageCommand;
import by.alesnax.qanda.command.impl.guest.*;
import by.alesnax.qanda.command.impl.user.*;

import java.util.HashMap;
import java.util.Map;

public final class CommandHelper {
    private static final String GUEST_ROLE = "guest";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";



    private Map<CommandName, Command> guestCommands = new HashMap<>();
    private Map<CommandName, Command> userCommands = new HashMap<>();
    private Map<CommandName, Command> moderatorCommands = new HashMap<>();
    private Map<CommandName, Command> adminCommands = new HashMap<>();


    private static final CommandHelper INSTANCE = new CommandHelper();


    private CommandHelper() {
        guestCommands.put(CommandName.ADD_QUESTION, new AddQuestionCommand());
        guestCommands.put(CommandName.CHANGE_LANGUAGE, new ChangeLanguageCommand());
        guestCommands.put(CommandName.CLEAN_QUESTION_FORM, new CleanQuestionFormCommand());
        guestCommands.put(CommandName.GO_TO_AUTHORIZATION_PAGE, new GotoAuthorizationPageCommand());
        guestCommands.put(CommandName.GO_TO_CATEGORY, new GotoCategoryCommand());
        guestCommands.put(CommandName.GO_TO_FIRST_PAGE, new GotoFirstPageCommand());
        guestCommands.put(CommandName.GO_TO_MAIN_PAGE, new GotoMainPageCommand());
        guestCommands.put(CommandName.GO_TO_PASSWORD_RECOVERY, new GotoPasswordRecoveryCommand());
        guestCommands.put(CommandName.GO_TO_POST_COMPLAINT, new GotoPostComplaintCommand());
        guestCommands.put(CommandName.GO_TO_PROFILE, new GotoProfileCommand());
        guestCommands.put(CommandName.GO_TO_QUEST_CATEGORIES, new GotoQuestCategoriesCommand());
        guestCommands.put(CommandName.GO_TO_REGISTRATION_PAGE, new GotoRegistrationPageCommand());
        guestCommands.put(CommandName.FIND_BEST_ANSWERS, new FindBestAnswersCommand());
        guestCommands.put(CommandName.FIND_BEST_QUESTIONS, new FindBestQuestionsCommand());
        guestCommands.put(CommandName.FIND_BEST_USERS, new FindBestUsersCommand());
        guestCommands.put(CommandName.SEARCH_ANSWER_QUESTION, new SearchAnswerQuestCommand());
        guestCommands.put(CommandName.REGISTER_NEW_USER, new RegisterNewUserCommand());
        guestCommands.put(CommandName.USER_AUTHORIZATION, new UserAuthorizationCommand());
        guestCommands.put(CommandName.GO_TO_QUESTION, new GotoQuestionCommand());


        userCommands.putAll(guestCommands);
        userCommands.put(CommandName.CHANGE_USER_LANGUAGE, new ChangeUserLanguageCommand());
        userCommands.put(CommandName.GO_TO_FRIENDS, new GotoFriendsCommand());
        userCommands.put(CommandName.GO_TO_MY_NEWS, new GotoNewsCommand());
        userCommands.put(CommandName.GO_TO_POST_CORRECTION, new GotoPostCorrectionCommand());
        userCommands.put(CommandName.GO_TO_EDIT_PROFILE, new GotoEditProfileCommand());
        userCommands.put(CommandName.GO_TO_REPOSTS, new GotoRepostsCommand());
        userCommands.put(CommandName.LOG_OUT, new LogOutCommand());
        userCommands.put(CommandName.RATE_POST, new RatePostCommand());
        userCommands.put(CommandName.REMOVE_FOLLOWING_USER, new RemoveFollowingUserCommand());
        userCommands.put(CommandName.FOLLOW_USER, new FollowUserCommand());
        userCommands.put(CommandName.DELETE_POST, new DeletePostCommand());
        userCommands.put(CommandName.CHANGE_USER_INFO, new ChangeUserInfoCommand());
        userCommands.put(CommandName.CHANGE_PASSWORD, new ChangePasswordCommand());
        userCommands.put(CommandName.UPLOAD_FILE, new UploadFileCommand());


        moderatorCommands.putAll(userCommands);


        adminCommands.putAll(moderatorCommands);

    }

    public Command getCommand(String role, String name) {
        name = name.replace('-', '_').toUpperCase();
        CommandName commandName;
        Command command = null;

            commandName = CommandName.valueOf(name);
            switch (role) {
                case GUEST_ROLE:
                    command = guestCommands.get(commandName);
                    break;
                case USER_ROLE:
                    command = userCommands.get(commandName);
                    break;
                case MODERATOR_ROLE:
                    command = moderatorCommands.get(commandName);
                    break;
                case ADMIN_ROLE:
                    command = adminCommands.get(commandName);
                    break;
                default:
                    command = guestCommands.get(commandName);
                    break;
            }
        return command;
    }

    public static CommandHelper getInstance() {
        return INSTANCE;
    }
}
