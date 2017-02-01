package by.alesnax.qanda.validation;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class {@code CategoryValidation} contains methods for checking if values of class Category parameters correct
 *
 * @author Aliaksandr Nakhankou
 */

@SuppressWarnings("Duplicates")
public class CategoryValidation {

    /**
     * Keys of validation error messages that are located in loc.properties file
     */
    private static final String CATEGORY_ERROR_HEADER = "category.error_msg.category_errors_header";
    private static final String LOGIN_EMPTY = "user_registration.error_msg.login_empty";
    private static final String LOGIN_FALSE = "user_registration.error_msg.login_false";
    private static final String TITLE_EN_EMPTY = "category.error_msg.title_en_empty";
    private static final String TITLE_EN_FALSE = "category.error_msg.title_en_false";
    private static final String TITLE_RU_EMPTY = "category.error_msg.title_ru_empty";
    private static final String TITLE_RU_FALSE = "category.error_msg.title_ru_false";
    private static final String DESCRIPTION_EN_EMPTY = "category.error_msg.description_en_empty";
    private static final String DESCRIPTION_EN_FALSE = "category.error_msg.description_en_false";
    private static final String DESCRIPTION_RU_EMPTY = "category.error_msg.description_ru_empty";
    private static final String DESCRIPTION_RU_FALSE = "category.error_msg.description_ru_false";
    private static final String WRONG_CATEGORY_STATUS = "category.error_msg.wrong_category_status";

    /**
     * Keys of regex used while validation located in config.properties file
     */
    private static final String TITLE_REGEX = "category_validation.title_regex";
    private static final String LOGIN_REGEX = "user_validation.login_regex";
    private static final String DESCRIPTION_REGEX = "category_validation.description_regex";


    public List<String> validateNewCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu) {
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    /**
     * method validates values of category parameters while category correction and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     * method calls by users with ADMIN role
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateCorrectedCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) {
        ConfigurationManager configurationManager = new ConfigurationManager();

        // calling common category info validation method
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        //  moderator login validation
        String loginRegex = configurationManager.getProperty(LOGIN_REGEX);
        Pattern pLogin = Pattern.compile(loginRegex);
        Matcher mLogin = pLogin.matcher(login);
        if (login == null || login.isEmpty()) {
            errorMessages.add(LOGIN_EMPTY);
        } else if (!mLogin.matches()) {
            errorMessages.add(LOGIN_FALSE);
        }

        // category status validation
        try {
            Category.CategoryStatus.valueOf(categoryStatus);
        } catch (IllegalArgumentException e) {
            errorMessages.add(WRONG_CATEGORY_STATUS);
        }

        // adding error title if list has already contained errors
        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    /**
     * method validates values of category parameters while category correction and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *  method calls by users with MODERATOR role
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateCorrectedCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) {

        // calling common category info validation method
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        // category status validation
        try {
            Category.CategoryStatus.valueOf(categoryStatus);
        } catch (IllegalArgumentException e) {
            errorMessages.add(WRONG_CATEGORY_STATUS);
        }

        // adding error title if list has already contained errors
        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    /**
     * method validates values of common for other methods category parameters and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *  method calls by users with MODERATOR role
     *
     * @return list of validation errors or empty list
     */
    private ArrayList<String> validateCommonInfo(String titleEn, String titleRu, String descriptionEn, String descriptionRu) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();

        // validation of english category title
        String titleRegex = configurationManager.getProperty(TITLE_REGEX);
        Pattern pTitle = Pattern.compile(titleRegex);
        Matcher mTitleEn = pTitle.matcher(titleEn);

        if (titleEn == null || titleEn.isEmpty()) {
            errorMessages.add(TITLE_EN_EMPTY);
        } else if (!mTitleEn.matches()) {
            errorMessages.add(TITLE_EN_FALSE);
        }

        // validation of russian category title
        Matcher mTitleRu = pTitle.matcher(titleRu);

        if (titleRu == null || titleRu.isEmpty()) {
            errorMessages.add(TITLE_RU_EMPTY);
        } else if (!mTitleRu.matches()) {
            errorMessages.add(TITLE_RU_FALSE);
        }

        // validation of english category description
        String descriptionRegex = configurationManager.getProperty(DESCRIPTION_REGEX);
        Pattern pDescription = Pattern.compile(descriptionRegex);
        Matcher mDescriptionEn = pDescription.matcher(descriptionEn);

        if (descriptionEn == null || descriptionEn.isEmpty()) {
            errorMessages.add(DESCRIPTION_EN_EMPTY);
        } else if (!mDescriptionEn.matches()) {
            errorMessages.add(DESCRIPTION_EN_FALSE);
        }

        // validation of russian category description
        Matcher mDescriptionRu = pDescription.matcher(descriptionRu);

        if (descriptionRu == null || descriptionRu.isEmpty()) {
            errorMessages.add(DESCRIPTION_RU_EMPTY);
        } else if (!mDescriptionRu.matches()) {
            errorMessages.add(DESCRIPTION_RU_FALSE);
        }
        return errorMessages;
    }
}
