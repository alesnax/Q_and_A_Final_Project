package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code ComplaintValidation} contains methods for checking if values of class Complaint parameters correct
 *
 * @author Aliaksandr Nakhankou
 */
public class ComplaintValidation {

    /**
     * Keys of validation error messages that are located in loc.properties file
     */
    private static final String ERROR_COMPLAINT_DECISION_HEADER = "common.add_new_complaint.error_msg.error_decision_header";
    private static final String ERROR_COMPLAINT_HEADER = "common.add_new_complaint.error_msg.error_header";
    private static final String COMPLAINT_EMPTY = "common.add_new_complaint.error_msg.empty";
    private static final String COMPLAINT_TOO_SHORT = "common.add_new_complaint.error_msg.short";
    private static final String COMPLAINT_TOO_LONG = "common.add_new_complaint.error_msg.long";

    /**
     * Keys of validation parameters that are located in config.properties file
     */
    private static final String MIN_TITLE_LENGTH = "add_new_question.min_title_length";
    private static final String MAX_TITLE_LENGTH = "add_new_question.max_title_length";

    /**
     * method validates values of complaint parameters while adding new complaint and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateComplaint(String content) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // complaint content validation
        int minContentLength = Integer.parseInt(configurationManager.getProperty(MIN_TITLE_LENGTH));
        int maxContentLength = Integer.parseInt(configurationManager.getProperty(MAX_TITLE_LENGTH));
        if (content == null || content.isEmpty()) {
            errorMessages.add(COMPLAINT_EMPTY);
            successful = false;
        } else if (content.length() > maxContentLength) {
            successful = false;
            errorMessages.add(COMPLAINT_TOO_LONG);
        } else if (content.length() < minContentLength) {
            errorMessages.add(COMPLAINT_TOO_SHORT);
            successful = false;
        }
        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_COMPLAINT_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of complaint parameters while adding complaint decision and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateComplaintDecision(String decision) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // complaint content validation
        int maxContentLength = Integer.parseInt(configurationManager.getProperty(MAX_TITLE_LENGTH));
        int minContentLength = Integer.parseInt(configurationManager.getProperty(MIN_TITLE_LENGTH));
        if (decision == null || decision.isEmpty()) {
            errorMessages.add(COMPLAINT_EMPTY);
            successful = false;
        } else if (decision.length() > maxContentLength) {
            errorMessages.add(COMPLAINT_TOO_LONG);
            successful = false;
        } else if (decision.length() < minContentLength) {
            successful = false;
            errorMessages.add(COMPLAINT_TOO_SHORT);
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_COMPLAINT_DECISION_HEADER);
            return errorMessages;
        }
    }
}
