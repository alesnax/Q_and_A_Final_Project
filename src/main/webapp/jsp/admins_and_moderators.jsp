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
        <fmt:message bundle="${loc}" key="admins_and_moders.title_text"/>
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
            <c:if test="${not empty sessionScope.success_profile_update_message}">
                <div class="page_block wide_block post_content success_message_block">
                    <div class="success_msg">
                        <fmt:message bundle="${loc}" key="${sessionScope.success_profile_update_message}"/>
                        <c:remove var="success_profile_update_message" scope="session"/>
                    </div>
                </div>
            </c:if>
            <div class="wall_content wide_block">
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="admins_and_moders.main_title"/>
                        </h1>
                    </div>
                </div>


                <div class="page_block wide_block post_content">
                    <div class="change_role_block">
                        <fmt:message bundle="${loc}" key="admins_and_moders.change_role_header" var="change_role_header"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.change_role_header_add" var="change_role_header_add"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.change_role_ph" var="change_role_ph"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.change_submit" var="change_submit"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.choose_role" var="choose_role"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.user_role" var="user_role"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.moderator_role" var="moderator_role"/>
                        <fmt:message bundle="${loc}" key="admins_and_moders.admin_role" var="admin_role"/>
                        <form class="" action="/Controller" method="post" name="change_user_role">
                            <h3>${change_role_header}</h3>
                            <div class="header_add">${change_role_header_add}</div>
                            <input type="hidden" name="command" value="change_user_role">
                            <input class="login_input" type="text" name="login" value="" placeholder="${change_role_ph}">

                            <select class="role_select" name="role">
                                <option selected="selected" disabled="">
                                    ${choose_role}
                                </option>
                                <option value="user">${user_role}</option>
                                <option value="moderator">${moderator_role}</option>
                                <option value="admin">${admin_role}</option>
                            </select>
                            <input class="change_role_submit" type="submit" value="${change_submit}">
                            <div class="cancel_submit_block" >
                                <fmt:message bundle="${config}" key="command.go_to_admins_and_moderators" var="go_to_admins_and_moderators"/>
                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                <a class="cancel_link" href="${go_to_admins_and_moderators}">${cancel_text}</a>
                            </div>
                        </form>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty requestScope.admins_moders.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="no_friends">
                                    <fmt:message bundle="${loc}" key="admins_and_moders.no_users"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="user" items="${requestScope.admins_moders.items}">
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
                                    <div class="cat_status">
                                        <c:out value="${user.role}"/>
                                    </div>
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
                        <c:if test="${requestScope.admins_moders.getTotalPagesCount() > 1}">
                            <fmt:message bundle="${config}" key="command.go_to_admins_and_moderators" var="go_to_admins_and_moderators"/>
                            <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                            <ul class="pagination">
                                <c:if test="${requestScope.admins_moders.getCurrentPage() > 1}">
                                    <li>
                                        <a href="${go_to_admins_and_moderators}${page_no}${requestScope.admins_moders.getCurrentPage()-1}">«</a>
                                    </li>
                                </c:if>
                                <c:forEach var="number" begin="1" end="${requestScope.admins_moders.getTotalPagesCount()}">
                                    <c:choose>
                                        <c:when test="${number eq requestScope.admins_moders.getCurrentPage()}">
                                            <li><a class="active">${number}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li>
                                                <a href="${go_to_admins_and_moderators}${page_no}${number}">${number}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${requestScope.admins_moders.getCurrentPage() < requestScope.admins_moders.getTotalPagesCount()}">
                                    <li>
                                        <a href="${go_to_admins_and_moderators}${page_no}${requestScope.admins_moders.getCurrentPage()+1}">»</a>
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
