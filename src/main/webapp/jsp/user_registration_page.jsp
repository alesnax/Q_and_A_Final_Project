<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <meta charset="utf-8">
    <title>
        <fmt:message bundle="${loc}" key="common.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/user_registration_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
<fmt:message bundle="${config}" key="command.go_to_authorization_page" var="go_to_authorization_page"/>
<fmt:message bundle="${config}" key="img.common.logo" var="main_logo"/>

<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <fmt:message bundle="${config}" key="path.page.main" var="main"/>
            <a href="${go_to_main}">
                <img class="header_logo" src="${main_logo}" alt="Q&A logo"/>
            </a>
        </div>
        <c:import url="template/header_search_block.jsp"/>
        <c:import url="template/switch_language.jsp"/>
        <div class="fl_r h_links">
            <a class="h_link" href="${go_to_authorization_page}">
                <fmt:message bundle="${loc}" key="common.sign_in_text"/>
            </a>
        </div>
    </div>
</header>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <div class="wall_content wide_block">
                <div class="validation_header page_block ">
                    <div class="logo_block">
                        <a href="${go_to_main}">
                            <img class="link_logo" src="${main_logo}" alt="QA logo"/>
                        </a>
                    </div>
                    <div class="welcome_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="guest.user_authorization_page.welcome_simple_msg"/>
                        </h1>
                        <h2>
                            <fmt:message bundle="${loc}" key="user_registration.h2.text.please_register"/>
                        </h2>
                    </div>
                </div>
                <div class="validation_block ">
                    <div class="create_account_form_block page_block">
                        <form onsubmit="return validateForm('${sessionScope.locale}')" class="create_account_form" id="create_account"
                              name="create_account" action="../Controller" method="post">
                            <input type="hidden" name="command" value="register_new_user"/>
                            <div class="form_element name">
                                <c:if test="${not empty sessionScope.user_validation_error}">
                                    <c:forEach var="error" items="${sessionScope.user_validation_error}">
                                        <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="${error}"/>
                                        </span>
                                    </c:forEach>
                                    <c:remove var="user_validation_error" scope="session"/>
                                </c:if>
                            </div>
                            <div class="form_element name">
                                <fieldset>
                                    <legend>
                                        <fmt:message bundle="${loc}" key="user_registration.form.first_name.placeholder" var="fname_ph"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.last_name.placeholder" var="lname_ph"/>
                                        <strong>
                                            <fmt:message bundle="${loc}" key="user_registration.form.name.legend"/>
                                            <span class="notice_star">*</span>
                                        </strong>
                                    </legend>
                                    <input type="text" value name="FirstName" id="FirstName" class="" placeholder="${fname_ph}">
                                    <span class="errormsg" id="error_0_FirstName"></span>
                                    <input type="text" value name="LastName" id="LastName" class="" placeholder="${lname_ph}">
                                    <span class="errormsg" id="error_0_LastName"></span>
                                </fieldset>
                            </div>
                            <div class="form_element login">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.login.placeholder" var="login_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.login.legend"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="text" value name="login" id="login" class="" placeholder="${login_ph}">
                                </label>
                                <span class="errormsg" id="error_0_login"></span>
                            </div>
                            <div class="form_element password_form_element">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.pass1.placeholder" var="p1_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.pass1.label"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="password" value name="Passwd" id="Passwd" class="" placeholder="${p1_ph}">
                                </label>
                                <span class="errormsg" id="error_0_Passwd"></span>
                            </div>
                            <div class="form_element password_form_element">
                                <label>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.pass2.label"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="password" value name="PasswdAgain" id="PasswdAgain" class="" placeholder="${p1_ph}">
                                </label>
                                <span class="errormsg" id="error_0_PasswdAgain"></span>
                            </div>
                            <div class="form_element login">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.email.placeholder" var="email_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.email.label"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="text" value name="email" id="email" class="" placeholder="${email_ph}">
                                </label>
                                <span class="errormsg" id="error_0_email"></span>
                            </div>
                            <div class="form_element birtday">
                                <fieldset>
                                    <legend>
                                        <strong>
                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.label"/>
                                            <span class="notice_star">*</span>
                                        </strong>
                                    </legend>
                                    <select class="birth_day" name="birth_day">
                                        <option value="0" selected="selected" disabled>
                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.day.select_value"/>
                                        </option>
                                        <c:forEach var="day" begin="1" end="31">
                                            <option value="${day}">${day}</option>
                                        </c:forEach>
                                    </select>
                                    <select class="birth_month" name="birth_month">
                                        <option value="0" selected="selected" disabled>
                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.select_value"/>
                                        </option>
                                        <c:forEach var="m_numb" begin="1" end="12">
                                            <option value="${m_numb}">
                                                <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.value${m_numb}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <select class="birth_year" name="birth_year">
                                        <option value="0" selected="selected" disabled>
                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.year.select_value"/>
                                        </option>
                                        <fmt:message bundle="${config}" key="user_registration_page.year_low_limit" var="min_year"/>
                                        <fmt:message bundle="${config}" key="user_registration_page.year_top_limit" var="max_year"/>
                                        <c:forEach var="year" begin="${min_year}" end="${max_year}">
                                            <option value="${year}">${year}</option>
                                        </c:forEach>
                                    </select>
                                </fieldset>
                                <span class="errormsg" id="error_0_birthday"></span>
                            </div>
                            <div class="form_element gender">
                                <label>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.gender.label"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </label>
                                <select class="gender" name="gender">
                                    <option value="0" selected="selected" disabled>
                                        <fmt:message bundle="${loc}" key="user_registration.form.gender.text"/>
                                    </option>
                                    <option value="1">
                                        <fmt:message bundle="${loc}" key="user_registration.form.gender.male"/>
                                    </option>
                                    <option value="2">
                                        <fmt:message bundle="${loc}" key="user_registration.form.gender.female"/>
                                    </option>
                                </select>
                                <span class="errormsg" id="error_0_gender"></span>
                            </div>

                            <div class="form_element key_word">
                                <label>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.kew_word.label"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </label>
                                <select class="gender" name="key_word">
                                    <option value="0" selected="selected" disabled>
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_ch"/>
                                    </option>
                                    <option value="1">
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_1"/>
                                    </option>
                                    <option value="2">
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_2"/>
                                    </option>
                                    <option value="3">
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_3"/>
                                    </option>
                                    <option value="4">
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_4"/>
                                    </option>
                                </select>
                                <span class="errormsg" id="error_0_keyWord"></span>
                            </div>

                            <div class="form_element key_word_value">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.key_word_value.placeholder" var="key_word_value_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word_value.legend"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="text" value name="key_word_value" id="key_word_value" class="" placeholder="${key_word_value_ph}">
                                </label>
                                <span  class="errormsg" id="error_0_keyWordValue"></span>
                            </div>

                            <div class="form_element country">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.country.placeholder" var="country_ph"/>
                                    <strong><fmt:message bundle="${loc}" key="user_registration.form.country.label"/></strong>
                                    <input type="text" value name="country" id="country" class="" placeholder="${country_ph}">
                                </label>
                                <span class="errormsg" id="error_0_country"></span>
                            </div>
                            <div class="form_element city">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.city.placeholder" var="city_ph"/>
                                    <strong><fmt:message bundle="${loc}" key="user_registration.form.city.label"/></strong>
                                    <input type="text" value name="city" id="city" class="" placeholder="${city_ph}">
                                </label>
                                <span class="errormsg" id="error_0_city"></span>
                            </div>
                            <div class="form_element page_status">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.status.placeholder" var="status_ph"/>
                                    <strong><fmt:message bundle="${loc}" key="user_registration.form.status.label"/></strong>
                                    <input type="text" value name="page_status" id="page_status" class="" placeholder="${status_ph}">
                                </label>
                            </div>
                            <div class="form_element submit_button">
                                <fmt:message bundle="${loc}" key="user_registration.form.submit_value" var="submit_v"/>
                                <span class="errormsg"><fmt:message bundle="${loc}" key="user_registration.form.msg_oblig"/></span>
                                <input type="submit" value="${submit_v}" name="submit" class="reg_button">
                            </div>
                        </form>
                    </div>
                    <div class="sample_block page_block">
                        <div>
                            <h3>
                                <fmt:message bundle="${loc}" key="user_registartion.txt.sample_header"/>
                            </h3>
                        </div>
                        <fmt:message bundle="${config}" key="path.img.user_registration.sample_img" var="sample_img"/>
                        <img class="sample_img" src="${sample_img}" alt="page_sample">
                    </div>
                </div>
            </div>
        </section>
        <c:import url="template/footer.jsp"/>
    </div>
</div>
<fmt:message bundle="${config}" key="path.js.user_register_validation_script" var="valid_script"/>
<script src="${valid_script}">
</script>
</body>
</html>
