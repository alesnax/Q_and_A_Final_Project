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
        <fmt:message bundle="${loc}" key="best_users.title_text"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/best_users_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
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
                            <fmt:message bundle="${loc}" key="best_users.main_title"/>
                        </h1>
                    </div>
                </div>
                <c:forEach var="best_user" items="${requestScope.best_users.items}" varStatus="status">
                    <div class="page_block wide_block post_content">
                        <div class="user_img">
                            <a href="${go_to_profile}${best_user.id}" class="user_image">
                                <p>
                                    <img class="mini_img" src="${best_user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                                </p>
                            </a>
                        </div>
                        <div class="friend_description">
                            <a href="${go_to_profile}${best_user.id}" class="login_title">
                                <c:out value="${best_user.name} ${best_user.surname} (${best_user.login})"/>
                            </a>
                            <div class="f_status_msg_block">
                                <c:out value="${best_user.userStatus}"/>
                            </div>
                        </div>
                        <div class="right_info">
                            <div class="position">
                                ${status.count + (requestScope.best_users.getCurrentPage() - 1) * requestScope.best_users.itemsPerPage}
                            </div>
                            <div class="q_counter">
                                <div class="count">
                                    <fmt:message bundle="${loc}" key="friends.user_rate_title" var="user_rate_title"/>
                                    <span title="${user_rate_title}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${best_user.userRate}"/></span>
                                    <span class="star-full" ></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${requestScope.best_users.getTotalPagesCount() > 1}">
                    <fmt:message bundle="${config}" key="command.find_best_users" var="find_best_users"/>
                    <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                    <ul class="pagination">
                        <c:if test="${requestScope.best_users.getCurrentPage() > 1}">
                            <li>
                                <a href="${find_best_users}${page_no}${requestScope.best_users.getCurrentPage()-1}">«</a>
                            </li>
                        </c:if>
                        <c:forEach var="number" begin="1" end="${requestScope.best_users.getTotalPagesCount()}">
                            <c:choose>
                                <c:when test="${number eq requestScope.best_users.getCurrentPage()}">
                                    <li><a class="active">${number}</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li>
                                        <a href="${find_best_users}${page_no}${number}">${number}</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${requestScope.best_users.getCurrentPage() < requestScope.best_users.getTotalPagesCount()}">
                            <li>
                                <a href="${find_best_users}${page_no}${requestScope.best_users.getCurrentPage()+1}">»</a>
                            </li>
                        </c:if>
                    </ul>
                </c:if>
            </div>
        </section>
    </div>
</div>
</body>
</html>
