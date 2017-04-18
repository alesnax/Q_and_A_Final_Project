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
        <fmt:message bundle="${loc}" key="authorization.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/user_authorization_style.css">
</head>
<body>
<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
        <fmt:message bundle="${config}" key="img.common.logo" var="main_logo"/>
        <div class="fl_l ">
            <a href="${go_to_main}">
                <img class="header_logo" src="${main_logo}" alt="Q&A logo">
            </a>
        </div>
        <c:import url="template/header_search_block.jsp"/>
        <c:import url="template/switch_language.jsp"/>
        <div class="fl_r h_links">
            <fmt:message bundle="${config}" key="command.go_to_registration_page" var="go_to_registration_page"/>
            <a class="h_link" href="${go_to_registration_page}">
                <fmt:message bundle="${loc}" key="common.sign_up_text"/>
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
                            <img class="link_logo" src="${main_logo}" alt="Q&A logo">
                        </a>
                    </div>
                    <div class="welcome_block">
                        <h1>
                            <c:choose>
                                <c:when test="${not empty sessionScope.welcome_msg}">
                                    <fmt:message bundle="${loc}" key="${sessionScope.welcome_msg}"/>
                                    <c:remove var="welcome_msg" scope="session"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:message bundle="${loc}" key="guest.user_authorization_page.welcome_simple_msg"/>
                                </c:otherwise>
                            </c:choose>
                        </h1>
                        <h2>
                            <fmt:message bundle="${loc}" key="guest.user_authorization_page.please_log"/>
                        </h2>
                    </div>
                </div>
                <c:if test="${not empty sessionScope.password_recover_success}">
                    <div class="page_block wide_block post_content success_message_block">
                        <div class="success_msg">
                            <fmt:message bundle="${loc}" key="${sessionScope.password_recover_success}"/>
                            ${sessionScope.temp_password}
                            <c:remove var="temp_password" scope="session"/>
                            <c:remove var="password_recover_success" scope="session"/>
                        </div>
                    </div>
                </c:if>
                <div class="validation_block ">
                    <div class="fl_l left_block">
                        <div class="create_account_form_block page_block">
                            <form onsubmit="return validateLoginForm('${sessionScope.locale}')" class="create_account_form" id="create_account" name="create_account" action="/Controller" method="post">
                                <input type="hidden" name="command" value="user_authorization">
                                <div class="enter_form_element name">
                                    <c:if test="${not empty sessionScope.not_registered_user_yet}">
                                        <c:forEach var="error" items="${sessionScope.not_registered_user_yet}">
                                            <span class="errormsg">
                                                    <fmt:message bundle="${loc}" key="${error}"/>
                                            </span>
                                        </c:forEach>
                                        <c:remove var="not_registered_user_yet" scope="session"/>
                                    </c:if>
                                </div>
                                <div class="enter_form_element login">
                                    <label>
                                        <strong>
                                            <fmt:message bundle="${loc}" key="guest.user_authorization_page.email_text" var="email_text"/>
                                            ${email_text}
                                        </strong>
                                        <input type="text" value="" name="email" id="email" class="" placeholder="${email_text}">
                                    </label>
                                    <span class="errormsg" id="error_0_email"></span>
                                </div>
                                <div class="enter_form_element password_form_element">
                                    <label>
                                        <fmt:message bundle="${loc}" key="guest.user_authorization_page.password_text" var="password_text"/>
                                        <input type="password" value="" name="Passwd" id="Passwd" class="" placeholder="${password_text}">
                                    </label>
                                    <span class="errormsg" id="error_0_Passwd"></span>
                                </div>
                                <div class="enter_form_element password_forgot_element">
                                    <fmt:message bundle="${config}" key="command.go_to_password_recovery" var="forgot_pass_command"/>
                                    <a class="forgot_pass" href="${forgot_pass_command}">
                                        <fmt:message bundle="${loc}" key="guest.user_authorization_page.forgot_password"/>
                                    </a>
                                </div>
                                <div class="enter_form_element submit_button">
                                    <span class="errormsg" id="error_0_enter"></span>
                                    <fmt:message bundle="${loc}" key="guest.user_authorization_page.enter_button" var="enter_button"/>
                                    <input type="submit" value="${enter_button}" name="submit" class="login_button">
                                </div>
                            </form>
                        </div>
                        <div class="or_register_block">
                            <div class="enter_form_element password_form_element">
                                <div class="register_header">
                                    <span>
                                        <fmt:message bundle="${loc}" key="guest.user_authorization_page.have_account"/>
                                    </span>
                                </div>
                                <div class="register_link_block">
                                    <a class="or_reg_link" href="${go_to_registration_page}">
                                        <fmt:message bundle="${loc}" key="guest.user_authorization_page.create_acc"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="sample_block page_block">
                        <div>
                            <h3>
                                <fmt:message bundle="${loc}" key="guest.user_authorization_page.qa_welcome_description"/>
                            </h3>
                        </div>
                        <fmt:message bundle="${config}" key="authorization.page.sample_img" var="sample_img"/>
                        <img class="sample_img" src="${sample_img}" alt="page_sample">
                    </div>
                </div>
            </div>
        </section>
        <c:import url="template/footer.jsp"/>
    </div>
</div>
<fmt:message bundle="${config}" key="path.js.user_login_validation_script" var="login_script"/>
<script src="${login_script}">
</script>
</body>
</html>
