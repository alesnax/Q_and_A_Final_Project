package by.alesnax.qanda.validation;

import by.alesnax.qanda.resource.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alesnax on 05.12.2016.
 */

// сделать регистрацию через имэйл с подтверждением а затем вторая часть регистрации.


@SuppressWarnings("Duplicates")
public class UserValidation {

    private static final String ERROR_HEADER = "user_registration.error_msg.error_header";
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

    private static final String MALE = "user_registration_page.form_value.sex.male";
    private static final String FEMALE = "user_registration_page.form_value.sex.female";
    private static final String SEX_UNCHOSEN = "user_registration_page.form_value.sex.unchosen";
    private static final String GEO_REGEX = "user_validation.geo_regex";
    private static final String LOGIN_REGEX = "user_validation.login_regex";
    private static final String PASSWORD_REGEX = "user_validation.password_regex";
    private static final String NAME_REGEX = "user_validation.name_regex";
    private static final String EMAIL_REGEX = "user_validation.email_regex";
    private static final String YEAR_LOW_LIMIT = "user_registration_page.year_low_limit";
    private static final String YEAR_TOP_LIMIT = "user_registration_page.year_top_limit";

    private static final String ERROR_VALID_HEADER = "user_authorization.error_msg.error_header";
    private static final String EMAIL_IS_EMPTY = "user_authorization.error_msg.email_empty";
    private static final String EMAIL_IS_FALSE = "user_authorization.error_msg.email_false";
    private static final String PASSWORD_IS_EMPTY = "user_authorization.error_msg.password_empty";
    private static final String PASSWORD_IS_FALSE = "user_authorization.error_msg.password_false";

    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;


    public ArrayList<String> validateNewUser(String login, String password, String passwordCopy, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city) {
        boolean successful = true;

        ArrayList<String> errorMessages = checkCommonUserParameters(login, name, surname, email, bDay, bMonth, bYear, sex, country, city);
        if (!errorMessages.isEmpty()) {
            successful = false;
        }

        // 2. passwords
        String passwordRegex = ConfigurationManager.getProperty(PASSWORD_REGEX);
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

        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_HEADER);
            return errorMessages;
        }
    }

    public List<String> validateUserInfo(String email, String password) {
        ArrayList<String> errorMessages = new ArrayList<>();
        boolean successful = true;

        String emailRegex = ConfigurationManager.getProperty(EMAIL_REGEX);
        Pattern pEmail = Pattern.compile(emailRegex);
        Matcher mEmail = pEmail.matcher(email);

        if (email == null || email.isEmpty()) {
            errorMessages.add(EMAIL_IS_EMPTY);
        } else if (!mEmail.matches()) {
            successful = false;
            errorMessages.add(EMAIL_IS_FALSE);
        }

        String passwordRegex = ConfigurationManager.getProperty(PASSWORD_REGEX);
        Pattern pPassword = Pattern.compile(passwordRegex);
        Matcher mPassword = pPassword.matcher(password);

        if (password == null || password.isEmpty()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_EMPTY);
        } else if (!mPassword.matches()) {
            successful = false;
            errorMessages.add(PASSWORD_IS_FALSE);
        }

        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_VALID_HEADER);
            return errorMessages;
        }
    }

    public ArrayList<String> validateUserMainData(String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city) {
        ArrayList<String> errorMessages = checkCommonUserParameters(login, name, surname, email, bDay, bMonth, bYear, sex, country, city);
        if (!errorMessages.isEmpty()) {
            errorMessages.add(0, ERROR_HEADER);
            return errorMessages;
        } else {
            return errorMessages;
        }
    }

    private ArrayList<String> checkCommonUserParameters(String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city) {
        ArrayList<String> errorMessages = new ArrayList<>();

        // 1. login
        String loginRegex = ConfigurationManager.getProperty(LOGIN_REGEX);
        Pattern pLogin = Pattern.compile(loginRegex);
        Matcher mLogin = pLogin.matcher(login);
        if (login == null || login.isEmpty()) {
            errorMessages.add(LOGIN_EMPTY);
        } else if (!mLogin.matches()) {
            errorMessages.add(LOGIN_FALSE);
        }

        // 3. name
        String nameRegex = ConfigurationManager.getProperty(NAME_REGEX);
        Pattern pName = Pattern.compile(nameRegex);
        Matcher mName = pName.matcher(name);

        if (name == null || name.isEmpty()) {
            errorMessages.add(NAME_EMPTY);
        } else if (!mName.matches()) {
            errorMessages.add(NAME_FALSE);
        }

        // 4. surname
        Pattern pSurname = Pattern.compile(nameRegex);
        Matcher mSurname = pSurname.matcher(name);

        if (surname == null || surname.isEmpty()) {
            errorMessages.add(SURNAME_EMPTY);
        } else if (!mSurname.matches()) {
            errorMessages.add(SURNAME_FALSE);
        }

        // 5. email
        String emailRegex = ConfigurationManager.getProperty(EMAIL_REGEX);
        Pattern pEmail = Pattern.compile(emailRegex);
        Matcher mEmail = pEmail.matcher(email);

        if (email == null || email.isEmpty()) {
            errorMessages.add(EMAIL_EMPTY);
        } else if (!mEmail.matches()) {
            errorMessages.add(EMAIL_FALSE);
        }

        //6. date
        // реализовать для високосных годов февраля и тд
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

        int minYear = Integer.parseInt(ConfigurationManager.getProperty(YEAR_LOW_LIMIT));
        int maxYear = Integer.parseInt(ConfigurationManager.getProperty(YEAR_TOP_LIMIT));
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

        String sexUnchosen = ConfigurationManager.getProperty(SEX_UNCHOSEN);
        String male = ConfigurationManager.getProperty(MALE);
        String female = ConfigurationManager.getProperty(FEMALE);

        if (sex == null || sex.isEmpty() || sex.equals(sexUnchosen)) {
            errorMessages.add(SEX_EMPTY);
        } else if (!(male.equals(sex) || female.equals(sex))) {
            errorMessages.add(SEX_WRONG_TYPE);
        }

        String geoRegex = ConfigurationManager.getProperty(GEO_REGEX);
        if (!(country == null || country.isEmpty())) {
            Pattern pCountry = Pattern.compile(geoRegex);
            Matcher mCountry = pCountry.matcher(country);
            if (!mCountry.matches()) {
                errorMessages.add(COUNTRY_FALSE);
            }
        }
        if (!(city == null || city.isEmpty())) {
            Pattern pCity = Pattern.compile(geoRegex);
            Matcher mCity = pCity.matcher(city);
            if (!mCity.matches()) {
                errorMessages.add(CITY_FALSE);
            }
        }

        return errorMessages;
    }

    public List<String> validateNewPassword(String password1, String password2, String password3) {
        ArrayList<String> errorMessages = new ArrayList<>();
        boolean successful = true;

        String passwordRegex = ConfigurationManager.getProperty(PASSWORD_REGEX);
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

        if (successful) {
            return errorMessages;
        } else {
            errorMessages.add(0, ERROR_VALID_HEADER);
            return errorMessages;
        }
    }
}
