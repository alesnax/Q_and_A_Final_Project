package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 13.12.2016.
 */
public class PostValidation {
    private static final String ERROR_HEADER = "common.add_new_question.error_msg.error_header";
    private static final String TITLE_EMPTY = "common.add_new_question.error_msg.title_empty";
    private static final String TITLE_TOO_SHORT = "common.add_new_question.error_msg.title_too_short";
    private static final String TITLE_TOO_LONG = "common.add_new_question.error_msg.title_too_long";
    private static final String CHOOSE_CATEGORY = "common.add_new_question.error_msg.choose_category";
    private static final String DESCRIPTION_EMPTY = "common.add_new_question.error_msg.description_empty";
    private static final String DESCRIPTION_TOO_SHORT = "common.add_new_question.error_msg.description_too_short";


    public List<String> validateQuestion(String title, String category, String description) {
        ArrayList<String> errorMessages = new ArrayList<>();

        boolean successful = true;

        if (title == null || title.isEmpty()) {
            successful = false;
            errorMessages.add(TITLE_EMPTY);
        } else if (title.length() < 10) {
            successful = false;
            errorMessages.add(TITLE_TOO_SHORT);
        } else if (title.length() > 128) {
            successful = false;
            errorMessages.add(TITLE_TOO_LONG);
        }

        if (category == null || category.isEmpty()) {
            successful = false;
            errorMessages.add(CHOOSE_CATEGORY);
        }

        if (description == null || description.isEmpty()) {
            successful = false;
            errorMessages.add(DESCRIPTION_EMPTY);
        } else if (description.length() < 10) {
            successful = false;
            errorMessages.add(DESCRIPTION_TOO_SHORT);
        }

        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_HEADER);
            return errorMessages;
        }
    }
}
