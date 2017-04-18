<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <link rel="stylesheet" href="../../css/left_bar_style.css">
    <link rel="stylesheet" href="../../css/sprite.css">
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
                    <span class="icon icon_user-check"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.friends"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_followers" var="go_to_followers"/>
                <a href="${go_to_followers}" class="left_row">
                    <span class="icon icon_address-book"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.followers"/>
                    </span>
                </a>
            </li>
            <div class="bottom_line"></div>
        </c:if>
        <c:if test="${sessionScope.user.role eq 'ADMIN' or sessionScope.user.role eq 'MODERATOR'}">
            <li>
                <fmt:message bundle="${config}" key="command.go_to_complaints" var="go_to_complaints"/>
                <a href="${go_to_complaints}" class="left_row">
                    <span class="icon icon_envelop"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.complaints"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_all_users" var="go_to_all_users"/>
                <a href="${go_to_all_users}" class="left_row">
                    <span class="icon icon_users"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.all_users"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_moderated_categories" var="go_to_moderated_categories"/>
                <a href="${go_to_moderated_categories}" class="left_row">
                    <span class="icon icon_shield"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.moderated_categories"/>
                    </span>
                </a>
            </li>
            <li>
                <fmt:message bundle="${config}" key="command.go_to_banned_users" var="go_to_banned_users"/>
                <a href="${go_to_banned_users}" class="left_row">
                    <span class="icon icon_user-minus"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.banned_users"/>
                    </span>
                </a>
            </li>
            <div class="bottom_line"></div>
        </c:if>
        <c:if test="${sessionScope.user.role eq 'ADMIN'}">
            <li>
                <fmt:message bundle="${config}" key="command.go_to_admins_and_moderators" var="go_to_admins_and_moderators"/>
                <a href="${go_to_admins_and_moderators}" class="left_row">
                    <span class="icon icon_user-tie"></span>
                    <span class="left_label">
                        <fmt:message bundle="${loc}" key="common.left_bar.admins_and_moders"/>
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
