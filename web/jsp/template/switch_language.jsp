<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 13.12.2016
  Time: 12:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>


<html>
<head>
    <link rel="stylesheet" href="/css/switch_language_style.css">
</head>
<body>

<%--<c:choose>
  <c:when test="${sessionScope.user.role eq 'USER' or sessionScope.user.role eq 'ADMIN' or sessionScope.user.role eq 'MODERATOR'}">

        <c:choose>
            <c:when test="${locale eq 'ru' }">
                <div class="fl_r lang_links">
                    <fmt:message bundle="${config}" key="command.change_language_to_en" var="change_lang_to_en"/>
                    <a class="lang_link" href="${change_lang_to_en}">
                        <fmt:message bundle="${loc}" key="common.change_en_language" var="change_en_language"/>
                        <span class="lang_label">${change_en_language}</span>
                        <span class="en_icon"></span>
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="fl_r lang_links">
                    <fmt:message bundle="${config}" key="command.change_language_to_ru" var="change_lang_to_ru"/>
                    <a class="lang_link" href="${change_lang_to_ru}">
                        <fmt:message bundle="${loc}" key="common.change_ru_language" var="change_ru_language"/>
                        <span class="lang_label">${change_ru_language}</span>
                        <span class="ru_icon"></span>
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>--%>
<div class="fl_r lang_links">
    <fmt:message bundle="${config}" key="command.change_language_to_en" var="change_lang_to_en"/>
    <a class="lang_link" href="${change_lang_to_en}">
        <fmt:message bundle="${loc}" key="common.change_en_language" var="change_en_language"/>
        <span class="lang_label">${change_en_language}</span>
        <span class="en_icon"></span>
    </a>
</div>
<div class="fl_r lang_links">
    <fmt:message bundle="${config}" key="command.change_language_to_ru" var="change_lang_to_ru"/>
    <a class="lang_link" href="${change_lang_to_ru}">
        <fmt:message bundle="${loc}" key="common.change_ru_language" var="change_ru_language"/>
        <span class="lang_label">${change_ru_language}</span>
        <span class="ru_icon"></span>
    </a>
</div>
<%--   </c:otherwise>
</c:choose>
--%>
</body>
</html>