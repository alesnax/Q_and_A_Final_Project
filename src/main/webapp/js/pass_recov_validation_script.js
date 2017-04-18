/**
 * Created by alesnax on 17.01.2017.
 */

function validateRecoveringForm() {
    var locale = arguments[0];
    var result = true;
    var FILL_FIELD = "You can't leave this empty.",
        EMAIL_ERROR = "Please, check email, address should contain '@' and '.' symbols",
        CHOOSE_KEYWORD_TYPE = "Please choose type of key word",
        KEYWORD_RULE = "KeyWord can consist of letters(A-Za-z or А-Яа-я), digits and '-' sign. Length from 5 to 45 symbols.",
        KEYWORD_LEN = "Length from 5 to 45 symbols.";


    if (locale == 'ru') {
        FILL_FIELD = "Поле не должно быть пустым.";
        EMAIL_ERROR = "Пожалуйста, проверьте email. Поле 'email' должно содержать символы '@' и '.' и быть корректным.";
        CHOOSE_KEYWORD_TYPE = "Пожалуйста, выберите тип ключевого слова.",
        KEYWORD_RULE = "Ключевое слово может состоять из букв(A-Za-z или А-Яа-я), цифр и символа '-'. Длина поля от 5 до 45 символов.",
        KEYWORD_LEN = "Длина поля от 5 до 45 символов.";
    }

    var errEmail = document.getElementById("error_0_email"),
        errKeyWord = document.getElementById("error_0_keyWord"),
        errKeyWordValue = document.getElementById("error_0_keyWordValue");

    var uEmailClass = document.getElementById("rec_email"),
        keyWordClass = document.getElementById("key_word_value");


    var x = document.getElementsByClassName("form_error");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.remove("form_error");
    }

    errEmail.innerHTML = "";
    errKeyWord.innerHTML = "";
    errKeyWordValue.innerHTML = "";

    var uEmail = document.pass_recov_form.email.value,
        keyWord = document.pass_recov_form.key_word.value,
        keyWordValue = document.pass_recov_form.key_word_value.value;


    var regexp2 = /^([\w\.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}$)/i;
    var regexp4 = /(^[A-Za-zА-Яа-я0-9\-\s]{5,45}$)/;

    if (!uEmail) {
        errEmail.innerHTML = FILL_FIELD;
        uEmailClass.className = "form_error";
        result = false;
    } else if (!regexp2.test(uEmail)) {
        errEmail.innerHTML = EMAIL_ERROR;
        uEmailClass.className = "form_error";
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
    } else if (keyWordValue.length < 5) {
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
    return result;
}



