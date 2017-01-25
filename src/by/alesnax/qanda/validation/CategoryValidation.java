package by.alesnax.qanda.validation;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alesnax on 16.01.2017.
 */
@SuppressWarnings("Duplicates")
public class CategoryValidation {
    private static final String TITLE_REGEX = "category_validation.title_regex";
    private static final String LOGIN_REGEX = "user_validation.login_regex";
    private static final String DESCRIPTION_REGEX = "category_validation.description_regex";

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
    private static final String CATEGORY_ERROR_HEADER = "category.error_msg.category_errors_header";


    public List<String> validateNewCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu) {
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    public List<String> validateCorrectedCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) {
        ConfigurationManager configurationManager = new ConfigurationManager();
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        String loginRegex = configurationManager.getProperty(LOGIN_REGEX);
        Pattern pLogin = Pattern.compile(loginRegex);
        Matcher mLogin = pLogin.matcher(login);
        if (login == null || login.isEmpty()) {
            errorMessages.add(LOGIN_EMPTY);
        } else if (!mLogin.matches()) {
            errorMessages.add(LOGIN_FALSE);
        }
        try {
            Category.CategoryStatus.valueOf(categoryStatus);
        } catch (IllegalArgumentException e){
            errorMessages.add(WRONG_CATEGORY_STATUS);
        }

        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    public List<String> validateCorrectedCategory(String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) {
        ArrayList<String> errorMessages = validateCommonInfo(titleEn, titleRu, descriptionEn, descriptionRu);

        try {
            Category.CategoryStatus.valueOf(categoryStatus);
        } catch (IllegalArgumentException e){
            errorMessages.add(WRONG_CATEGORY_STATUS);
        }

        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, CATEGORY_ERROR_HEADER);
        }
        return errorMessages;
    }

    private ArrayList<String> validateCommonInfo(String titleEn, String titleRu, String descriptionEn, String descriptionRu) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();

        String titleRegex = configurationManager.getProperty(TITLE_REGEX);
        Pattern pTitle = Pattern.compile(titleRegex);

        Matcher mTitleEn = pTitle.matcher(titleEn);

        if (titleEn == null || titleEn.isEmpty()) {
            errorMessages.add(TITLE_EN_EMPTY);
        } else if (!mTitleEn.matches()) {
            errorMessages.add(TITLE_EN_FALSE);
        }

        Matcher mTitleRu = pTitle.matcher(titleRu);

        if (titleRu == null || titleRu.isEmpty()) {
            errorMessages.add(TITLE_RU_EMPTY);
        } else if (!mTitleRu.matches()) {
            errorMessages.add(TITLE_RU_FALSE);
        }

        String descriptionRegex = configurationManager.getProperty(DESCRIPTION_REGEX);
        Pattern pDescription = Pattern.compile(descriptionRegex);

        Matcher mDescriptionEn = pDescription.matcher(descriptionEn);

        if (descriptionEn == null || descriptionEn.isEmpty()) {
            errorMessages.add(DESCRIPTION_EN_EMPTY);
        } else if (!mDescriptionEn.matches()) {
            errorMessages.add(DESCRIPTION_EN_FALSE);
        }

        Matcher mDescriptionRu = pDescription.matcher(descriptionRu);

        if (descriptionRu == null || descriptionRu.isEmpty()) {
            errorMessages.add(DESCRIPTION_RU_EMPTY);
        } else if (!mDescriptionRu.matches()) {
            errorMessages.add(DESCRIPTION_RU_FALSE);
        }
        return errorMessages;
    }
}
