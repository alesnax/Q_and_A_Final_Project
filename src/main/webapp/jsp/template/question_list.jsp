<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <link rel="stylesheet" href="../../css/question_list_style.css">
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
<fmt:message bundle="${loc}" key="common.rate_post.submit_text" var="rate_submit_text"/>
<fmt:message bundle="${loc}" key="common.post.show_answers_text" var="show_answers_text"/>
<fmt:message bundle="${loc}" key="common.post.your_mark_title" var="your_mark_title"/>
<fmt:message bundle="${loc}" key="common.post.correct_answer_ph" var="correct_answer_ph"/>
<fmt:message bundle="${loc}" key="common.post.correct_answer_submit" var="correct_answer_submit"/>
<fmt:message bundle="${loc}" key="common.post.go_to_cat_title" var="go_to_cat_title"/>
<fmt:message bundle="${loc}" key="common.post.correct_question_submit" var="correct_question_submit"/>
<fmt:message bundle="${loc}" key="common.post.complaint_submit" var="complaint_submit"/>
<fmt:message bundle="${loc}" key="common.post.complaint_header" var="complaint_header"/>
<fmt:message bundle="${loc}" key="common.post.complaint_ph" var="complaint_ph"/>


<c:forEach var="post" items="${requestScope.posts.items}">
    <c:choose>
        <%--        ANSWER POST TYPE BLOCK        --%>
        <c:when test="${post.type eq 'ANSWER'}">
            <div class="page_block wide_block post_content">
                <div class="post_header">
                    <div class="post_header_left">
                        <a class="post_q_title" href="${go_to_q}${post.parentId}">
                            <span>Q: ${post.parentTitle}</span>
                        </a>
                    </div>
                    <div class="post_header_right">
                        <div class="post_category">
                            <a href="${go_to_category}${post.categoryInfo.id}"
                               title="${go_to_cat_title}">
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

                <div class="answer_block">
                    <div class="post_inner_content">
                        <c:choose>
                            <%--    answer correcting block        --%>
                            <c:when test="${not empty sessionScope.edit_post_id and sessionScope.edit_post_id eq post.id}">
                                <div class="correct_answer_block">
                                    <form class="correct_answer_form" action="/Controller" method="post">
                                        <div class="">
                                            <c:if test="${not empty sessionScope.correct_answer_validation_failed}">
                                                <c:forEach var="error" items="${sessionScope.correct_answer_validation_failed}">
                                                    <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                                    <span class="errormsg"><c:out value="${msg}"/><br/></span>
                                                </c:forEach>
                                                <c:remove var="correct_answer_validation_failed" scope="session"/>
                                            </c:if>
                                        </div>
                                        <input type="hidden" name="command" value="add_corrected_answer">
                                        <input type="hidden" name="post_id" value="${post.id}">
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.corrected_answer_description}">
                                                <c:set var="corrected_answer"
                                                       value="${sessionScope.corrected_answer_description}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="corrected_answer" value="${post.content}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <textarea class="correct_q_place" rows="3" maxlength="2000" placeholder="${correct_answer_ph}" name="answer_description">${corrected_answer}</textarea>
                                        <c:remove var="corrected_answer_description" scope="session"/>
                                        <input class="correct_q_submit" type="submit" value="${correct_answer_submit}">
                                        <div class="cancel_submit_block" >
                                            <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                            <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                            <a class="cancel_link" href="${go_to_current_page}">${cancel_text}</a>
                                        </div>
                                    </form>
                                </div>
                                <c:remove var="edit_post_id" scope="session"/>
                            </c:when>
                            <c:otherwise>
                                <div class="answer_text">
                                    <div class="post_description">
                                        <span>A: ${post.content}</span>
                                    </div>
                                </div>
                                <%--        complaint creating block        --%>
                                <c:if test="${sessionScope.complaint_id eq post.id}">
                                    <div class="correct_answer_block">
                                        <form class="correct_answer_form" action="/Controller" method="post" name="add_complaint_form">
                                            <span>${complaint_header}</span>
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
                                            <input type="hidden" name="command" value="add_complaint">
                                            <input type="hidden" name="post_id" value="${post.id}">
                                            <textarea class="correct_q_place" rows="3" maxlength="2000" placeholder="${complaint_ph}" name="complaint_description">${sessionScope.corrected_complaint_description}</textarea>
                                            <c:remove var="corrected_complaint_description" scope="session"/>
                                            <input class="correct_q_submit" type="submit" value="${complaint_submit}">
                                            <div class="cancel_submit_block" >
                                                <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                                <a class="cancel_link" href="${go_to_current_page}">${cancel_text}</a>
                                            </div>
                                        </form>
                                    </div>
                                    <c:remove var="complaint_id" scope="session"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="answer_header wide_block">
                        <div class="post_author_left">
                            <a href="${go_to_profile}${post.user.id}" class="post_image">
                                <p>
                                    <img class="mini_img" src="${post.user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                                </p>
                            </a>
                            <div class="post_header_info">
                                <h5 class="post_author">
                                    <a class="user" href="${go_to_profile}${post.user.id}">
                                        <span>${post.user.login}</span>
                                    </a>
                                </h5>
                                <div class="post_date">
                                <span class="rel_date ">
                                    <fmt:formatDate value="${post.publishedTime}" type="both" dateStyle="long" timeStyle="medium"/><br/>
                                </span>
                                </div>
                                <c:if test="${not empty post.modifiedTime}">
                                    <div class="post_date">
                                        <span class="rel_date " title="<fmt:formatDate value="${post.modifiedTime}" type="both" dateStyle="long" timeStyle="medium"/>">
                                            <fmt:message bundle="${loc}" key="common.post.edited_title"/>
                                        </span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="rate_block">
                            <div>
                                <c:forEach begin="1" end="10" varStatus="status">
                                    <c:choose>
                                        <c:when test="${not empty post.currentUserMark and post.currentUserMark eq status.count }">
                                            <span class="star-full" data-descr="${your_mark_title}"></span>
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
                                <div title="${show_quest_marks_title}">
                                    <span class="rate_number">
                                        <fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" value="${post.averageMark}"/>
                                    </span>
                                </div>
                            </div>
                            <div>
                                <c:if test="${not empty sessionScope.user and sessionScope.user.banned eq false}">
                                    <form class="rate_form fl_r" action="/Controller" method="post">
                                        <input type="hidden" name="command" value="rate_post"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>
                                        <select class="rate_select" name="mark">
                                            <c:forEach begin="1" end="10" varStatus="status">
                                                <option value="${status.count}">${status.count}</option>
                                            </c:forEach>
                                        </select>
                                        <input class="rate_submit" type="submit" value="${rate_submit_text}"/>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                        <div class="post_buttons_right">
                            <div>
                                <c:if test="${(not empty sessionScope.user.id and sessionScope.user.id ne post.user.id and post.user.role eq 'USER') or (not empty sessionScope.complaint_id and sessionScope.complaint_id ne post.id)}">
                                    <form action="/Controller" method="post" class="inline">
                                        <input type="hidden" name="command" value="go_to_post_complaint"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>
                                        <button type="submit" class="delete">
                                            <span class="icon icon_pacman" title="${complaint_title}"></span>
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${sessionScope.user.id eq post.user.id or sessionScope.user.role eq 'ADMIN' or sessionScope.user.id eq post.categoryInfo.userId}">
                                    <form action="/Controller" method="post" class="inline">
                                        <input type="hidden" name="command" value="delete_post"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>
                                        <input type="hidden" name="moderator_id" value="${post.categoryInfo.userId}"/>
                                        <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                                        <button type="submit" class="correct_post">
                                            <span class="icon icon_cross" title="${deleting_title}"></span>
                                        </button>
                                    </form>
                                    <c:if test="${not empty sessionScope.user and not sessionScope.user.banned and post.categoryInfo.status ne 'CLOSED'}">
                                        <form action="/Controller" method="post" class="inline">
                                            <input type="hidden" name="command" value="go_to_post_correction"/>
                                            <input type="hidden" name="post_id" value="${post.id}"/>
                                            <input type="hidden" name="moderator_id" value="${post.categoryInfo.userId}"/>
                                            <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                                            <button type="submit" class="correct_post">
                                                <span class="icon icon_pencil" title="${correction_title}"></span>
                                            </button>
                                        </form>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <%--        QUESTION ans SERVICE TYPE BLOCK        --%>
        <c:otherwise>
            <div class="page_block wide_block post_content">
                <div class="post_header">
                    <div class="post_header_left">
                        <a href="${go_to_profile}${post.user.id}" class="post_image">
                            <p>
                                <img class="mini_img" src="${post.user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                            </p>
                        </a>
                        <div class="post_header_info">
                            <h5 class="post_author">
                                <a class="user" href="${go_to_profile}${post.user.id}">
                                    <span>${post.user.login}</span>
                                </a>
                            </h5>
                            <div class="post_date">
                                <span class="rel_date apost_date">
                                    <fmt:formatDate value="${post.publishedTime}" type="both" dateStyle="long" timeStyle="medium"/><br/>
                                </span>
                            </div>
                            <c:if test="${not empty post.modifiedTime}">
                                <div class="post_date">
                                        <span class="rel_date " title="<fmt:formatDate value="${post.modifiedTime}" type="both" dateStyle="long" timeStyle="medium"/>">
                                            <fmt:message bundle="${loc}" key="common.post.edited_title"/>
                                        </span>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="post_header_right">
                        <div>
                            <c:if test="${(not empty sessionScope.user.id and sessionScope.user.id ne post.user.id and post.user.role eq 'USER') or (not empty sessionScope.complaint_id and sessionScope.complaint_id ne post.id)}">
                                <form action="/Controller" method="post" class="inline">
                                    <input type="hidden" name="command" value="go_to_post_complaint"/>
                                    <input type="hidden" name="post_id" value="${post.id}"/>
                                    <button type="submit" class="delete">
                                        <span class="icon icon_pacman" title="${complaint_title}"></span>
                                    </button>
                                </form>
                            </c:if>
                            <c:if test="${sessionScope.user.id eq post.user.id or sessionScope.user.role eq 'ADMIN' or sessionScope.user.id eq post.categoryInfo.userId}">
                                <form action="/Controller" method="post" class="inline">
                                    <input type="hidden" name="command" value="delete_post"/>
                                    <input type="hidden" name="post_id" value="${post.id}"/>
                                    <input type="hidden" name="moderator_id" value="${post.categoryInfo.userId}"/>
                                    <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                                    <button type="submit" class="correct_post">
                                        <span class="icon icon_cross" title="${deleting_title}"></span>
                                    </button>
                                </form>
                                <c:if test="${not empty sessionScope.user and not sessionScope.user.banned}">
                                    <form action="/Controller" method="post" class="inline">
                                        <input type="hidden" name="command" value="go_to_post_correction"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>
                                        <input type="hidden" name="moderator_id" value="${post.categoryInfo.userId}"/>
                                        <input type="hidden" name="post_user_id" value="${post.user.id}"/>
                                        <button type="submit" class="correct_post">
                                            <span class="icon icon_pencil" title="${correction_title}"></span>
                                        </button>
                                    </form>
                                </c:if>
                            </c:if>
                        </div>
                        <c:if test="${empty sessionScope.edit_post_id}">
                            <div class="post_category">
                                <a href="${go_to_category}${post.categoryInfo.id}" title="${go_to_cat_title}">
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
                        </c:if>
                    </div>
                </div>
                <div class="post_inner_content">
                    <div class="wall_text">
                        <c:choose>
                            <%--        Question and Service Correcting block        --%>
                            <c:when test="${not empty sessionScope.edit_post_id and sessionScope.edit_post_id eq post.id}">
                                <div class="correct_ask_form_block">
                                    <form class="correct_qq_ask_form" action="/Controller" method="post">
                                        <input type="hidden" name="command" value="add_corrected_question"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>

                                        <div class="">
                                            <c:if test="${not empty sessionScope.correct_question_validation_failed}">
                                                <c:forEach var="error"
                                                           items="${sessionScope.correct_question_validation_failed}">
                                                    <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                                    <span class="errormsg">${msg}<br/></span>
                                                </c:forEach>
                                                <c:remove var="correct_question_validation_failed" scope="session"/>
                                            </c:if>
                                        </div>
                                        <fmt:message bundle="${loc}" key="common.ask_form_block.input_placeholder" var="quest_title_ph"/>
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.corrected_post_title}">
                                                <c:set var="c_post_title" value="${sessionScope.corrected_post_title}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="c_post_title" value="${post.title}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:remove var="corrected_post_title" scope="session"/>
                                        <input class="correct_qq_title_place" type="text" name="corrected_post_title" value="${c_post_title}" placeholder="${quest_title_ph}"/>
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.corrected_post_category}">
                                                <c:set var="c_post_category" value="${sessionScope.corrected_post_category}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="c_post_category" value="${post.categoryInfo.id}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:remove var="corrected_post_category" scope="session"/>
                                        <select class="correct_qq_select_place" name="corrected_post_category">
                                            <option selected="selected" disabled>
                                                <fmt:message bundle="${loc}" key="common.ask_form_block.select_category"/>
                                            </option>
                                            <c:forEach var="cat" items="${sessionScope.categories_info}">
                                                <c:choose>
                                                    <c:when test="${sessionScope.locale eq 'ru'}">
                                                        <c:choose>
                                                            <c:when test="${cat.id eq c_post_category}">
                                                                <option selected="selected" value="${cat.id}">${cat.titleRu}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${cat.id}">${cat.titleRu}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${cat.id eq c_post_category}">
                                                                <option selected="selected" value="${cat.id}">${cat.titleEn}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${cat.id}">${cat.titleEn}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.corrected_question_description}">
                                                <c:set var="corrected_question" value="${sessionScope.corrected_question_description}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="corrected_question" value="${post.content}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:remove var="corrected_question_description" scope="session"/>
                                        <fmt:message bundle="${loc}" key="common.ask_form_block.textarea_placeholder" var="textarea_placeholder"/>
                                        <textarea class="correct_qq_form_place" name="corrected_question_description" rows="3" placeholder="${textarea_placeholder}">${corrected_question}</textarea>
                                        <input class="correct_qq_form_submit" type="submit" value="${correct_question_submit}"/>
                                        <div class="cancel_submit_block" >
                                            <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                            <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                            <a class="cancel_link" href="${go_to_current_page}">${cancel_text}</a>
                                        </div>
                                    </form>
                                    <c:remove var="edit_post_id" scope="session"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${post.type eq 'SERVICE'}">
                                    <div class="post_q_title">
                                        <span>
                                            NOTICE: ${post.title}
                                        </span>
                                    </div>
                                </c:if>
                                <c:if test="${post.type eq 'QUESTION'}">
                                    <a class="post_q_title" href="${go_to_q}${post.id}">
                                        <span>
                                            Q: ${post.title}
                                        </span>
                                    </a>
                                </c:if>

                                <div class="post_description">
                                    <span>${post.content}</span>
                                </div>

                                <%--        complaint creating block        --%>
                                <c:if test="${sessionScope.complaint_id eq post.id}">
                                    <div class="correct_answer_block">
                                        <form class="correct_answer_form" action="/Controller" method="post" name="add_complaint_form">
                                            <span>${complaint_header}</span>
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
                                            <input type="hidden" name="command" value="add_complaint">
                                            <input type="hidden" name="post_id" value="${post.id}">
                                            <textarea class="correct_q_place" rows="3" maxlength="2000" placeholder="${complaint_ph}" name="complaint_description">${sessionScope.corrected_complaint_description}</textarea>
                                            <c:remove var="corrected_complaint_description" scope="session"/>
                                            <input class="correct_q_submit" type="submit" value="${complaint_submit}">
                                            <div class="cancel_submit_block" >
                                                <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                                <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                                <a class="cancel_link" href="${go_to_current_page}">${cancel_text}</a>
                                            </div>
                                        </form>
                                    </div>
                                    <c:remove var="complaint_id" scope="session"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${post.type eq 'QUESTION'}">
                        <div class="post_footer">
                            <div class="fl_l">
                                <c:forEach begin="1" end="10" varStatus="status">
                                    <c:choose>
                                        <c:when test="${not empty post.currentUserMark and post.currentUserMark eq status.count }">
                                            <span class="star-full" data-descr="${your_mark_title}"></span>
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
                                <div class="show_mark" title="${show_quest_marks_title}">
                                    <span class="rate_numb">
                                        <fmt:formatNumber type="number" minFractionDigits="1" maxFractionDigits="1" value="${post.averageMark}"/>
                                    </span>
                                </div>
                            </div>
                            <div class="fl_l">
                                <c:if test="${not empty sessionScope.user and sessionScope.user.banned eq false}">
                                    <form class="rate_form" action="/Controller" method="post">
                                        <input type="hidden" name="command" value="rate_post"/>
                                        <input type="hidden" name="post_id" value="${post.id}"/>
                                        <select class="rate_select" name="mark">
                                            <c:forEach begin="1" end="10" varStatus="status">
                                                <option value="${status.count}">${status.count}</option>
                                            </c:forEach>
                                        </select>
                                        <input class="rate_submit" type="submit" name="rate" value="${rate_submit_text}"/>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>
                <c:if test="${post.type eq 'QUESTION'}">
                    <div class="answers_block">
                        <div class="answers_header">
                            <a class="answers_title" href="${go_to_q}${post.id}">
                                <span>${show_answers_text}</span>
                            </a>
                        </div>
                    </div>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</c:forEach>


</body>
</html>
