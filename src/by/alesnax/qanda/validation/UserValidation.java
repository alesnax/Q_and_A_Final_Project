package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class {@code UserValidation} contains methods for checking if values of class User parameters correct
 *
 * @author Aliaksandr Nakhankou
 */
@SuppressWarnings("Duplicates")
public class UserValidation {

    /**
     * Keys of validation error messages that are located in loc.properties file
     */
    private static final String ERROR_HEADER = "user_registration.error_msg.error_header";
    private static final String ERROR_VALID_HEADER = "user_authorization.error_msg.error_header";
    private static final String LOGIN_EMPTY = "user_registration.error_msg.login_empty";
    private static final String LOGIN_FALSE = "user_registration.error_msg.login_false";
    private static final String PASSWORD_EMPTY = "user_registration.error_msg.password_empty";
    private static final String PASSWORDS_NOT_EQUAL = "user_registration.error_msg.passwords_not_equal";
    private static final String PASSWORD_FALSE = "user_registration.error_msg.password_false";
    private static final String NAME_EMPTY = "user_registration.error_msg.name_empty";
    private static final String NAME_FALSE = "user_registration.error_msg.name_false";
    private static final String SURNAME_EMPTY = "user_registration.error_msg.surname_empty";
    private static final String SURNAME_FALSE = "user_registration.error_msg.surname_false";
    private static final String EMAIL_EMPTY = "user_registration.error_msg.email_empty";
    private static final String EMAIL_FALSE = "user_registration.error_msg.email_false";
    private static final String DAY_OUT_LIMIT = "user_registration.error_msg.day_out_limit";
    private static final String DAY_EMPTY = "user_registration.error_msg.day_empty";
    private static final String MONTH_OUT_LIMIT = "user_registration.error_msg.month_out_limit";
    private static final String MONTH_EMPTY = "user_registration.error_msg.month_empty";
    private static final String YEAR_OUT_LIMIT = "user_registration.error_msg.year_out_limit";
    private static final String YEAR_EMPTY = "user_registration.error_msg.year_empty";
    private static final String SEX_EMPTY = "user_registration.error_msg.sex_empty";
    private static final String SEX_WRONG_TYPE = "user_registration.error_msg.sex_wrong_type";
    private static final String COUNTRY_FALSE = "user_registration.error_msg.country_false";
    private static final String CITY_FALSE = "user_registration.error_msg.city_false";
    private static final String DATE_NOT_NUMBER = "user_registration.error_msg.date_not_number";
    private static final String KEY_WORD_TYPE_EMPTY = "user_registration.error_msg.key_word_type_unchosen";
    private static final String KEY_WORD_TYPE_WRONG = "user_registration.error_msg.key_word_type_wrong";
    private static final String KEY_WORD_EMPTY = "user_registration.error_msg.key_word_empty";
    private static final String KEY_WORD_WRONG = "user_registration.error_msg.key_word_wrong";
    private static final String EMAIL_IS_EMPTY = "user_authorization.error_msg.email_empty";
    private static final String EMAIL_IS_FALSE = "user_authorization.error_msg.email_false";
    private static final String PASSWORD_IS_EMPTY = "user_authorization.error_msg.password_empty";
    private static final String PASSWORD_IS_FALSE = "user_authorization.error_msg.password_false";

    /**
     * Keys of validation parameters that are located in config.properties file
     */
    private static final String MALE = "user_registration_page.form_value.sex.male";
    private static final String FEMALE = "user_registration_page.form_value.sex.female";
    private static final String SEX_UNCHOSEN = "user_registration_page.form_value.sex.unchosen";
    private static final String KEY_WORD_TYPE_UNCHOSEN = "user_registration_page.form_value.key_word.unchosen";
    private static final String KEY_WORD_TYPE_MAIDEN_NAME = "user_registration_page.form_value.key_word.mothers_maiden_name";
    private static final String KEY_WORD_TYPE_PET_NICK = "user_registration_page.form_value.key_word.first_pet_nickname";
    private static final String KEY_WORD_TYPE_PASS_NO = "user_registration_page.form_value.key_word.passport_number";
    private static final String KEY_WORD_TYPE_CODEWORD = "user_registration_page.form_value.key_word.codeword";
    private static final String YEAR_LOW_LIMIT = "user_registration_page.year_low_limit";
    private static final String YEAR_TOP_LIMIT = "user_registration_page.year_top_limit";
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;

    /**
     * Keys of regex used while validation located in config.properties file
     */
    private static final String GEO_REGEX = "user_validation.geo_regex";
    private static final String LOGIN_REGEX = "user_validation.login_regex";
    private static final String PASSWORD_REGEX = "user_validation.password_regex";
    private static final String NAME_REGEX = "user_validation.name_regex";
    private static final String EMAIL_REGEX = "user_validation.email_regex";
    private static final String KEY_WORD_REGEX = "user_validation.key_word_regex";

    /**
     * method validates values of user info parameters while user registration and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public ArrayList<String> validateNewUser(String login, String password, String passwordCopy, String name, String surname,
                                             String email, String bDay, String bMonth, String bYear, String sex, String country,
                                             String city, String keyWordType, String keyWord) {
        boolean successful = true;
        ConfigurationManager configurationManager = new ConfigurationManager();
        ArrayList<String> errorMessages = checkCommonUserParameters(login, name, surname, email, bDay, bMonth, bYear, sex, country, city,
                keyWordType, keyWord);
        if (!errorMessages.isEmpty()) {
            successful = false;
        }

        // passwords validation
        String passwordRegex = configurationManager.getProperty(PASSWORD_REGEX);
        Pattern pPassword = Pattern.compile(passwordRegex);
        Matcher mPassword = pPassword.matcher(password);

        if (password == null || password.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_EMPTY);
        } else if (passwordCopy == null || passwordCopy.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_EMPTY);
        } else if (!password.equals(passwordCopy)) {
            successful = false;
            errorMessages.add(PASSWORDS_NOT_EQUAL);
        } else if (!mPassword.matches()) {
            successful = false;
            errorMessages.add(PASSWORD_FALSE);
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
     * method validates values of user info parameters while user authorisation and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateUserInfo(String email, String password) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // validation of email
        String emailRegex = configurationManager.getProperty(EMAIL_REGEX);
        Pattern pEmail = Pattern.compile(emailRegex);
        Matcher mEmail = pEmail.matcher(email);

        if (email == null || email.isEmpty()) {
            successful = false;
            errorMessages.add(EMAIL_IS_EMPTY);
        } else if (!mEmail.matches()) {
            successful = false;
            errorMessages.add(EMAIL_IS_FALSE);
        }

        // password validation
        String passwordRegex = configurationManager.getProperty(PASSWORD_REGEX);
        Pattern pPassword = Pattern.compile(passwordRegex);
        Matcher mPassword = pPassword.matcher(password);

        if (password == null || password.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_EMPTY);
        } else if (!mPassword.matches()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_FALSE);
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_VALID_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of user info parameters while correcting user info action
     * and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public ArrayList<String> validateUserMainData(String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String keyWordType, String keyWord) {
        ArrayList<String> errorMessages = checkCommonUserParameters(login, name, surname, email, bDay, bMonth, bYear, sex, country, city, keyWordType, keyWord);

        // adding error title if list has already contained errors
        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, ERROR_HEADER);
            return errorMessages;
        } else {
            return errorMessages;
        }
    }

    /**
     * method validates values of password while changing user password action
     * and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validateNewPassword(String password1, String password2, String password3) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        // passwords validation
        String passwordRegex = configurationManager.getProperty(PASSWORD_REGEX);
        Pattern pPassword = Pattern.compile(passwordRegex);
        Matcher mPassword1 = pPassword.matcher(password1);
        Matcher mPassword2 = pPassword.matcher(password2);

        if (password1 == null || password1.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_EMPTY);
        } else if (!mPassword1.matches()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_FALSE);
        }

        if (password2 == null || password2.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_EMPTY);
        } else if (password3 == null || password3.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_EMPTY);
        } else if (!password2.equals(password3)) {
            successful = false;
            errorMessages.add(PASSWORDS_NOT_EQUAL);
        } else if (!mPassword2.matches()) {
            successful = false;
            errorMessages.add(PASSWORD_FALSE);
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_VALID_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of password, type of key word and its value while password recovering action
     * and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    public List<String> validatePasswordRecovData(String email, String keyWordType, String keyWordValue) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();
        boolean successful = true;

        //email validation
        String emailRegex = configurationManager.getProperty(EMAIL_REGEX);
        Pattern pEmail = Pattern.compile(emailRegex);
        Matcher mEmail = pEmail.matcher(email);

        if (email == null || email.isEmpty()) {
            successful = false;
            errorMessages.add(EMAIL_EMPTY);
        } else if (!mEmail.matches()) {
            successful = false;
            errorMessages.add(EMAIL_FALSE);
        }

        // validation of type of key word
        String keyWordNotChosen = configurationManager.getProperty(KEY_WORD_TYPE_UNCHOSEN);
        String keyWord1 = configurationManager.getProperty(KEY_WORD_TYPE_MAIDEN_NAME);
        String keyWord2 = configurationManager.getProperty(KEY_WORD_TYPE_PASS_NO);
        String keyWord3 = configurationManager.getProperty(KEY_WORD_TYPE_PET_NICK);
        String keyWord4 = configurationManager.getProperty(KEY_WORD_TYPE_CODEWORD);

        if (keyWordType == null || keyWordType.isEmpty() || keyWordType.equals(keyWordNotChosen)) {
            successful = false;
            errorMessages.add(KEY_WORD_TYPE_EMPTY);
        } else if (!(keyWord1.equals(keyWordType) || keyWord2.equals(keyWordType) || keyWord3.equals(keyWordType) || keyWord4.equals(keyWordType))) {
            successful = false;
            errorMessages.add(KEY_WORD_TYPE_WRONG);
        }

        //validation of key word value
        String keyWordRegex = configurationManager.getProperty(KEY_WORD_REGEX);
        Pattern pKeyWord = Pattern.compile(keyWordRegex);
        Matcher mKeyWord = pKeyWord.matcher(keyWordValue);
        if (keyWordValue == null || keyWordValue.isEmpty()) {
            successful = false;
            errorMessages.add(KEY_WORD_EMPTY);
        } else if (!mKeyWord.matches()) {
            successful = false;
            errorMessages.add(KEY_WORD_WRONG);
        }

        // adding error title if list has already contained errors
        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_VALID_HEADER);
            return errorMessages;
        }
    }

    /**
     * method validates values of parameters, common for several other methods
     * and adds validation error messages into returned
     * list if parameters are incorrect or don't match expected limits or regex
     *
     * @return list of validation errors or empty list
     */
    private ArrayList<String> checkCommonUserParameters(String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String keyWordType, String keyWord) {
        ArrayList<String> errorMessages = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager();

        // 1. login validation
        String loginRegex = configurationManager.getProperty(LOGIN_REGEX);
        Pattern pLogin = Pattern.compile(loginRegex);
        Matcher mLogin = pLogin.matcher(login);
        if (login == null || login.isEmpty()) {
            errorMessages.add(LOGIN_EMPTY);
        } else if (!mLogin.matches()) {
            errorMessages.add(LOGIN_FALSE);
        }

        // 2. name validation
        String nameRegex = configurationManager.getProperty(NAME_REGEX);
        Pattern pName = Pattern.compile(nameRegex);
        Matcher mName = pName.matcher(name);

        if (name == null || name.isEmpty()) {
            errorMessages.add(NAME_EMPTY);
        } else if (!mName.matches()) {
            errorMessages.add(NAME_FALSE);
        }

        // 3. surname validation
        Pattern pSurname = Pattern.compile(nameRegex);
        Matcher mSurname = pSurname.matcher(name);

        if (surname == null || surname.isEmpty()) {
            errorMessages.add(SURNAME_EMPTY);
        } else if (!mSurname.matches()) {
            errorMessages.add(SURNAME_FALSE);
        }

        // 4. email validation
        String emailRegex = configurationManager.getProperty(EMAIL_REGEX);
        Pattern pEmail = Pattern.compile(emailRegex);
        Matcher mEmail = pEmail.matcher(email);

        if (email == null || email.isEmpty()) {
            errorMessages.add(EMAIL_EMPTY);
        } else if (!mEmail.matches()) {
            errorMessages.add(EMAIL_FALSE);
        }

        //5. date validation
        if (bDay == null || bDay.isEmpty()) {
            errorMessages.add(DAY_EMPTY);
        } else {
            try {
                int day = Integer.parseInt(bDay);
                if (day < MIN_DAY || day > MAX_DAY) {
                    errorMessages.add(DAY_OUT_LIMIT);
                }
            } catch (NumberFormatException e) {
                errorMessages.add(DATE_NOT_NUMBER);
            }
        }

        if (bMonth == null || bMonth.isEmpty()) {
            errorMessages.add(MONTH_EMPTY);
        } else {
            try {
                int month = Integer.parseInt(bMonth);
                if (month < MIN_MONTH || month > MAX_MONTH) {
                    errorMessages.add(MONTH_OUT_LIMIT);
                }
            } catch (NumberFormatException e) {
                errorMessages.add(DATE_NOT_NUMBER);
            }
        }

        int minYear = Integer.parseInt(configurationManager.getProperty(YEAR_LOW_LIMIT));
        int maxYear = Integer.parseInt(configurationManager.getProperty(YEAR_TOP_LIMIT));
        if (bYear == null || bYear.isEmpty()) {
            errorMessages.add(YEAR_EMPTY);
        } else {
            try {
                int year = Integer.parseInt(bYear);
                if (year < minYear || year > maxYear) {
                    errorMessages.add(YEAR_OUT_LIMIT);
                }
            } catch (NumberFormatException e) {
                errorMessages.add(DATE_NOT_NUMBER);
            }
        }

        //6. sex validation
        String sexUnchosen = configurationManager.getProperty(SEX_UNCHOSEN);
        String male = configurationManager.getProperty(MALE);
        String female = configurationManager.getProperty(FEMALE);

        if (sex == null || sex.isEmpty() || sex.equals(sexUnchosen)) {
            errorMessages.add(SEX_EMPTY);
        } else if (!(male.equals(sex) || female.equals(sex))) {
            errorMessages.add(SEX_WRONG_TYPE);
        }

        //7. type of key word validation
        String keyWordNotChosen = configurationManager.getProperty(KEY_WORD_TYPE_UNCHOSEN);
        String keyWord1 = configurationManager.getProperty(KEY_WORD_TYPE_MAIDEN_NAME);
        String keyWord2 = configurationManager.getProperty(KEY_WORD_TYPE_PASS_NO);
        String keyWord3 = configurationManager.getProperty(KEY_WORD_TYPE_PET_NICK);
        String keyWord4 = configurationManager.getProperty(KEY_WORD_TYPE_CODEWORD);

        if (keyWordType == null || keyWordType.isEmpty() || keyWordType.equals(keyWordNotChosen)) {
            errorMessages.add(KEY_WORD_TYPE_EMPTY);
        } else if (!(keyWord1.equals(keyWordType) || keyWord2.equals(keyWordType) || keyWord3.equals(keyWordType) || keyWord4.equals(keyWordType))) {
            errorMessages.add(KEY_WORD_TYPE_WRONG);
        }

        //8. key word validation
        String keyWordRegex = configurationManager.getProperty(KEY_WORD_REGEX);
        Pattern pKeyWord = Pattern.compile(keyWordRegex);
        Matcher mKeyWord = pKeyWord.matcher(keyWord);
        if (keyWord == null || keyWord.isEmpty()) {
            errorMessages.add(KEY_WORD_EMPTY);
        } else if (!mKeyWord.matches()) {
            errorMessages.add(KEY_WORD_WRONG);
        }

        //9. country validation
        String geoRegex = configurationManager.getProperty(GEO_REGEX);
        if (!(country == null || country.isEmpty())) {
            Pattern pCountry = Pattern.compile(geoRegex);
            Matcher mCountry = pCountry.matcher(country);
            if (!mCountry.matches()) {
                errorMessages.add(COUNTRY_FALSE);
            }
        }

        //10. city validation
        if (!(city == null || city.isEmpty())) {
            Pattern pCity = Pattern.compile(geoRegex);
            Matcher mCity = pCity.matcher(city);
            if (!mCity.matches()) {
                errorMessages.add(CITY_FALSE);
            }
        }

        return errorMessages;
    }
}
