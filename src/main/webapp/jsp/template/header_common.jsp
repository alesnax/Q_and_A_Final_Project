<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="customtags" prefix="ales" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="../../css/header_common_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="img.common.logo" var="main_logo"/>

<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
            <a href="${go_to_main}">
                <img class="header_logo" src="${main_logo}" alt="Q&A logo"/>
            </a>
        </div>
        <fmt:message bundle="${loc}" key="common.header.search_form.placeholder" var="s_form_ph"/>
        <fmt:message bundle="${loc}" key="common.header.search_form.submit_value" var="s_submit_v"/>
        <div class="fl_l search_block">
            <form class="search_form" action="/Controller" method="get">
                <input type="hidden" name="command" value="search_posts"/>
                <input class="s_back_img search_input" name="content" value="${requestScope.search_query}" type="text" placeholder="${s_form_ph}"/>
                <input class="search_submit" type="submit" value="${s_submit_v}"/>
            </form>
        </div>
        <c:if test="${not empty sessionScope.user}">
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.log_out" var="log_out_command"/>
                <a class="h_link" href=${log_out_command}>
                    <fmt:message bundle="${loc}" key="common.log_out_text"/>
                </a>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <div class="fl_r lang_links">
                <fmt:message bundle="${config}" key="command.change_language_to_en" var="change_lang_to_en"/>
                <a class="lang_link" href="${change_lang_to_en}">
                    <span class="lang_label">
                        <fmt:message bundle="${loc}" key="common.change_en_language"/>
                    </span>
                    <span class="en_icon"></span>
                </a>
            </div>
            <div class="fl_r lang_links">
                <fmt:message bundle="${config}" key="command.change_language_to_ru" var="change_lang_to_ru"/>
                <a class="lang_link" href="${change_lang_to_ru}">
                    <span class="lang_label">
                        <fmt:message bundle="${loc}" key="common.change_ru_language"/>
                    </span>
                    <span class="ru_icon"></span>
                </a>
            </div>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_registration_page" var="go_to_registration"/>
                <a class="h_link" href="${go_to_registration}">
                    <fmt:message bundle="${loc}" key="common.sign_up_text"/>
                </a>
            </div>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_authorization_page" var="go_to_login"/>
                <a class="h_link" href="${go_to_login}">
                    <fmt:message bundle="${loc}" key="common.sign_in_text"/>
                </a>
            </div>
        </c:if>
        <div class="fl_r h_links">
            <ales:top_user_info/>


            <%-- <c:if test="${not empty sessionScope.user}">
                    <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                    <a href="${go_to_profile}${sessionScope.user.id}" class="header_avatar_image">
                        <span class="header_login">${sessionScope.user.login}</span>
                        <img class="mini_header_avatar_img" src="${sessionScope.user.avatar}" alt="avatar">
                    </a>
                </c:if>--%>
        </div>
    </div>
</header>