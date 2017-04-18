package test;

import by.alesnax.qanda.validation.CategoryValidation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by alesnax on 27.01.2017.
 *
 */
public class CategoryValidationTest {

    private static CategoryValidation categoryValidation;
    private List<String> errors;
    private String titleEn;
    private String titleRu;
    private String descriptionEn;
    private String descriptionRu;
    private String login;
    private String categoryStatus;


    @BeforeClass
    public static void initValidation() {
        categoryValidation = new CategoryValidation();
    }

    @AfterClass
    public static void destroyValidation() {
        categoryValidation = null;
    }

    @Test
    public void validateNewCategoryErrorsListShouldBeEmpty() {
        titleEn = "Sport";
        titleRu = "Спорт";
        descriptionEn = "All about sport";
        descriptionRu = "Все о спорте";
        errors = categoryValidation.validateNewCategory(titleEn, titleRu, descriptionEn, descriptionRu);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validateNewCategoryErrorsSizeListShouldBeEqual5CauseInvalidParams() {
        titleEn = "Q";
        titleRu = "description description description description description description description " +
                "description description description description description description description description " +
                "description description description description description description description ";
        descriptionEn = "Short";
        descriptionRu = "tooshort";
        errors = categoryValidation.validateNewCategory(titleEn, titleRu, descriptionEn, descriptionRu);
        assertTrue("Errors list should be equal because all parameters are invalid and added errors title", errors.size() == 5);
    }

    @Test
    public void validateNewCategoryErrorsSizeListShouldBeEqual5CauseEmptyParams() {
        titleEn = "";
        titleRu = "";
        descriptionEn = "";
        descriptionRu = "";
        errors = categoryValidation.validateNewCategory(titleEn, titleRu, descriptionEn, descriptionRu);
        assertTrue("Errors list should be equal because all parameters are empty and added errors title", errors.size() == 5);
    }

    @Test
    public void validateCorrectedCategoryErrorsListShouldBeEmpty() {
        titleEn = "Sport";
        titleRu = "Спорт";
        descriptionEn = "All about sport";
        descriptionRu = "Все о спорте";
        login = "Slimack";
        categoryStatus = "NEW";
        errors = categoryValidation.validateCorrectedCategory(titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validateCorrectedCategoryErrorsSizeListShouldBeEqual7CauseInvalidParams() {
        titleEn = "Q";
        titleRu = "description description description description description description description " +
                "description description description description description description description description " +
                "description description description description description description description ";
        descriptionEn = "Short";
        descriptionRu = "tooshort";
        login = "Slimack=-032";
        categoryStatus = "NEW123";
        errors = categoryValidation.validateCorrectedCategory(titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
        assertTrue("Errors list should be equal because all parameters are invalid and added errors title", errors.size() == 7);
    }

    @Test
    public void validateCorrectedCategoryErrorsSizeListShouldBeEqual7CauseEmptyParams() {
        titleEn = "";
        titleRu = "";
        descriptionEn = "";
        descriptionRu = "";
        login = "";
        categoryStatus = "";
        errors = categoryValidation.validateCorrectedCategory(titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
        assertTrue("Errors list should be equal because all parameters are empty and added errors title", errors.size() == 7);
    }


}
