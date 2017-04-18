<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 04.01.2017
  Time: 20:22
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
        <fmt:message bundle="${loc}" key="all_users.title_text"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/friends_style.css">
</head>
</head>
<body>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <c:if test="${not empty sessionScope.wrong_command_message}">
                <div class=" wrong_message_block">
                    <div class="error_msg">
                        <fmt:message bundle="${loc}" key="${sessionScope.wrong_command_message}"/>
                        <c:remove var="wrong_command_message" scope="session"/>
                    </div>
                </div>
            </c:if>
            <div class="wall_content wide_block">
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="all_users.main_title"/>
                        </h1>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty requestScope.all_users.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="no_friends">
                                    <fmt:message bundle="${loc}" key="all_users.no_users"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="user" items="${requestScope.all_users.items}">
                            <div class="page_block wide_block post_content">
                                <div class="user_img">
                                    <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                                    <a href="${go_to_profile}${user.id}" class="user_image">
                                        <p>
                                            <img class="mini_img" src="${user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                                        </p>
                                    </a>
                                </div>
                                <div class="friend_description">
                                    <a href="${go_to_profile}${user.id}" class="login_title">
                                        <c:out value="${user.name} ${user.surname} (${user.login})"/>
                                    </a>
                                    <div class="f_status_msg_block">
                                        <c:out value="${user.userStatus}"/>
                                    </div>
                                </div>
                                <div class="right_info">
                                    <div class="q_counter">
                                        <div class="count">
                                            <fmt:message bundle="${loc}" key="friends.user_rate_title" var="user_rate_title"/>
                                            <span title="${user_rate_title}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${user.userRate}"/></span>
                                            <span class="star-full" ></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${requestScope.all_users.getTotalPagesCount() > 1}">
                            <fmt:message bundle="${config}" key="command.go_to_all_users" var="go_to_all_users"/>
                            <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                            <ul class="pagination">
                                <c:if test="${requestScope.all_users.getCurrentPage() > 1}">
                                    <li>
                                        <a href="${go_to_all_users}${page_no}${requestScope.all_users.getCurrentPage()-1}">«</a>
                                    </li>
                                </c:if>
                                <c:forEach var="number" begin="1" end="${requestScope.all_users.getTotalPagesCount()}">
                                    <c:choose>
                                        <c:when test="${number eq requestScope.all_users.getCurrentPage()}">
                                            <li><a class="active">${number}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li>
                                                <a href="${go_to_all_users}${page_no}${number}">${number}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${requestScope.all_users.getCurrentPage() < requestScope.all_users.getTotalPagesCount()}">
                                    <li>
                                        <a href="${go_to_all_users}${page_no}${requestScope.all_users.getCurrentPage()+1}">»</a>
                                    </li>
                                </c:if>
                            </ul>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </div>
</div>
</body>
</html>
