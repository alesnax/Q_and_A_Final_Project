<%--
  User: alesnax
  Date: 03.12.2016
  Time: 17:50
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
        <fmt:message bundle="${loc}" key="profile.page_title"/> ${user.login}
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/user_profile_style.css">
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

            <div class="top_block">
                <div class="page_block photo_block">
                    <div class="page_avatar">
                        <div class="photo-wrap">
                            <img class="avatar" src="${requestScope.user.avatar}" alt="no_photo" onerror="src='/img/no_avatar.jpg'">
                        </div>
                        <c:choose>
                            <c:when test="${requestScope.user.id eq sessionScope.user.id}">
                                <div class="profile_edit">
                                    <fmt:message bundle="${config}" key="command.go_to_edit_profile" var="edit_profile"/>
                                    <a class="profile_edit_act" href="${edit_profile}">
                                        <fmt:message bundle="${loc}" key="profile.edit_profile_text"/>
                                    </a>
                                </div>
                            </c:when>
                            <c:when test="${requestScope.user.friend eq true}">
                                <div class="following_block">
                                    <div class="follow">
                                        <span class="following_text">
                                            <fmt:message bundle="${loc}" key="profile.following_text"/>
                                        </span>
                                    </div>
                                </div>
                                <div class="double_block">
                                    <form action="/Controller" method="post">
                                        <input type="hidden" name="command" value="remove_following_user">
                                        <input type="hidden" name="user_id" value="${requestScope.user.id}">
                                        <button type="submit" name="remove" class="remove_submit_button">
                                            <fmt:message bundle="${loc}" key="profile.remove_following_user_title" var="unfollow_title"/>
                                            <span class="icon icon_cross_remove" title="${unfollow_title}"></span>
                                        </button>
                                    </form>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="">
                                    <form action="/Controller" method="post">
                                        <input type="hidden" name="command" value="follow_user">
                                        <input type="hidden" name="user_id" value="${requestScope.user.id}">
                                        <button class="follow_button" type="submit" name="follow">
                                            <fmt:message bundle="${loc}" key="profile.follow_text"/>
                                        </button>
                                    </form>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="page_block short_info_block">
                    <div class="profile_name">
                        <h1>${user.name} ${user.surname} (${user.login})</h1>
                    </div>
                    <div class="short_info">
                        <table>
                            <tbody>
                            <tr>
                                <td class="info_label"><fmt:message bundle="${loc}" key="profile.birthday_text"/></td>
                                <td class="info_labeled">${user.birthday} </td>
                            </tr>
                            <c:if test="${not empty user.country}">
                                <tr>
                                    <td class="info_label"><fmt:message bundle="${loc}"
                                                                        key="profile.country_text"/></td>
                                    <td class="info_labeled">${user.country}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty user.city}">
                                <tr>
                                    <td class="info_label"><fmt:message bundle="${loc}" key="profile.city_text"/></td>
                                    <td class="info_labeled">${user.city}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty user.email}">
                                <tr>
                                    <td class="info_label"><fmt:message bundle="${loc}" key="profile.email_text"/></td>
                                    <td class="info_labeled">${user.email}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty user.status}">
                                <tr>
                                    <td class="info_label"><fmt:message bundle="${loc}" key="profile.status_text"/></td>
                                    <td class="info_labeled">${user.status}</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                    <div class="counts_module">
                        <fmt:message bundle="${config}" key="command.go_to_friends" var="go_to_friends"/>
                        <div class="page_counter" >
                            <div class="count">
                                ${user.statistics.followingUsersCount}
                            </div>
                            <div class="label">
                                <fmt:message bundle="${loc}" key="profile.friends_text"/>
                            </div>
                        </div>
                        <div class="page_counter">
                            <div class="count">
                                ${user.statistics.followersCount}
                            </div>
                            <div class="label">
                                <fmt:message bundle="${loc}" key="profile.followers_text"/>
                            </div>
                        </div>
                        <div class="page_counter">
                            <div class="count">
                                    <fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" value="${user.statistics.rate}"/>
                            </div>
                            <div class="label">
                                <fmt:message bundle="${loc}" key="profile.rate_text"/>
                            </div>
                        </div>
                        <div class="page_counter">
                            <div class="count">
                                ${user.statistics.questionsCount}
                            </div>
                            <div class="label">
                                <fmt:message bundle="${loc}" key="profile.questions_text"/>
                            </div>
                        </div>
                        <div class="page_counter">
                            <div class="count">
                                ${user.statistics.answersCount}
                            </div>
                            <div class="label">
                                <fmt:message bundle="${loc}" key="profile.answers_text"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <c:if test="${requestScope.user.id eq sessionScope.user.id}">
                <c:import url="template/add_question.jsp"/>
            </c:if>
            <div class="wall_content wide_block">
                <c:import url="template/question_list.jsp"/>
                <c:if test="${requestScope.posts.getTotalPagesCount() > 1}">
                    <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                    <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                    <ul class="pagination">
                        <c:if test="${requestScope.posts.getCurrentPage() > 1}">
                            <li>
                                <a href="${go_to_profile}${user.id}${page_no}${requestScope.posts.getCurrentPage()-1}">«</a>
                            </li>
                        </c:if>
                        <c:forEach var="number" begin="1" end="${requestScope.posts.getTotalPagesCount()}">
                            <c:choose>
                                <c:when test="${number eq requestScope.posts.getCurrentPage()}">
                                    <li><a class="active">${number}</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li>
                                        <a href="${go_to_profile}${user.id}${page_no}${number}">${number}</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${requestScope.posts.getCurrentPage() < requestScope.posts.getTotalPagesCount()}">
                            <li>
                                <a href="${go_to_profile}${user.id}${page_no}${requestScope.posts.getCurrentPage()+1}">»</a>
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