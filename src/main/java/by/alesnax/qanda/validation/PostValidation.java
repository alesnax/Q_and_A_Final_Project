package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code PostValidation} contains methods for checking if values of  class Post parameters correct
 *
 * @author Aliaksandr Nakhankou
 */
@SuppressWarnings("Duplicates")
public class PostValidation {

    /**
     * Keys of validation error messages that are located in loc.properties file
     */
    private static final String ERROR_HEADER = "common.add_new_question.error_msg.error_header";
    private static final String ERROR_ANSWER_HEADER = "common.add_new_answer.error_msg.error_header";
    private static final String TITLE_EMPTY = "common.add_new_question.error_msg.title_empty";
    private static final String TITLE_TOO_SHORT = "common.add_new_question.error_msg.title_too_short";
    private static final String TITLE_TOO_LONG = "common.add_new_question.error_msg.title_too_long";
    private static final String CHOOSE_CATEGORY = "common.add_new_question.error_msg.choose_category";
    private static final String DESCRIPTION_EMPTY = "common.add_new_question.error_msg.description_empty";
    private static final String ANSWER_EMPTY = "common.add_new_answer.error_msg.description_empty";
    private static final String DESCRIPTION_TOO_SHORT = "common.add_new_question.error_msg.description_too_short";
    private static final String DESCRIPTION_TOO_LONG = "common.add_new_question.error_msg.description_too_long";
    private static final String WRONG_EXPECTED_PARAMETER = "common.add_new_answer.error_msg.wrong_parameter";

    /**
     * Keys of validation parameters that are located in config.properties file
     */
    private static final String MIN_TITLE_LENGTH = "add_new_question.min_title_length";
    private static final String MAX_TITLE_LENGTH = "add_new_question.max_title_length";
    private static final String MIN_CONTENT_LENGTH = "add_new_question.min_content_length";
    private static final String MAX_CONTENT_LENGTH = "add_new_question.max_content_length";

    /**
     * method validates values of question parameters while adding new question and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateQuestion(String title, String category, String content) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // question title validation
        int minTitleLength = Integer.parseInt(configurationManager.getProperty(MIN_TITLE_LENGTH));
        int maxTitleLength = Integer.parseInt(configurationManager.getProperty(MAX_TITLE_LENGTH));
        if (title == null || title.isEmpty()) {
            successful = false;
            errorMessages.add(TITLE_EMPTY);
        } else if (title.length() < minTitleLength) {
            successful = false;
            errorMessages.add(TITLE_TOO_SHORT);
        } else if (title.length() > maxTitleLength) {
            successful = false;
            errorMessages.add(TITLE_TOO_LONG);
        }

        // question category validation
        if (category == null || category.isEmpty()) {
            successful = false;
            errorMessages.add(CHOOSE_CATEGORY);
        }

        // question content validation
        int minContentLength = Integer.parseInt(configurationManager.getProperty(MIN_CONTENT_LENGTH));
        int maxContentLength = Integer.parseInt(configurationManager.getProperty(MAX_CONTENT_LENGTH));
        if (content == null || content.isEmpty()) {
            successful = false;
            errorMessages.add(DESCRIPTION_EMPTY);
        } else if (content.length() > maxContentLength) {
            successful = false;
            errorMessages.add(DESCRIPTION_TOO_LONG);
        } else if (content.length() < minContentLength) {
            successful = false;
            errorMessages.add(DESCRIPTION_TOO_SHORT);
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of answer parameters while adding new answer and adds validation error
     * messages into returned list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateAnswer(String questionId, String categoryId, String content) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // category id validation
        try {
            int questId = Integer.parseInt(questionId);
            int catId = Integer.parseInt(categoryId);
            if (questId <= 0 || catId <= 0) {
                errorMessages.add(WRONG_EXPECTED_PARAMETER);
                successful = false;
            }
        } catch (NumberFormatException e) {
            errorMessages.add(WRONG_EXPECTED_PARAMETER);
            successful = false;
        }

        // answer content validation
        int minContentLength = Integer.parseInt(configurationManager.getProperty(MIN_CONTENT_LENGTH));
        int maxContentLength = Integer.parseInt(configurationManager.getProperty(MAX_CONTENT_LENGTH));
        if (content == null || content.isEmpty()) {
            errorMessages.add(ANSWER_EMPTY);
            successful = false;
        } else if (content.length() > maxContentLength) {
            errorMessages.add(DESCRIPTION_TOO_LONG);
            successful = false;
        } else if (content.length() < minContentLength) {
            errorMessages.add(DESCRIPTION_TOO_SHORT);
            successful = false;
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_ANSWER_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of corrected answer parameters while answer correcting and adds validation error
     * messages into returned list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateCorrectedAnswer(String content) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // answer content validation
        int minContentLength = Integer.parseInt(configurationManager.getProperty(MIN_CONTENT_LENGTH));
        int maxContentLength = Integer.parseInt(configurationManager.getProperty(MAX_CONTENT_LENGTH));
        if (content == null || content.isEmpty()) {
            errorMessages.add(ANSWER_EMPTY);
            successful = false;
        } else if (content.length() > maxContentLength) {
            errorMessages.add(DESCRIPTION_TOO_LONG);
            successful = false;
        } else if (content.length() < minContentLength) {
            errorMessages.add(DESCRIPTION_TOO_SHORT);
            successful = false;
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_ANSWER_HEADER);
            return errorMessages;
        }
    }
}