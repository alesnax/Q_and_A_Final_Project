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
        <fmt:message bundle="${loc}" key="bans.title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/ban_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>
<fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
<fmt:message bundle="${config}" key="command.go_to_post" var="go_to_post"/>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <div class="wall_content wide_block">
                <c:if test="${not empty sessionScope.wrong_command_message}">
                    <div class="page_block wide_block post_content wrong_message_block">
                        <div class="error_msg">
                            <fmt:message bundle="${loc}" key="${sessionScope.wrong_command_message}"/>
                            <c:remove var="wrong_command_message" scope="session"/>
                        </div>
                    </div>
                </c:if>
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="bans.txt.main_title"/>
                        </h1>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty requestScope.bans.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="">
                                    <fmt:message bundle="${loc}" key="bans.txt.no_bans"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <fmt:message bundle="${loc}" key="ban.text.cause" var="cause_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.post_id" var="post_id_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.banned_by" var="banned_by_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.start" var="start_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.end" var="end_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.unban_submit" var="unban_submit"/>
                        <c:forEach var="ban" items="${requestScope.bans.items}">
                            <div class="page_block wide_block post_content">
                                <div class="cat_img">
                                    <a href="${go_to_profile}${ban.user.id}" class="cat_image">
                                        <p>
                                            <img class="cat_mini_img" src="${ban.user.avatar}" alt="some" onerror="src='/img/no_avatar.jpg'">
                                        </p>
                                    </a>
                                </div>
                                <div class="cat_description">
                                    <a href="${go_to_profile}${ban.user.id}" class="cat_title">
                                        <c:out value="${ban.user.login}"/>
                                    </a>
                                    <div class="cat_content">
                                        <c:out value="${cause_text}${ban.cause}"/>
                                    </div>
                                    <a href="${go_to_post}${ban.postId}&back_page=bans" class="cat_title">
                                        <c:out value="${post_id_text}"/>
                                    </a>
                                    <div class="fl_r">
                                        <span class="">${banned_by_text}</span>
                                        <a href="${go_to_profile}${ban.moderator.id}" class="cat_moderator">
                                            <c:out value="${ban.moderator.login}"/>
                                        </a>
                                    </div>
                                </div>
                                <div class="right_info">
                                    <div class="cat_status">
                                        <span class="rel_date ">
                                             ${start_text}
                                        <fmt:formatDate value="${ban.start}" type="both" dateStyle="medium" timeStyle="short"/><br/>
                                        </span>
                                    </div>
                                    <div class="cat_status">
                                        <span class="rel_date ">
                                           ${end_text}
                                        <fmt:formatDate value="${ban.end}" type="both" dateStyle="medium" timeStyle="short"/><br/>
                                    </span>
                                    </div>
                                    <div class="q_counter">
                                        <form class="unban_form" action="/Controller" method="post" name="stop_user_ban">
                                            <input type="hidden" name="command" value="stop_user_ban">
                                            <input type="hidden" name="ban_id" value="${ban.id}">
                                            <input type="hidden" name="moderator_user_id" value="${ban.moderator.id}">
                                            <input type="submit" value="${unban_submit}" class="unban_submit">
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${requestScope.bans.getTotalPagesCount() > 1}">
                            <fmt:message bundle="${config}" key="command.go_to_banned_users" var="go_to_banned_users"/>
                            <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                            <ul class="pagination">
                                <c:if test="${requestScope.bans.getCurrentPage() > 1}">
                                    <li>
                                        <a href="${go_to_banned_users}${page_no}${requestScope.bans.getCurrentPage()-1}">«</a>
                                    </li>
                                </c:if>
                                <c:forEach var="number" begin="1" end="${requestScope.bans.getTotalPagesCount()}">
                                    <c:choose>
                                        <c:when test="${number eq requestScope.bans.getCurrentPage()}">
                                            <li><a class="active">${number}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li>
                                                <a href="${go_to_banned_users}${page_no}${number}">${number}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${requestScope.bans.getCurrentPage() < requestScope.bans.getTotalPagesCount()}">
                                    <li>
                                        <a href="${go_to_banned_users}${page_no}${requestScope.bans.getCurrentPage()+1}">»</a>
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