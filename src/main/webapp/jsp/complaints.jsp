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
        <fmt:message bundle="${loc}" key="complaints.title"/>
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
                            <fmt:message bundle="${loc}" key="complaints.txt.main_title"/>
                        </h1>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty requestScope.complaints.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="no_friends">
                                    <fmt:message bundle="${loc}" key="complaints.txt.no_complaints"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <fmt:message bundle="${loc}" key="complaints.text.comp_descript_text" var="comp_descript_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.post_id" var="post_id_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.banned_by" var="banned_by_text"/>
                        <fmt:message bundle="${loc}" key="complaint.text.published_time_text" var="published_time_text"/>
                        <fmt:message bundle="${loc}" key="complaint.text.processed_time_text" var="processed_time_text"/>
                        <fmt:message bundle="${loc}" key="ban.text.unban_submit" var="unban_submit"/>
                        <fmt:message bundle="${loc}" key="complaint.process.header_text" var="complaint_process_header"/>
                        <c:forEach var="complaint" items="${requestScope.complaints.items}">
                            <div class="page_block wide_block post_content">
                                <div class="cat_img">
                                    <a href="${go_to_profile}${complaint.user.id}" class="cat_image">
                                        <p>
                                            <img class="cat_mini_img" src="${complaint.user.avatar}" alt="some" onerror="src='/img/no_avatar.jpg'">
                                        </p>
                                    </a>
                                </div>
                                <div class="cat_description">
                                    <a href="${go_to_profile}${complaint.user.id}" class="cat_title">
                                        <fmt:message bundle="${loc}" key="ban.text.c_athor"/>
                                        <c:out value="${complaint.user.login}"/>
                                    </a>
                                    <div class="cat_content">
                                        <c:out value="${comp_descript_text}${complaint.description}"/>
                                    </div>
                                    <a href="${go_to_post}${complaint.postId}&back_page=complaints" class="cat_title">
                                        <c:out value="${post_id_text}"/>
                                    </a>
                                </div>
                                <div class="right_info">
                                    <div class="cat_status">
                                        <span class="rel_date ">
                                             ${published_time_text}
                                        <fmt:formatDate value="${complaint.publishedTime}" type="both" dateStyle="medium" timeStyle="short"/><br/>
                                        </span>
                                    </div>
                                    <div class="cat_status">
                                        <span class="rel_date ">
                                           ${processed_time_text}
                                        <fmt:formatDate value="${complaint.processedTime}" type="both" dateStyle="medium" timeStyle="short"/><br/>
                                    </span>
                                    </div>
                                    <c:choose>
                                        <c:when test="${complaint.status eq 'CANCELLED'}">
                                            <div class="complaint_status_cancel">${complaint.status}</div>
                                        </c:when>
                                        <c:when test="${complaint.status eq 'APPROVED'}">
                                            <div class="complaint_status_approved">${complaint.status}</div>
                                        </c:when>
                                        <c:when test="${complaint.status eq 'NEW'}">
                                            <div class="complaint_status_new">${complaint.status}</div>
                                        </c:when>
                                    </c:choose>
                                </div>
                                <c:choose>
                                    <c:when test="${complaint.status eq 'NEW' and empty sessionScope.process_post_id}">
                                        <div class="go_to_process">
                                            <fmt:message bundle="${config}" key="command.go_to_complaint_process" var="go_to_complaint_process"/>
                                            <fmt:message bundle="${config}" key="command.page_query_part" var="page_query_part"/>
                                            <a class="go_to_process_title" href="${go_to_complaint_process}${complaint.postId}&user_id=${complaint.user.id}${page_query_part}${requestScope.complaints.getCurrentPage()}">
                                                <span>${complaint_process_header}</span>
                                            </a>
                                        </div>
                                    </c:when>
                                    <c:when test="${complaint.status eq 'NEW' and
                                    (not empty sessionScope.process_post_id  and sessionScope.process_post_id eq complaint.postId and sessionScope.process_author_id eq complaint.user.id)}">
                                        <div class="wall_text">
                                            <div class="process_complaint_form_block">
                                                <form class="process_compl_form" action="/Controller" method="post">
                                                    <fmt:message bundle="${loc}" key="complaint.process.decision_ph" var="compl_decision_ph"/>
                                                    <fmt:message bundle="${loc}" key="common.post.complaint_process_submit" var="compl_proc_submit"/>
                                                    <h3>
                                                        ${complaint_process_header}
                                                    </h3>
                                                    <div class="">
                                                        <c:if test="${not empty sessionScope.complaint_validation_failed}">
                                                            <c:forEach var="error" items="${sessionScope.complaint_validation_failed}">
                                                                <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                                                <span class="errormsg">
                                                                            <c:out value="${msg}"/><br/>
                                                                        </span>
                                                            </c:forEach>
                                                            <c:remove var="complaint_validation_failed" scope="session"/>
                                                        </c:if>
                                                    </div>
                                                    <input type="hidden" name="command" value="add_complaint_decision">
                                                    <input type="hidden" name="post_id" value="${complaint.postId}">
                                                    <input type="hidden" name="author_id" value="${complaint.user.id}">
                                                    <input class="compl_description_place" type="text" name="complaint_decision" value="${sessionScope.invalidated_decision}" placeholder="${compl_decision_ph}">
                                                    <select class="compl_proc_select_place" name="status">
                                                        <option value="0"><fmt:message bundle="${loc}" key="complaint.approve_text"/></option>
                                                        <option value="1"><fmt:message bundle="${loc}" key="complaint.cancel_text"/></option>
                                                    </select>
                                                    <c:remove var="invalidated_decision" scope="session"/>
                                                    <input class="correct_q_submit" type="submit" value="${compl_proc_submit}">
                                                    <div class="cancel_submit_block" >
                                                        <fmt:message bundle="${config}" key="command.go_to_complaints" var="go_to_complaints"/>
                                                        <fmt:message bundle="${config}" key="command.page_query_part" var="page_query_part"/>
                                                        <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                                        <a class="cancel_link" href="${go_to_complaints}${page_query_part}${requestScope.complaints.getCurrentPage()}">${cancel_text}</a>
                                                    </div>
                                                </form>
                                                <c:remove var="process_post_id" scope="session" />
                                                <c:remove var="process_author_id" scope="session" />
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="complaint_description">
                                            <div class="cat_content">
                                                ${complaint.decision}
                                            </div>
                                            <c:if test="${not empty complaint.moderator}">
                                            <a href="${go_to_profile}${complaint.moderator.id}" class="cat_moderator">
                                                ${complaint.moderator.role}: ${complaint.moderator.login}
                                            </a>
                                            </c:if>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                        <c:if test="${requestScope.complaints.getTotalPagesCount() > 1}">
                            <fmt:message bundle="${config}" key="command.go_to_complaints" var="go_to_complaints"/>
                            <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                            <ul class="pagination">
                                <c:if test="${requestScope.complaints.getCurrentPage() > 1}">
                                    <li>
                                        <a href="${go_to_complaints}${page_no}${requestScope.complaints.getCurrentPage()-1}">«</a>
                                    </li>
                                </c:if>
                                <c:forEach var="number" begin="1" end="${requestScope.complaints.getTotalPagesCount()}">
                                    <c:choose>
                                        <c:when test="${number eq requestScope.complaints.getCurrentPage()}">
                                            <li><a class="active">${number}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li>
                                                <a href="${go_to_complaints}${page_no}${number}">${number}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${requestScope.complaints.getCurrentPage() < requestScope.complaints.getTotalPagesCount()}">
                                    <li>
                                        <a href="${go_to_complaints}${page_no}${requestScope.complaints.getCurrentPage()+1}">»</a>
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