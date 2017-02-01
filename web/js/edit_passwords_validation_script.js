/**
 * Created by alesnax on 06.01.2017.
 */

function validatePasswords() {
    var locale = arguments[0];
    var result = true;
    var FILL_FIELD = "You can't leave this empty.",
        PWD_NOT_EQUAL = "These passwords don't match. Try again?",
        SHORT_PWD = "Short passwords are easy to guess. Try one with at least 8 characters.",
        PWD_RULE = "Use at least one lowercase letter, one uppercase letter, one numeral, and 8 characters.";

    if (locale == 'ru') {
        FILL_FIELD = "Поле не должно быть пустым.",
            PWD_NOT_EQUAL = "Пароли не совпадают. Повторите ввод.",
            SHORT_PWD = "Короткие пароли легко отгадать, используйте пароль длиной не менее 8 символов.",
            PWD_RULE = "Используйте минимум одну строчную, одну заглавную букву, одну цифру и длину пароля не менее 8 символов.";
    }

    var errPwd0 = document.getElementById("error_0_OldPasswd"),
        errPwd1 = document.getElementById("error_0_Passwd"),
        errPwd2 = document.getElementById("error_0_PasswdAgain");

    var password0Class = document.getElementById("OldPasswd"),
        password1Class = document.getElementById("Passwd"),
        password2Class = document.getElementById("PasswdAgain");

    password0Class.classList.remove("form_error");
    password1Class.classList.remove("form_error");
    password2Class.classList.remove("form_error");

    errPwd0.innerHTML = "";
    errPwd1.innerHTML = "";
    errPwd2.innerHTML = "";

    var password0 = document.change_password.OldPasswd.value,
        password1 = document.change_password.Passwd.value,
        password2 = document.change_password.PasswdAgain.value;

    if (!password0) {
        errPwd0.innerHTML = FILL_FIELD;
        password0Class.className = "form_error";
        result = false;
    } else if (password0.length < 8) {
        errPwd0.innerHTML = SHORT_PWD;
        password0Class.className = "form_error";
        result = false;
    } else if (password0 && password0.search(/[a-z]/) == -1) {
        errPwd0.innerHTML = PWD_RULE;
        password0Class.className = "form_error";
        result = false;
    } else if (password0 && password0.search(/[A-Z]/) == -1) {
        errPwd0.innerHTML = PWD_RULE;
        password0Class.className = "form_error";
        result = false;
    } else if (password0 && password0.search(/[0-9]/) == -1) {
        errPwd0.innerHTML = PWD_RULE;
        password0Class.className = "form_error";
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
        document.change_password.Passwd.value = "";
        document.change_password.PasswdAgain.value = "";
        result = false;
    }

    return result;
}

function validateDeletedPassword() {
    var result = true;
    var locale = arguments[0];
    var FILL_FIELD = "You can't leave this empty.",
        SHORT_PWD = "Short passwords are easy to guess. Try one with at least 8 characters.",
        PWD_RULE = "Use at least one lowercase letter, one uppercase letter, one numeral, and 8 characters.",
        EMAIL_ERROR = "Please, check email, address should contain '@' and '.' symbols";

    if (locale == 'ru') {
        FILL_FIELD = "Поле не должно быть пустым.",
            SHORT_PWD = "Короткие пароли легко отгадать, используйте пароль длиной не менее 8 символов.",
            PWD_RULE = "Используйте минимум одну строчную, одну заглавную букву, одну цифру и длину пароля не менее 8 символов.",
            EMAIL_ERROR = "Пожалуйста, проверьте email. Поле 'email' должно содержать символы '@' и '.' и быть корректным.";
    }

    var errPwdDel = document.getElementById("error_0_delPasswd");
    var password1ClassDel = document.getElementById("delPasswd");

    var x = document.getElementsByClassName("form_error");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.remove("form_error");
    }
    password1ClassDel.classList.remove("form_error");

    errPwdDel.innerHTML = "";

    var password1 = document.delete_account.delPasswd.value;

    if (!password1) {
        errPwdDel.innerHTML = FILL_FIELD;
        password1ClassDel.className = "form_error";
        result = false;
    } else if (password1.length < 8) {
        errPwdDel.innerHTML = SHORT_PWD;
        password1ClassDel.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[a-z]/) == -1) {
        errPwdDel.innerHTML = PWD_RULE;
        password1ClassDel.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[A-Z]/) == -1) {
        errPwdDel.innerHTML = PWD_RULE;
        password1ClassDel.className = "form_error";
        result = false;
    } else if (password1 && password1.search(/[0-9]/) == -1) {
        errPwdDel.innerHTML = PWD_RULE;
        password1ClassDel.className = "form_error";
        result = false;
    }

    return result;
}
