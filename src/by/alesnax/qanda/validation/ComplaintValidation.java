package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 16.01.2017.
 */
public class ComplaintValidation {
    private static final String ERROR_COMPLAINT_DECISION_HEADER = "common.add_new_complaint.error_msg.error_decision_header";
    private static final String ERROR_COMPLAINT_HEADER = "common.add_new_complaint.error_msg.error_header";

    private static final String COMPLAINT_EMPTY = "common.add_new_complaint.error_msg.empty";
    private static final String COMPLAINT_TOO_SHORT = "common.add_new_complaint.error_msg.short";
    private static final String COMPLAINT_TOO_LONG = "common.add_new_complaint.error_msg.long";

    private static final String MIN_TITLE_LENGTH = "add_new_question.min_title_length";
    private static final String MAX_TITLE_LENGTH = "add_new_question.max_title_length";


    public List<String> validateComplaint(String content) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

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
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_COMPLAINT_HEADER);
            return errorMessages;
        }
    }

    public List<String> validateComplaintDecision(String decision) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

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
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_COMPLAINT_DECISION_HEADER);
            return errorMessages;
        }
    }
}
