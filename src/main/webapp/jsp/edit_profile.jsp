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
        <fmt:message bundle="${loc}" key="edit_profile.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/edit_profile_style.css">
</head>
<body>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <div class="wall_content wide_block">
                <c:if test="${not empty sessionScope.success_profile_update_message}">
                    <div class="page_block wide_block post_content success_message_block">
                        <div class="success_msg">
                            <fmt:message bundle="${loc}" key="${sessionScope.success_profile_update_message}"/>
                            <c:remove var="success_profile_update_message" scope="session"/>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty sessionScope.wrong_command_message}">
                    <div class="page_block wide_block post_content wrong_message_block">
                        <div class="error_msg">
                            <fmt:message bundle="${loc}" key="${sessionScope.wrong_command_message}"/>
                            <c:remove var="wrong_command_message" scope="session"/>
                        </div>
                    </div>
                </c:if>
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="edit_profile.txt.main_header"/>
                        </h1>
                    </div>
                </div>
                <%--                           photo block--%>
                <div class="top_block">
                    <div class="page_block photo_block">
                        <div class="page_avatar">
                            <div class="photo-wrap">
                                <img class="avatar" src="${sessionScope.user.avatar}" alt="no_photo" onerror="src='/img/no_avatar.jpg'">
                            </div>
                        </div>
                    </div>
                    <div class="page_block upload_avatar_block">
                        <div class="upload_avatar_header">
                            <h2>
                                <fmt:message bundle="${loc}" key="edit_profile.load_avatar_form.avatar_submit"/>
                            </h2>
                        </div>
                        <form enctype="multipart/form-data" class="load_avatar_form"
                              name="load_avatar" action="/Controller" method="post">
                            <input type="hidden" name="command" value="upload_avatar"/>
                            <div class="form_element name">
                                <div class="right_form_field">
                                    <input type="file" name="avatar">
                                </div>
                            </div>
                            <div class="form_element submit_button">
                                <fmt:message bundle="${loc}" key="edit_profile.user_data_form.submit_button" var="submit_v"/>
                                <input type="submit" value="${submit_v}" class="reg_button_load">
                            </div>
                        </form>
                    </div>
                    <div class="page_block change_lang_block">
                        <div class="upload_avatar_header">
                            <h2>
                                <fmt:message bundle="${loc}" key="edit_profile.change_lang_form.header"/>
                            </h2>
                        </div>
                        <form class="change_lang_form" name="change_language" action="/Controller" method="post">
                            <input type="hidden" name="command" value="change_user_language"/>
                            <div class="form_element name">
                                <label>
                                    <div class="right_form_field">
                                        <select class="lang" name="language">
                                            <fmt:message bundle="${loc}" key="edit_profile.change_lang.russian" var="rus"/>
                                            <fmt:message bundle="${loc}" key="edit_profile.change_lang.english" var="eng"/>
                                            <fmt:message bundle="${loc}" key="edit_profile.change_lang.none" var="no_lang"/>
                                            <c:choose>
                                                <c:when test="${not empty sessionScope.user.language and sessionScope.user.language eq 'RU'}">
                                                    <option disabled value="none">
                                                            ${no_lang}
                                                    </option>
                                                    <option selected="selected" value="ru">
                                                            ${rus}
                                                    </option>
                                                    <option value="en">
                                                            ${eng}
                                                    </option>
                                                </c:when>
                                                <c:when test="${not empty sessionScope.user.language and sessionScope.user.language eq 'EN'}">
                                                    <option disabled value="none">
                                                            ${no_lang}
                                                    </option>
                                                    <option value="ru">
                                                            ${rus}
                                                    </option>
                                                    <option selected="selected" value="en">
                                                            ${eng}
                                                    </option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option selected="selected" disabled value="none">
                                                            ${no_lang}
                                                    </option>
                                                    <option value="ru">
                                                            ${rus}
                                                    </option>
                                                    <option value="en">
                                                            ${eng}
                                                    </option>
                                                </c:otherwise>
                                            </c:choose>
                                        </select>
                                    </div>
                                </label>
                            </div>
                            <div class="form_element submit_button">
                                <fmt:message bundle="${loc}" key="edit_profile.change_lang.submit_button" var="submit_change"/>
                                <input type="submit" value="${submit_change}" class="reg_button_load">
                            </div>
                        </form>
                    </div>
                </div>
                <%--        edit main user info block--%>
                <div class="create_account_form_block page_block">
                    <form onsubmit="return validateForm('${sessionScope.locale}')" class="create_account_form"
                          id="create_account" name="create_account" action="/Controller" method="post">
                        <input type="hidden" name="command" value="change_user_info"/>
                        <div class="form_element name">
                            <c:if test="${not empty sessionScope.user_validation_error}">
                                <c:forEach var="error" items="${sessionScope.user_validation_error}">
                                        <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="${error}"/>
                                        </span>
                                </c:forEach>
                                <c:remove var="user_validation_error"/>
                            </c:if>
                        </div>
                        <div class="form_element name">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.first_name.placeholder" var="fname_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.name"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.name}" name="FirstName" id="FirstName" class="" placeholder="${fname_ph}">
                                    <span class="errormsg" id="error_0_FirstName"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element name">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.last_name.placeholder" var="lname_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.surname"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.surname}" name="LastName" id="LastName" class="" placeholder="${lname_ph}">
                                    <span class="errormsg" id="error_0_LastName"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element login">
                            <label>
                                <fmt:message bundle="${loc}" key="user_registration.form.login.placeholder" var="login_ph"/>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.login"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.login}" name="login" id="login" class="" placeholder="${login_ph}">
                                    <span class="errormsg" id="error_0_login"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element email">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.email.placeholder"
                                                 var="email_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.email"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.email}" name="email" id="email" class="" placeholder="${email_ph}">
                                    <span class="errormsg" id="error_0_email"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element birthday">
                            <label>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.birthday"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <fieldset>
                                        <select class="birth_day" name="birth_day">
                                            <c:forEach var="day" begin="1" end="31">
                                                <c:choose>
                                                    <c:when test="${sessionScope.user.birthday.date eq day}">
                                                        <option selected="selected" value="${day}">${day}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${day}">${day}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                        <select class="birth_month" name="birth_month">
                                            <c:forEach var="m_numb" begin="1" end="12">
                                                <c:choose>
                                                    <c:when test="${(sessionScope.user.birthday.month + 1) eq m_numb}">
                                                        <option selected="selected" value="${m_numb}">
                                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.value${m_numb}"/>
                                                        </option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${m_numb}">
                                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.value${m_numb}"/>
                                                        </option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                        <select class="birth_year" name="birth_year">
                                            <fmt:message bundle="${config}" key="user_registration_page.year_low_limit" var="min_year"/>
                                            <fmt:message bundle="${config}" key="user_registration_page.year_top_limit" var="max_year"/>
                                            <c:forEach var="year" begin="${min_year}" end="${max_year}">
                                                <c:choose>
                                                    <c:when test="${(sessionScope.user.birthday.year + 1900) eq year}">
                                                        <option selected value="${year}">${year}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${year}">${year}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                        <span class="errormsg" id="error_0_birthday"></span>
                                    </fieldset>
                                </div>
                            </label>
                        </div>
                        <div class="form_element">
                            <label>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.gender"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field sex">
                                    <select class="gender" name="gender">
                                        <c:choose>
                                            <c:when test="${sessionScope.user.sex}">
                                                <option selected="selected" value="1">
                                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.male"/>
                                                </option>
                                                <option value="2">
                                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.female"/>
                                                </option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="1">
                                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.male"/>
                                                </option>
                                                <option selected="selected" value="2">
                                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.female"/>
                                                </option>
                                            </c:otherwise>
                                        </c:choose>
                                    </select>
                                    <span class="errormsg" id="error_0_gender"></span>
                                </div>
                            </label>
                        </div>


                        <div class="form_element key_word">
                            <label>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.kew_word.label_edit"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field sex">
                                    <select class="gender" name="key_word">
                                        <option value="0" selected="selected" disabled>
                                            <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_ch"/>
                                        </option>
                                        <c:forEach var="val" begin="1" end="4">
                                            <c:choose>
                                                <c:when test="${sessionScope.user.keyWord.getValue() eq val}">
                                                    <option selected="selected" value="${val}">
                                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_${val}"/>
                                                    </option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${val}">
                                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word.option_${val}"/>
                                                    </option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </select>
                                    <span class="errormsg" id="error_0_keyWord"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element key_word_value">
                            <label>
                                <fmt:message bundle="${loc}" key="user_registration.form.key_word_value.placeholder" var="key_word_value_ph"/>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="user_registration.form.key_word_value.legend"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.keyWordValue}" name="key_word_value" id="key_word_value" class="" placeholder="${key_word_value_ph}">
                                    <span class="errormsg" id="error_0_keyWordValue"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element country">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.country.placeholder" var="country_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.country"/>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.country}" name="country" id="country" class="" placeholder="${country_ph}">
                                    <span class="errormsg" id="error_0_country"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element city">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.city.placeholder" var="city_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.city"/>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.city}" name="city" id="city" class="" placeholder="${city_ph}">
                                    <span class="errormsg" id="error_0_city"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element page_status">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="user_registration.form.status.placeholder" var="status_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.user_data_form.page_status"/>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="text" value="${sessionScope.user.status}" name="page_status" id="page_status" class="" placeholder="${status_ph}">
                                </div>
                            </label>
                        </div>
                        <div class="form_element ">
                           <span class="errormsg">
                                <fmt:message bundle="${loc}" key="user_registration.form.msg_oblig"/>
                            </span>
                        </div>
                        <div class="form_element submit_button">
                            <fmt:message bundle="${loc}" key="edit_profile.user_data_form.submit_button" var="submit_v"/>
                            <input type="submit" value="${submit_v}" class="reg_button">
                            <div class="cancel_submit_block" >
                                <fmt:message bundle="${config}" key="command.go_to_edit_profile" var="go_to_edit_profile"/>
                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                <a class="cancel_link" href="${go_to_edit_profile}">${cancel_text}</a>
                            </div>
                        </div>
                    </form>
                </div>

                <div class="create_account_form_block page_block">
                    <form onsubmit="return validatePasswords('${sessionScope.locale}')" class="create_account_form"
                          id="change_password" name="change_password" action="/Controller" method="post">
                        <input type="hidden" name="command" value="change_password"/>
                        <div class="form_element name">
                            <c:if test="${not empty sessionScope.password_validation_error}">
                                <c:forEach var="error" items="${sessionScope.password_validation_error}">
                                        <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="${error}"/>
                                        </span>
                                </c:forEach>
                                <c:remove var="password_validation_error" scope="session"/>
                            </c:if>
                        </div>
                        <div class="form_element password_form_element">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass1.placeholder" var="p1_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass1.lab"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="password" value name="OldPasswd" id="OldPasswd" class="" placeholder="${p1_ph}">
                                    <span class="errormsg" id="error_0_OldPasswd"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element password_form_element">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass2.placeholder" var="p2_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass2.lab"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="password" value name="Passwd" id="Passwd" class="" placeholder="${p2_ph}">
                                    <span class="errormsg" id="error_0_Passwd"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element password_form_element">
                            <label>
                                <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass3.placeholder" var="p3_ph"/>
                                <div class="left_form_text">
                                    <strong>
                                        <fmt:message bundle="${loc}" key="edit_profile.change_password_form.pass3.lab"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="password" value name="PasswdAgain" id="PasswdAgain" class="" placeholder="${p3_ph}">
                                    <span class="errormsg" id="error_0_PasswdAgain"></span>
                                </div>
                            </label>
                        </div>
                        <div class="form_element submit_button">
                            <fmt:message bundle="${loc}" key="edit_profile.change_password_form.submit_button" var="submit_val"/>
                            <input type="submit" value="${submit_val}" name="submit" class="reg_button">
                            <div class="cancel_submit_block" >
                                <fmt:message bundle="${config}" key="command.go_to_edit_profile" var="go_to_edit_profile"/>
                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                <a class="cancel_link" href="${go_to_edit_profile}">${cancel_text}</a>
                            </div>
                        </div>
                    </form>
                </div>


                <div class="create_account_form_block page_block red">
                    <div class="upload_avatar_header">
                        <h2>
                            <fmt:message bundle="${loc}" key="delete_profile.change_lang_form.header"/>
                        </h2>
                    </div>
                    <form onsubmit="return validateDeletedPassword('${sessionScope.locale}')" class="create_account_form"
                           name="delete_account" action="/Controller" method="post">
                        <input type="hidden" name="command" value="delete_account"/>
                        <div class="form_element name">
                            <c:if test="${not empty sessionScope.delete_account_error}">
                                <c:forEach var="error" items="${sessionScope.delete_account_error}">
                                        <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="${error}"/>
                                        </span>
                                </c:forEach>
                                <c:remove var="delete_account_error" scope="session"/>
                            </c:if>
                        </div>
                        <div class="form_element password_form_element">
                            <label>
                                <div class="left_form_text">
                                    <fmt:message bundle="${loc}" key="edit_profile.delete_password_form.pass1.placeholder" var="pass_ph"/>
                                    <strong>
                                        <fmt:message bundle="${loc}" key="delete_profile.change_password_form.pass1.lab"/>
                                        <span class="notice_star">*</span>
                                    </strong>
                                </div>
                                <div class="right_form_field">
                                    <input type="password" value name="password" id="delPasswd" class="" placeholder="${pass_ph}">
                                    <span class="errormsg" id="error_0_delPasswd"></span>
                                </div>
                            </label>
                        </div>

                        <div class="form_element submit_button">
                            <fmt:message bundle="${loc}" key="delete_profile.change_password_form.submit_button" var="del_submit_val"/>
                            <input type="submit" value="${del_submit_val}" class="reg_button">
                            <div class="cancel_submit_block" >
                                <fmt:message bundle="${config}" key="command.go_to_edit_profile" var="go_to_edit_profile"/>
                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                <a class="cancel_link" href="${go_to_edit_profile}">${cancel_text}</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </div>
</div>
<fmt:message bundle="${config}" key="path.js.edit_passwords_validation_script" var="pass_script"/>
<fmt:message bundle="${config}" key="path.js.edit_main_info_validation_script" var="main_script"/>
<script src="${main_script}"></script>
<script src="${pass_script}"></script>
</body>
</html>