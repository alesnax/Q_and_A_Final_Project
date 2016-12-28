<%--
  User: alesnax
  Date: 03.12.2016
  Time: 16:53
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>

<html>
<head>
    <meta charset="utf-8">
    <fmt:message bundle="${loc}" key="common.page_title" var="page_title"/>
    <title><c:out value="${page_title}"/></title>                                   <%--спросить про сиаут--%>
    <link rel="shortcut icon" href="/img/q_logo.png" type="image/png">
    <link rel="stylesheet" href="/css/user_registration_style.css">
    <link rel="stylesheet" href="/css/sprite.css">

</head>
<body>




<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <fmt:message bundle="${config}" key="path.page.main" var="main"/>
            <a href="../Controller?command=go_to_main_page">
                <img class="header_logo" src="/img/logo.png" alt="Q&amp;A logo"/>
            </a>
        </div>


        <c:import url="template/header_search_block.jsp" />


        <c:import url="template/switch_language.jsp" />


        <div class="fl_r h_links">
            <fmt:message bundle="${loc}" key="common.sign_in_text" var="sign_in"/>
            <a class="h_link" href="../Controller?command=go_to_authorization_page">${sign_in}</a>
        </div>
    </div>
</header>
<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp" />

        <section>

            <div class="wall_content wide_block">
                <div class="validation_header page_block ">
                    <div class="logo_block">
                        <a href="../Controller?command=go_to_main_page">
                            <img class="link_logo" src="/img/logo.png" alt="QA logo"/>
                        </a>
                    </div>
                    <div class="welcome_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="guest.user_authorization_page.welcome_simple_msg" var="welcome_msg"/>
                            <c:out value="${welcome_msg}" />
                        </h1>
                        <fmt:message bundle="${loc}" key="user_registration.h2.text.please_register" var="pl_reg"/>
                        <h2>${pl_reg}</h2>
                    </div>
                </div>
                <div class="validation_block ">
                        <fmt:message bundle="${loc}" key="user_registration.error.js.fill_field" var="fill_field_js"/>
                    <div class="create_account_form_block page_block">
                        <form onsubmit="return validateForm()" class="create_account_form" id="create_account"
                              name="create_account" action="../Controller" method="post">
                            <input type="hidden" name="command" value="register_new_user"/>
                            <div class="form_element name">
                                <c:if test="${not empty user_validation_error}">
                                    <c:forEach var="error" items="${user_validation_error}">
                                        <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                        <span class="errormsg">
                                                ${msg}
                                        </span>
                                    </c:forEach>
                                    <c:remove var="user_validation_error" />
                                </c:if>
                            </div>

                            <div class="form_element name">
                                <fieldset>
                                    <legend>
                                        <fmt:message bundle="${loc}" key="user_registration.form.name.legend" var="name_leg"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.first_name.placeholder" var="fname_ph"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.last_name.placeholder" var="lname_ph"/>
                                        <strong>${name_leg}
                                            <span class="notice_star">*</span>
                                        </strong>
                                    </legend>
                                    <input type="text" value name="FirstName" id="FirstName" class=""
                                           placeholder="${fname_ph}">
                                    <span class="errormsg" id="error_0_FirstName"></span>
                                    <input type="text" value name="LastName" id="LastName" class=""
                                           placeholder="${lname_ph}">
                                    <span class="errormsg" id="error_0_LastName"></span>
                                </fieldset>
                            </div>

                            <div class="form_element login">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.login.legend" var="login_label"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.login.placeholder" var="login_ph"/>
                                    <strong>${login_label}
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="text" value name="login" id="login" class="" placeholder="${login_ph}">
                                </label>
                                <span class="errormsg" id="error_0_login"></span>
                            </div>


                            <div class="form_element password_form_element">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.pass1.label" var="p1_lab"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.pass1.placeholder" var="p1_ph"/>
                                    <strong>${p1_lab}
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="password" value name="Passwd" id="Passwd" class="" placeholder="${p1_ph}">
                                </label>
                                <span class="errormsg" id="error_0_Passwd"></span>
                            </div>
                            <div class="form_element password_form_element">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.pass2.label" var="p2_lab"/>
                                    <strong>${p2_lab}
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="password" value name="PasswdAgain" id="PasswdAgain" class="" placeholder="${p1_ph}">
                                </label>
                                <span class="errormsg" id="error_0_PasswdAgain"></span>
                            </div>
                            <div class="form_element login">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.email.label" var="email_lab"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.email.placeholder" var="email_ph"/>
                                    <strong>${email_lab}
                                        <span class="notice_star">*</span>
                                    </strong>
                                    <input type="text" value name="email" id="email" class=""
                                           placeholder="${email_ph}">
                                </label>
                                <span class="errormsg" id="error_0_email"></span>
                            </div>
                            <div class="form_element birtday">
                                <fieldset>
                                    <legend>
                                        <fmt:message bundle="${loc}" key="user_registration.form.birthday.label" var="birth_lab"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.birthday.day.select_value" var="day_v"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.select_value" var="month_v"/>
                                        <fmt:message bundle="${loc}" key="user_registration.form.birthday.year.select_value" var="year_v"/>
                                        <strong>${birth_lab}
                                            <span class="notice_star">*</span>
                                        </strong>
                                    </legend>
                                    <select class="birth_day" name="birth_day">
                                        <option value="0" selected="selected" disabled>${day_v}</option>
                                        <c:forEach var="day" begin="1" end="31">
                                            <option value="${day}">${day}</option>
                                        </c:forEach>
                                    </select>
                                    <select class="birth_month" name="birth_month">
                                        <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                        <option value="0" selected="selected" disabled>${month_v}</option>
                                        <c:forEach var="m_numb" begin="1" end="12">
                                            <fmt:message bundle="${loc}" key="user_registration.form.birthday.month.value${m_numb}" var="m_val"/>
                                            <option value="${m_numb}">${m_val}</option>
                                        </c:forEach>


                                    </select>
                                    <select class="birth_year" name="birth_year">
                                        <option value="0" selected="selected" disabled>${year_v}</option>
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
                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.label" var="gender_lab"/>

                                    <strong>${gender_lab}
                                        <span class="notice_star">*</span>
                                    </strong>
                                </label>
                                <select class="gender" name="gender">
                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.text" var="gender_t"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.male" var="gender_m"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.gender.female" var="gender_f"/>


                                    <option value="0" selected="selected" disabled>${gender_t}</option>
                                    <option value="1">${gender_m}</option>
                                    <option value="2">${gender_f}</option>
                                </select>
                                <span class="errormsg" id="error_0_gender"></span>
                            </div>
                            <div class="form_element country">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.country.label" var="country_lab"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.country.placeholder" var="country_ph"/>
                                    <strong>${country_lab}</strong>
                                    <input type="text" value name="country" id="country" class="" placeholder="${country_ph}">
                                </label>
                                <span class="errormsg" id="error_0_country"></span>
                            </div>
                            <div class="form_element city">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.city.label" var="city_lab"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.city.placeholder" var="city_ph"/>
                                    <strong>${city_lab}</strong>
                                    <input type="text" value name="city" id="city" class="" placeholder="${city_ph}">
                                </label>
                                <span class="errormsg" id="error_0_city"></span>
                            </div>
                            <div class="form_element page_status">
                                <label>
                                    <fmt:message bundle="${loc}" key="user_registration.form.status.label" var="status_lab"/>
                                    <fmt:message bundle="${loc}" key="user_registration.form.status.placeholder" var="status_ph"/>

                                    <strong>${status_lab}</strong>
                                    <input type="text" value name="page_status" id="page_status" class=""
                                           placeholder="${status_ph}">
                                </label>
                            </div>
                            <div class="form_element submit_button">
                                <fmt:message bundle="${loc}" key="user_registration.form.msg_oblig" var="msg_oblig"/>
                                <fmt:message bundle="${loc}" key="user_registration.form.submit_value" var="submit_v"/>
                                <span class="errormsg">${msg_oblig}</span>
                                <input type="submit" value="${submit_v}" name="submit" class="reg_button">
                            </div>
                        </form>
                    </div>
                    <div class="sample_block page_block">
                        <div>
                            <fmt:message bundle="${loc}" key="user_registartion.txt.sample_header" var="sample_h"/>
                            <h3>${sample_h}</h3>
                        </div>
                        <fmt:message bundle="${config}" key="path.img.user_registration.sample_img" var="sample_img"/>
                        <img class="sample_img" src="${sample_img}" alt="page_sample">
                    </div>
                </div>
            </div>
        </section>
        <footer>
            <fmt:message bundle="${loc}" key="common.creator_name" var="creator_name"/>
            <fmt:message bundle="${config}" key="common.creator.email" var="creator_email"/>
            <span>&copy; ${creator_name}</span>
            <address><a href="mailto:${creator_email}">${creator_email}</a></address><%--сделать ссылку на профиль--%>
        </footer>
    </div>
</div>
<fmt:message bundle="${config}" key="path.js.user_register_validation_script" var="valid_script"/>
<script src="${valid_script}">
</script>
</body>
</html>
