package test;

import by.alesnax.qanda.validation.UserValidation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by alesnax on 26.01.2017.
 *
 */
public class UserValidationTest {

    private static UserValidation userValidation;
    private List<String> errors;
    private String login;
    private String password;
    private String passwordCopy;
    private String oldPassword;
    private String name;
    private String surname;
    private String email;
    private String bDay;
    private String bMonth;
    private String bYear;
    private String sex;
    private String country;
    private String city;
    private String keyWordType;
    private String keyWord;

    @BeforeClass
    public static void initValidation() {
        userValidation = new UserValidation();
    }

    @AfterClass
    public static void destroyValidation() {
        userValidation = null;
    }

    @Test
    public void validationNewUserErrorListSizeShouldContain14Elements() {
        login = "Slimackывпр";
        password = "123";
        passwordCopy = "123";
        name = "123";
        surname = "123";
        email = "alesnax@gmail";
        bDay = "asd";
        bMonth = "asd";
        bYear = "asd";
        sex = "asd";
        country = "123";
        city = "123";
        keyWordType = "123";
        keyWord = "123";
        errors = userValidation.validateNewUser(login, password, passwordCopy, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);
        assertTrue("Errors list size should be equal to 14 because all parameters are incorrect", errors.size() == 14);
    }


    @Test
    public void validationNewUserErrorsListNotEmpty() {
        login = "";
        password = "123";
        passwordCopy = "123";
        name = "123";
        surname = "123";
        email = "@gmail";
        bDay = "1";
        bMonth = "2";
        bYear = "1995";
        sex = "asd";
        country = "Bel";
        city = "";
        keyWordType = "code_word";
        keyWord = "lock";
        errors = userValidation.validateNewUser(login, password, passwordCopy, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);
        assertTrue("Errors list  should not be empty because of wrong parameters", !errors.isEmpty());
    }

    @Test
    public void validationNewUserErrorsListEmpty() {
        login = "slimack";
        password = "1212011Xx";
        passwordCopy = "1212011Xx";
        name = "Mister";
        surname = "Smith";
        email = "googlo@gmail.com";
        bDay = "1";
        bMonth = "2";
        bYear = "1995";
        sex = "1";
        country = "Belarus";
        city = "";
        keyWordType = "1";
        keyWord = "locking scarf";
        errors = userValidation.validateNewUser(login, password, passwordCopy, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);
        assertTrue("Errors list  should be empty because all parameters are true", errors.isEmpty());
    }

    @Test
    public void validationUserInfoErrorsListEmpty() {
        email = "googlo@gmail.com";
        password = "1212011Xx";
        errors = userValidation.validateUserInfo(email, password);
        assertTrue("Errors list  should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validationUserInfoErrorsListNotEmpty() {
        email = "googlo@gmail.com";
        password = "12120";
        errors = userValidation.validateUserInfo(email, password);
        assertTrue("Errors list  should not be empty because of invalid password", !errors.isEmpty());
    }

    @Test
    public void validationUserInfoErrorsListSizeEquals2() {
        email = "";
        password = "1212011Aa";
        errors = userValidation.validateUserInfo(email, password);
        assertTrue("Errors list size should be equal to 2 because of empty email and error title", errors.size() == 2);
    }

    @Test
    public void validationUserMainInfoErrorsListEmpty() {
        login = "slimack";
        name = "Mister";
        surname = "Smith";
        email = "googlo@gmail.com";
        bDay = "1";
        bMonth = "2";
        bYear = "1995";
        sex = "1";
        country = "Belarus";
        city = "";
        keyWordType = "1";
        keyWord = "locking scarf";
        errors = userValidation.validateUserMainData(login, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);
        assertTrue("Errors list  should be empty because of invalid parameters", errors.isEmpty());
    }

    @Test
    public void validationUserMainInfoErrorsListSizeShouldBeEqualTo13() {
        login = "sl";
        name = "123";
        surname = "123";
        email = "@gmail.com";
        bDay = "1a";
        bMonth = "2a";
        bYear = "995";
        sex = "11";
        country = "Be2larus";
        city = "123";
        keyWordType = "11";
        keyWord = "lock";
        errors = userValidation.validateUserMainData(login, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);
        assertTrue("Errors list size should be equal to 13 because all parameters are wrong and added error title", errors.size() == 13);
    }

    @Test
    public void validationNewPasswordErrorsListShouldBeEmpty() {
        oldPassword= "1212011Qqqq";
        password = "1212011Aa";
        passwordCopy = "1212011Aa";
        errors = userValidation.validateNewPassword(oldPassword, password, passwordCopy);
        assertTrue("Errors list  should  be empty because of valid parameters", errors.isEmpty());
    }

    @Test
    public void validationNewPasswordErrorsListSizeShouldBeEqual2() {
        oldPassword= "12120";
        password = "1212011Aa";
        passwordCopy = "1212011Aa";
        errors = userValidation.validateNewPassword(oldPassword, password, passwordCopy);
        assertTrue("Errors list size should be equal 2 because of invalid oldPassword", errors.size() == 2);
    }

    @Test
    public void validationNewPasswordErrorsListSizeShouldBeEqual2CauseDiffPass() {
        oldPassword = "1212011QQQQQq";
        password = "1212011Aa";
        passwordCopy = "1212011Zzzzz";
        errors = userValidation.validateNewPassword(oldPassword, password, passwordCopy);
        assertTrue("Errors list size should be equal 2 because password and its copy don't match", errors.size() == 2);
    }

    @Test
    public void validationPasswordRecoveringDataErrorsListSizeShouldBeEmpty() {
        email= "alexthegreat@gmail.com";
        keyWordType = "2";
        keyWord = "locking word";
        errors = userValidation.validatePasswordRecovData(email, keyWordType, keyWord);
        assertTrue("Errors list should be empty because all parameters are valid", errors.isEmpty());
    }

    @Test
    public void validationPasswordRecoveringDataErrorsListSizeShouldBeEqual2CauseInvalidEmail() {
        email= "alexthegreat@gmailcom";
        keyWordType = "2";
        keyWord = "locking word";
        errors = userValidation.validatePasswordRecovData(email, keyWordType, keyWord);
        assertTrue("Errors list size should be equal 2 because of onvalid email and added errors title", errors.size() == 2);
    }

    @Test
    public void validationPasswordRecoveringDataErrorsListSizeShouldBeEqual2CauseEmptyKeyWord() {
        email= "alexthegreat@gmail.com";
        keyWordType = "2";
        keyWord = "";
        errors = userValidation.validatePasswordRecovData(email, keyWordType, keyWord);
        assertTrue("Errors list size should be equal 2 because of empty keyword and errors title", errors.size() == 2);
    }



}
