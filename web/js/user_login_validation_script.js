function validateLoginForm() {
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



    var errPwd1 = document.getElementById("error_0_Passwd"),
        errEmail = document.getElementById("error_0_email");

    var password1Class = document.getElementById("Passwd"),
        uEmailClass = document.getElementById("email");


    var x = document.getElementsByClassName("form_error");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.remove("form_error");
    }
    password1Class.classList.remove("form_error");

    errPwd1.innerHTML = "";
    errEmail.innerHTML = "";


    var password1 = document.create_account.Passwd.value,
        uEmail = document.create_account.email.value;

    var regexp2 = /^([\w\.]+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}$)/i;


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

    if (!uEmail) {
        errEmail.innerHTML = FILL_FIELD;
        uEmailClass.className = "form_error";
        result = false;
    } else if (!regexp2.test(uEmail)) {
        errEmail.innerHTML = EMAIL_ERROR;
        uEmailClass.className = "form_error";
        result = false;
    }

    return result;
}
