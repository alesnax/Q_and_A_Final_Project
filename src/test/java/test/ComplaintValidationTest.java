package test;

import by.alesnax.qanda.validation.ComplaintValidation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by alesnax on 27.01.2017.
 */
public class ComplaintValidationTest {

    private static ComplaintValidation complaintValidation;
    private List<String> errors;
    private String content;
    private String decision;


    @BeforeClass
    public static void initValidation() {
        complaintValidation = new ComplaintValidation();
    }

    @AfterClass
    public static void destroyValidation() {
        complaintValidation = null;
    }

    @Test
    public void validateComplaintErrorsListShouldBeEmpty() {
        content = "Some content";
        errors = complaintValidation.validateComplaint(content);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validateComplaintErrorsListSizeShouldEqual2CauseInvalidContent() {
        content = "So";
        errors = complaintValidation.validateComplaint(content);
        assertTrue("Errors list should be equal 2 because too short content and added errors title", errors.size() == 2);
    }

    @Test
    public void validateComplaintErrorsListSizeShouldEqual2CauseEmptyContent() {
        content = "";
        errors = complaintValidation.validateComplaint(content);
        assertTrue("Errors list should be equal 2 because empty content and added errors title", errors.size() == 2);
    }

    @Test
    public void validateComplaintDecisionErrorsListShouldBeEmpty() {
        decision = "Moderator decision";
        errors = complaintValidation.validateComplaintDecision(decision);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validateComplaintDecisionErrorsListSizeShouldEqual2CauseInvalidDecision() {
        decision = "banned";
        errors = complaintValidation.validateComplaintDecision(decision);
        assertTrue("Errors list should be equal 2 because too short decision and added errors title", errors.size() == 2);
    }

    @Test
    public void validateComplaintDecisionErrorsListSizeShouldEqual2CauseEmptyDecision() {
        decision = "";
        errors = complaintValidation.validateComplaintDecision(decision);
        assertTrue("Errors list should be equal 2 because empty decision and added errors title", errors.size() == 2);
    }

}
