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
</head>
<body>
<nav class="fl_l">
    <ul>

        <c:if test="${sessionScope.user.role eq 'USER' or sessionScope.user.role eq 'ADMIN' or sessionScope.user.role eq 'MODERATOR'}">
            <li>
                <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
                <a href="${go_to_main}" class="left_row">
                    <span class="icon icon_home "></span>

                    <fmt:message bundle="${loc}" key="common.left_bar.my_profile" var="my_profile"/>
                    <span class="left_label">${my_profile}</span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_reposts" var="go_to_reposts"/>
                <a href="${go_to_reposts}" class="left_row">
                    <span class="icon  icon_star-empty "></span>
                    <fmt:message bundle="${loc}" key="common.left_bar.reposts" var="reposts"/>
                    <span class="left_label">${reposts}</span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_my_news" var="go_to_my_news"/>
                <a href="${go_to_my_news}" class="left_row">
                    <span class="icon icon_file-text"></span>
                    <fmt:message bundle="${loc}" key="common.left_bar.news" var="my_news_text"/>
                    <span class="left_label">${my_news_text}</span>
                </a>
            </li>

            <li>
                <fmt:message bundle="${config}" key="command.go_to_friends" var="go_to_friends"/>
                <a href="${go_to_friends}" class="left_row">
                    <span class="icon icon_users"></span>
                    <fmt:message bundle="${loc}" key="common.left_bar.friends" var="friends_text"/>
                    <span class="left_label">${friends_text}</span>
                </a>
            </li>
            <div class="bottom_line"></div>
        </c:if>

        <li>
            <fmt:message bundle="${config}" key="command.go_to_quest_categories" var="go_to_quest_categories"/>
            <a href="${go_to_quest_categories}" class="left_row">
                <span class="icon icon_list2"></span>
                <fmt:message bundle="${loc}" key="common.left_bar.q_categories" var="q_categories"/>
                <span class="left_label">${q_categories}</span>
            </a>
        </li>
        <div class="bottom_line"></div>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_questions" var="go_to_best_questions"/>
            <a href="${go_to_best_questions}" class="left_row">
                <span class="icon icon_trophy"></span>
                <fmt:message bundle="${loc}" key="common.left_bar.best_questions" var="best_questions"/>
                <span class="left_label">${best_questions}</span>
            </a>
        </li>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_answers" var="go_to_best_answers"/>
            <a href="${go_to_best_answers}" class="left_row">
                <span class="icon icon_stats-bars2"></span>
                <fmt:message bundle="${loc}" key="common.left_bar.best_answers" var="best_answers"/>
                <span class="left_label">${best_answers}</span>
            </a>
        </li>
        <li>
            <fmt:message bundle="${config}" key="command.find_best_users" var="go_to_best_users"/>
            <a href="${go_to_best_users}" class="left_row">
                <span class="icon icon_star-full"></span>
                <fmt:message bundle="${loc}" key="common.left_bar.best_users" var="best_users"/>
                <span class="left_label">${best_users}</span>
            </a>
        </li>


    </ul>
</nav>
</body>
</html>
