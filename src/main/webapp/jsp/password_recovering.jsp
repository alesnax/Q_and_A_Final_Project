<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 17.01.2017
  Time: 13:12
  To change this template use File | Settings | File Templates.
--%>
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
        <fmt:message bundle="${loc}" key="pass_recov.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/user_authorization_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
<fmt:message bundle="${config}" key="img.common.logo" var="main_logo"/>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <div class="wall_content wide_block">

                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="pass_recov.txt.main_header"/>
                        </h1>
                    </div>
                </div>

                <div class="pass_recov_form_block page_block">
                    <form onsubmit="return validateRecoveringForm('${sessionScope.locale}')" class="pass_recov_form"
                          id="pass_recov_form" name="pass_recov_form" action="/Controller" method="post">
                        <input type="hidden" name="command" value="recover_password"/>
                        <div class="form_element name">
                            <c:if test="${not empty sessionScope.pass_recov_error}">
                                <c:forEach var="error" items="${sessionScope.pass_recov_error}">
                                        <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="${error}"/>
                                        </span>
                                </c:forEach>
                                <c:remove var="pass_recov_error"/>
                            </c:if>
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
                                    <input type="text" value="${sessionScope.user.email}" name="email" id="rec_email" class="" placeholder="${email_ph}">
                                    <span class="errormsg" id="error_0_email"></span>
                                </div>
                            </label>
                        </div>

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
                                    <input type="password" value="${sessionScope.user.keyWordValue}" name="key_word_value" id="key_word_value" class="" placeholder="${key_word_value_ph}">
                                    <span class="errormsg" id="error_0_keyWordValue"></span>
                                </div>
                            </label>
                        </div>

                        <div class="form_element submit_button">
                            <fmt:message bundle="${loc}" key="pass_recov.form.submit_button" var="submit_v"/>
                            <span class="errormsg"><fmt:message bundle="${loc}" key="account_recovering.form.msg_oblig"/></span>
                            <input type="submit" value="${submit_v}" class="recover_button">
                            <div class="cancel_submit_block" >
                                <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                <a class="cancel_link" href="${go_to_current_page}">${cancel_text}</a>
                            </div>
                        </div>
                    </form>
                </div>

            </div>
        </section>
        <c:import url="template/footer.jsp"/>
    </div>
</div>
<fmt:message bundle="${config}" key="path.js.pass_recov_validation_script" var="pass_recov_script"/>
<script src="${pass_recov_script}">
</script>
</body>
</html>
