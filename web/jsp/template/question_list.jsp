<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 18.12.2016
  Time: 10:58
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
    <link rel="stylesheet" href="/css/question_list_style.css">
</head>
<body>

<fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
<fmt:message bundle="${config}" key="command.go_to_question" var="go_to_q"/>
<fmt:message bundle="${config}" key="command.show_question_marks" var="show_question_marks"/>
<fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>


<fmt:message bundle="${loc}" key="common.post.show_quest_marks_title" var="show_quest_marks_title"/>
<fmt:message bundle="${loc}" key="common.post.correction.title_text" var="correction_title"/>
<fmt:message bundle="${loc}" key="common.post.deleting.title_text" var="deleting_title"/>
<fmt:message bundle="${loc}" key="common.post.complaint.title_text" var="complaint_title"/>
<fmt:message bundle="${loc}" key="common.rate_post.submit_text" var="submit_text"/>
<fmt:message bundle="${loc}" key="category.txt.category_span" var="category_span"/>
<fmt:message bundle="${loc}" key="common.post.show_answers_text" var="show_answers_text"/>

<div id="question_block"></div>
<c:forEach var="post" items="${requestScope.questions}">
    <div class="page_block wide_block post_content">
        <div class="post_header">
            <div class="post_header_left">
                <a href="${go_to_profile}${post.user.id}" class="post_image">
                    <img class="mini_img" src="${post.user.avatar}" alt="avatar">
                </a>
                <div class="post_header_info">
                    <h5 class="post_author">
                        <a class="user" href="${go_to_profile}${post.user.id}">
                            <span>
                                    ${post.user.login}
                            </span>
                        </a>
                    </h5>
                    <div class="post_date">
                        <span class="rel_date apost_date">
                            <fmt:formatDate value="${post.publishedTime}" type="both" dateStyle="long" timeStyle="medium"/><br/>
                        </span>
                    </div>
                </div>
            </div>
            <div class="post_header_right">
                <div>
                    <c:if test="${sessionScope.user.id ne post.user.id}">
                        <form action="/Controller" method="post">
                            <input type="hidden" name="command" value="go_to_post_complaint"/>
                            <input type="hidden" name="post_id" value="${post.id}"/>
                            <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                            <button type="submit" class="delete">
                                <span class="icon icon_pacman" title="${complaint_title}"></span>
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${sessionScope.user.id eq post.user.id}">
                        <form action="/Controller" method="post">
                            <input type="hidden" name="command" value="delete_post"/>
                            <input type="hidden" name="post_id" value="${post.id}"/>
                            <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                            <button type="submit" class="correct_post">
                                <span class="icon icon_cross" title="${deleting_title}"></span>
                            </button>
                        </form>

                        <form action="/Controller" method="post">
                            <input type="hidden" name="command" value="go_to_post_correction"/>
                            <input type="hidden" name="post_id" value="${post.id}"/>
                            <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                            <button type="submit" class="correct_post">
                                <span class="icon icon_pencil" title="${correction_title}"></span>
                            </button>
                        </form>
                    </c:if>
                </div>
                <div class="post_category">
                    <span>
                        ${category_span}
                    </span>
                    <a href="${go_to_category}${post.categoryInfo.id}"
                       title="go to all category questions">
                        <c:choose>
                            <c:when test="${sessionScope.locale eq 'ru'}">
                                ${post.categoryInfo.titleRu}
                            </c:when>
                            <c:otherwise>
                                ${post.categoryInfo.titleEn}
                            </c:otherwise>
                        </c:choose>
                    </a>
                </div>
            </div>
        </div>
        <div class="post_inner_content">
            <div class="wall_text">
                <a class="post_q_title" href="${go_to_q}${post.id}">
                    <span>${post.title}</span>
                </a>
                <div class="post_description">
                    <span>
                            ${post.content}
                    </span>
                </div>
            </div>
            <div class="post_footer">
                <div>

                    <c:forEach begin="1" end="10" varStatus="status">
                        <c:choose>
                            <c:when test="${not empty post.currentUserMark and post.currentUserMark eq status.count }">
                                <span class="icon star-full" data-descr="your current mark"></span>
                            </c:when>
                            <c:when test="${post.averageMark >= status.count}">
                                <span class="icon icon_star-full"></span>
                            </c:when>
                            <c:when test="${post.averageMark < status.count - 0.7 }">
                                <span class="icon icon_star-empty"></span>
                            </c:when>
                            <c:otherwise>
                                <span class="icon icon_star-half"></span>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>


                    <a href="${show_question_marks}${post.id}" title="${show_quest_marks_title}">
                    <span class="rate_number">
                        <fmt:formatNumber type="number" maxFractionDigits="1" value="${post.averageMark}"/>
                    </span>
                    </a>

                </div>
                <div>
                    <form class="rate_form" action="/Controller" method="post">
                        <input type="hidden" name="command" value="rate_post"/>
                        <input type="hidden" name="post_id" value="${post.id}"/>
                        <select class="rate_select" name="mark">
                            <option disabled selected="selected" value="0"> -</option>
                            <c:forEach begin="1" end="10" varStatus="status">
                                <option value="${status.count}">${status.count}</option>
                            </c:forEach>
                        </select>
                        <input class="rate_submit" type="submit" name="rate" value="${submit_text}"/>
                    </form>
                </div>

            </div>
        </div>
        <div class="answers_block">
            <div class="answers_header">
                <a class="answers_title" href="${go_to_q}${post.id}">
                    <span>
                        ${show_answers_text}
                    </span>
                </a>
            </div>
        </div>
    </div>
</c:forEach>


</body>
</html>
