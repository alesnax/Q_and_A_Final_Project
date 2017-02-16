function validateForm() {
    var locale = arguments[0];
    var result = true;
    var FILL_FIELD = "You can't leave this empty.",
        NAME_RULE = "Name can consist of letters and '-' for complicated names. Length from 2 to 45 symbols.",
        LOGIN_RULE = "First symbol should be latin letter.",
        LOGIN_LEN = "Please use between 6 and 20 characters",
        LOGIN_WRONG = "Please use only letters (A-Z or a-z), numbers, and underline.",
        PWD_NOT_EQUAL = "These passwords don't match. Try again?",
        SHORT_PWD = "Short passwords are easy to guess. Try one with at least 8 characters.",
        PWD_RULE = "Use at least one lowercase letter, one uppercase letter, one numeral, and 8 characters.",
        EMAIL_ERROR = "Please, check email, address should contain '@' and '.' symbols",
        WRONG_DATE = "Sorry, check your birthday, maybe there is no such date in calendar.",
        CHOOSE_DAY = "Please, choose day.",
        CHOOSE_MONTH = "Please, choose month.",
        CHOOSE_YEAR = "Please, choose year.",
        CHOOSE_GENDER = "Please, choose your gender.",
        COUNTRY_RULE = "Country name should consist of letters(A-Za-z or А-Яа-я). Max length = 45 symbols.",
        CITY_RULE = "City name should consist of letters(A-Za-z or А-Яа-я). Max length = 45 symbols.",
        CHOOSE_KEYWORD_TYPE = "Please choose type of key word",
        KEYWORD_RULE = "KeyWord can consist of letters(A-Za-z or А-Яа-я), digits and '-' sign. Length from 5 to 45 symbols.",
        KEYWORD_LEN = "Length from 5 to 45 symbols.";



    if (locale == 'ru') {
        FILL_FIELD = "Поле не должно быть пустым.",
            NAME_RULE = "Имя может состоять из букв и символа '-'. Длина поля от 2 до 45 символов.",
            LOGIN_RULE = "Первый символ должен быть латинской буквой.",
            LOGIN_LEN = "Длина от 6 до 20 символов.",
            LOGIN_WRONG = "Пожалуйста, используйте буквы (A-Z or a-z), цифры и символ '_'.",
            PWD_NOT_EQUAL = "Пароли не совпадают. Повторите ввод.",
            SHORT_PWD = "Короткие пароли легко отгадать, используйте пароль длиной не менее 8 символов.",
            PWD_RULE = "Используйте минимум одну строчную, одну заглавную букву, одну цифру и длину пароля не менее 8 символов.",
            EMAIL_ERROR = "Пожалуйста, проверьте email. Поле 'email' должно содержать символы '@' и '.' и быть корректным.",
            WRONG_DATE = "Пожалуйста, проверьте дату рождения. Такого числа не существует.",
            CHOOSE_DAY = "Пожалуйста, выберите день.",
            CHOOSE_MONTH = "Пожалуйста, выберите месяц.",
            CHOOSE_YEAR = "Пожалуйста, выберите год.",
            CHOOSE_GENDER = "Пожалуйста, выберите пол.",
            COUNTRY_RULE = "Название страны должно состоять из букв(A-Za-z или А-Яа-я). Максимальная длина - 45 символов.",
            CITY_RULE = "Название города должно состоять из букв(A-Za-z или А-Яа-я). Максимальная длина - 45 символов.",
            CHOOSE_KEYWORD_TYPE = "Пожалуйста, выберите тип ключевого слова.",
            KEYWORD_RULE = "Ключевое слово может состоять из букв(A-Za-z или А-Яа-я), цифр и символа '-'. Длина поля от 5 до 45 символов.",
            KEYWORD_LEN = "Длина поля от 5 до 45 символов.";
    }

    var errFname = document.getElementById("error_0_FirstName"),
        errLname = document.getElementById("error_0_LastName"),
        errLogin = document.getElementById("error_0_login"),
        errPwd1 = document.getElementById("error_0_Passwd"),
        errPwd2 = document.getElementById("error_0_PasswdAgain"),
        errEmail = document.getElementById("error_0_email"),
        errBirth = document.getElementById("error_0_birthday"),
        errSex = document.getElementById("error_0_gender"),
        errCountry = document.getElementById("error_0_country"),
        errCity = document.getElementById("error_0_city"),
        errKeyWord = document.getElementById("error_0_keyWord"),
        errKeyWordValue = document.getElementById("error_0_keyWordValue");

    var fNameClass = document.getElementById("FirstName"),
        lNameClass = document.getElementById("LastName"),
        userNameClass = document.getElementById("login"),
        password1Class = document.getElementById("Passwd"),
        password2Class = document.getElementById("PasswdAgain"),
        uEmailClass = document.getElementById("email"),
        uCountryClass = document.getElementById("country"),
        uCityClass = document.getElementById("city"),
        keyWordClass = document.getElementById("key_word_value");


    var x = document.getElementsByClassName("form_error");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.remove("form_error");
    }
    password1Class.classList.remove("form_error");

    errFname.innerHTML = "";
    errLname.innerHTML = "";
    errLogin.innerHTML = "";
    errPwd1.innerHTML = "";
    errPwd2.innerHTML = "";
    errEmail.innerHTML = "";
    errBirth.innerHTML = "";
    errSex.innerHTML = "";
    errCountry.innerHTML = "";
    errCity.innerHTML = "";
    errKeyWord.innerHTML = "";
    errKeyWordValue.innerHTML = "";

    var fName = document.create_account.FirstName.value,
        lName = document.create_account.LastName.value,
        userName = document.create_account.login.value,
        password1 = document.create_account.Passwd.value,
        password2 = document.create_account.PasswdAgain.value,
        uEmail = document.create_account.email.value,
        bDay = document.create_account.birth_day.value,
        bMonth = document.create_account.birth_month.value,
        bYear = document.create_account.birth_year.value,
        uGender = document.create_account.gender.value,
        uCountry = document.create_account.country.value,
        uCity = document.create_account.city.value,
        keyWord = document.create_account.key_word.value,
        keyWordValue = document.create_account.key_word_value.value;



    var regexp = /(^[A-Za-z][A-Za-z\-]{0,44}$|^[А-Яа-я][А-Яа-я\-]{0,44}$)/;
    var regexp1 = /^[A-Za-z][A-Za-z0-9_]{5,19}$/;
    var regexp2 = /^([\w\.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}$)/i;
    var regexp3 = /(^[A-Za-z]{2,45}$)|^[А-Яа-я]{2,45}$|^$/;
    var regexp4 = /(^[A-Za-zА-Яа-я0-9\-\s]{5,45}$)/;

    if (!fName) {
        errFname.innerHTML = FILL_FIELD;
        fNameClass.className = "form_error";
        result = false;
    } else if (!regexp.test(fName)) {
        errFname.innerHTML = NAME_RULE;
        fNameClass.className = "form_error";
        result = false;
    }

    if (!lName) {
        errLname.innerHTML = FILL_FIELD;
        lNameClass.className = "form_error";
        result = false;
    } else if (!regexp.test(lName)) {
        errLname.innerHTML = NAME_RULE;
        lNameClass.className = "form_error";
        result = false;
    }

    if (!userName) {
        errLogin.innerHTML = FILL_FIELD;
        userNameClass.className = "form_error";
        result = false;
    } else if (userName.length < 6) {
        errLogin.innerHTML = LOGIN_LEN;
        userNameClass.className = "form_error";
        result = false;
    } else if (userName.length > 20) {
        errLogin.innerHTML = LOGIN_LEN;
        userNameClass.className = "form_error";
        result = false;
    } else if (userName && userName.search(/[a-z]/i) !== 0) {
        errLogin.innerHTML = LOGIN_RULE;
        userNameClass.className = "form_error";
        result = false;
    } else if (!regexp1.test(userName)) {
        errLogin.innerHTML = LOGIN_WRONG;
        userNameClass.className = "form_error";
        result = false;
    }

    if (!password1) {
        errPwd1.innerHTML = FILL_FIELD;
        password1Class.className = "form_error";
        result = false;
    } else if (password1.length < 8) {
        errPwd1.innerHTML = SHORT_PWD;
        password1Class.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[a-z]/) == -1) {
        errPwd1.innerHTML = PWD_RULE;
        password1Class.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[A-Z]/) == -1) {
        errPwd1.innerHTML = PWD_RULE;
        password1Class.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[0-9]/) == -1) {
        errPwd1.innerHTML = PWD_RULE;
        password1Class.className = "form_error";
        result = false;
    }

    if (!password2) {
        errPwd2.innerHTML = FILL_FIELD;
        password2Class.className = "form_error";
        result = false;
    }


    if (password1 && password2 && password1 !== password2) {
        password1Class.className = "form_error";
        password2Class.className = "form_error";
        errPwd2.innerHTML = PWD_NOT_EQUAL;
        document.create_account.Passwd.value = "";
        document.create_account.PasswdAgain.value = "";
        result = false;
    }


    if (!uEmail) {
        errEmail.innerHTML = FILL_FIELD;
        uEmailClass.className = "form_error";
        result = false;
    } else if (!regexp2.test(uEmail)) {
        errEmail.innerHTML = EMAIL_ERROR;
        uEmailClass.className = "form_error";
        result = false;
    }

    if (bDay == 0) {
        errBirth.innerHTML = CHOOSE_DAY;
        result = false;
    } else if (bMonth == 0) {
        errBirth.innerHTML = CHOOSE_MONTH;
        result = false;
    } else if (bYear == 0) {
        errBirth.innerHTML = CHOOSE_YEAR;
        result = false;
    }

    if (uGender == 0) {
        errSex.innerHTML = CHOOSE_GENDER;
        result = false;
    }

    if (keyWord == 0) {
        errKeyWord.innerHTML = CHOOSE_KEYWORD_TYPE;
        result = false;
    }

    if (!keyWordValue) {
        errKeyWordValue.innerHTML = FILL_FIELD;
        keyWordClass.className = "form_error";
        result = false;
    } else if (keyWordValue.length < 6) {
        errKeyWordValue.innerHTML = KEYWORD_LEN;
        keyWordClass.className = "form_error";
        result = false;
    } else if (keyWordValue.length > 45) {
        errKeyWordValue.innerHTML = KEYWORD_LEN;
        keyWordClass.className = "form_error";
        result = false;
    } else if (!regexp4.test(keyWordValue)) {
        errKeyWordValue.innerHTML = KEYWORD_RULE;
        keyWordClass.className = "form_error";
        result = false;
    }

    if (!regexp3.test(uCountry)) {
        errCountry.innerHTML = COUNTRY_RULE;
        uCountryClass.className = "form_error";
        result = false;
    }

    if (!regexp3.test(uCity)) {
        errCity.innerHTML = CITY_RULE;
        uCityClass.className = "form_error";
        result = false;
    }

    return result;
}