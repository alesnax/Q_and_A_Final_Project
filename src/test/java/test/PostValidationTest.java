package test;

import by.alesnax.qanda.validation.PostValidation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by alesnax on 26.01.2017.
 */
public class PostValidationTest {

    private static PostValidation postValidation;
    private List<String> errors;
    private String title;
    private String category;
    private String content;
    private String questionId;
    private String categoryId;


    @BeforeClass
    public static void initValidation() {
        postValidation = new PostValidation();
    }

    @AfterClass
    public static void destroyValidation() {
        postValidation = null;
    }

    @Test
    public void validationQuestionErrorsListShouldBeEmpty() {
        title= "Question title";
        category = "category";
        content = "Some content";
        errors = postValidation.validateQuestion(title, category, content);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validationQuestionErrorsListSizeShouldBeEqual2CauseEmptyContent() {
        title= "Question title";
        category = "q_category";
        content = "";
        errors = postValidation.validateQuestion(title, category, content);
        assertTrue("Errors list should be equal 2 because of empty content and added errors title", errors.size() == 2);
    }

    @Test
    public void validationQuestionErrorsListSizeShouldBeEqual2CauseShortTitle() {
        title= "title";
        category = "q_category";
        content = "some description";
        errors = postValidation.validateQuestion(title, category, content);
        assertTrue("Errors list should be equal 2 because of short title and added errors title", errors.size() == 2);
    }

    @Test
    public void validationAnswerErrorsListSizeShouldBeEqual3CauseWrongParams() {
        questionId= "qwe";
        categoryId = "qwe";
        content = "some";
        errors = postValidation.validateAnswer(questionId, categoryId, content);
        assertTrue("Errors list should be equal 3 because all parameters are invalid and added errors title", errors.size() == 3);
    }

    @Test
    public void validationAnswerErrorsListShouldBeEmpty() {
        questionId= "12";
        categoryId = "1";
        content = "some content";
        errors = postValidation.validateAnswer(questionId, categoryId, content);
        assertTrue("Errors list should be empty because all params are valid", errors.isEmpty());
    }

    @Test
    public void validateCorrectedAnswerErrorsListShouldBeEmpty() {
        content = "some content";
        errors = postValidation.validateCorrectedAnswer(content);
        assertTrue("Errors list should be empty because all params are valid", errors.isEmpty());
    }

    @Test
    public void validateCorrectedAnswerErrorsListSizeShouldBeEqual2() {
        content = "some content";
        errors = postValidation.validateCorrectedAnswer(content);
        assertTrue("Errors list should be equal 2 because of invalid short content and added error title", errors.isEmpty());
    }
}
