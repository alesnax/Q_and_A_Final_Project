<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 20.12.2016
  Time: 20:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/css/sprite.css">
    <link rel="stylesheet" href="/css/user_profile_style.css">
</head>
<body>


<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>




<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <a href="..${go_to_main}"><img class="header_logo" src="/img/logo.png" alt="Q&A logo"/></a>
        </div>
        <c:import url="header_search_block.jsp"/>
        <c:if test="${not empty sessionScope.user}">
            <div class="fl_r h_links">
                <a class="h_link" href="../Controller?command=log_out">log out</a>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <c:import url="switch_language.jsp"/>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_registration_page" var="go_to_registration"/>
                <fmt:message bundle="${loc}" key="common.sign_up_text" var="sign_up_text"/>
                <a class="h_link" href="${go_to_registration}">${sign_up_text}</a>
            </div>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_authorization_page" var="go_to_login"/>
                <fmt:message bundle="${loc}" key="common.sign_in_text" var="sign_in_text"/>

                <a class="h_link" href="${go_to_login}">${sign_in_text}</a>
            </div>
        </c:if>
    </div>
</header>