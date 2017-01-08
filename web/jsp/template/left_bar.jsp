<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 13.12.2016
  Time: 12:57
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
    <link rel="stylesheet" href="/css/left_bar_style.css">
    <link rel="stylesheet" href="/css/sprite.css">
</head>
<body>
<nav class="fl_l">
    <ul>

        <c:if test="${sessionScope.user.role eq 'USER' or sessionScope.user.role eq 'ADMIN' or sessionScope.user.role eq 'MODERATOR'}">
            <li>
                <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
                <a href="${go_to_main}" class="left_row">
                    <span class="icon icon_home "></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.my_profile"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_reposts" var="go_to_reposts"/>
                <a href="${go_to_reposts}" class="left_row">
                    <span class="icon  icon_star-empty "></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.reposts"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_my_news" var="go_to_my_news"/>
                <a href="${go_to_my_news}" class="left_row">
                    <span class="icon icon_file-text"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.news"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_friends" var="go_to_friends"/>
                <a href="${go_to_friends}" class="left_row">
                    <span class="icon icon_users"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.friends"/>
                    </span>
                </a>
            </li>
            <div class="bottom_line"></div>
        </c:if>
        <li>
            <fmt:message bundle="${config}" key="command.go_to_quest_categories" var="go_to_quest_categories"/>
            <a href="${go_to_quest_categories}" class="left_row">
                <span class="icon icon_list2"></span>
                <span class="left_label">
                    <fmt:message bundle="${loc}" key="common.left_bar.q_categories"/>
                </span>
            </a>
        </li>
        <div class="bottom_line"></div>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_questions" var="go_to_best_questions"/>
            <a href="${go_to_best_questions}" class="left_row">
                <span class="icon icon_trophy"></span>
                <span class="left_label">
                    <fmt:message bundle="${loc}" key="common.left_bar.best_questions"/>
                </span>
            </a>
        </li>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_answers" var="go_to_best_answers"/>
            <a href="${go_to_best_answers}" class="left_row">
                <span class="icon icon_stats-bars2"></span>
                <span class="left_label">
                    <fmt:message bundle="${loc}" key="common.left_bar.best_answers"/>
                </span>
            </a>
        </li>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_users" var="go_to_best_users"/>
            <a href="${go_to_best_users}" class="left_row">
                <span class="icon icon_star-full"></span>
                <span class="left_label">
                    <fmt:message bundle="${loc}" key="common.left_bar.best_users"/>
                </span>
            </a>
        </li>
    </ul>
</nav>
</body>
</html>
