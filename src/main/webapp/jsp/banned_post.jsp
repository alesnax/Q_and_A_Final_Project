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
        <fmt:message bundle="${loc}" key="banned_post.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/category_style.css">
    <link rel="stylesheet" href="../css/question_list_style.css">
</head>
<body>

<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>

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

            <c:if test="${not empty requestScope.post}">
                <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                <fmt:message bundle="${config}" key="command.go_to_question" var="go_to_q"/>
                <fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>
                <fmt:message bundle="${loc}" key="common.post.correction.title_text" var="correction_title"/>
                <fmt:message bundle="${loc}" key="common.post.deleting.title_text" var="deleting_title"/>
                <fmt:message bundle="${loc}" key="common.post.correct_answer_ph" var="correct_answer_ph"/>
                <fmt:message bundle="${loc}" key="common.post.correct_answer_submit" var="correct_answer_submit"/>
                <fmt:message bundle="${loc}" key="common.post.go_to_cat_title" var="go_to_cat_title"/>
                <fmt:message bundle="${loc}" key="common.post.correct_question_submit" var="correct_question_submit"/>

                <c:choose>
                    <c:when test="${requestScope.post.type eq 'ANSWER'}">
                        <div class="page_block wide_block post_content">
                            <div class="post_header">
                                <div class="post_header_left">
                                    <a class="post_q_title" href="${go_to_q}${requestScope.post.parentId}">
                                        <span>Q: ${requestScope.post.parentTitle}</span>
                                    </a>
                                </div>
                                <div class="post_header_right">
                                    <div class="post_category">
                                        <a href="${go_to_category}${requestScope.post.categoryInfo.id}"
                                           title="${go_to_cat_title}">
                                            <c:choose>
                                                <c:when test="${sessionScope.locale eq 'ru'}">
                                                    ${requestScope.post.categoryInfo.titleRu}
                                                </c:when>
                                                <c:otherwise>
                                                    ${requestScope.post.categoryInfo.titleEn}
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="answer_block">
                                <div class="post_inner_content">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.edit_post_id and sessionScope.edit_post_id eq requestScope.post.id}">
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
                                                    <input type="hidden" name="post_id" value="${requestScope.post.id}">
                                                    <c:choose>
                                                        <c:when test="${not empty sessionScope.corrected_answer_description}">
                                                            <c:set var="corrected_answer"
                                                                   value="${sessionScope.corrected_answer_description}"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="corrected_answer" value="${requestScope.post.content}"/>
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
                                                    <span>A: ${requestScope.post.content}</span>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="answer_header wide_block">
                                    <div class="post_author_left">
                                        <a href="${go_to_profile}${requestScope.post.user.id}" class="post_image">
                                            <p>
                                                <img class="mini_img" src="${requestScope.post.user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                                            </p>
                                        </a>
                                        <div class="post_header_info">
                                            <h5 class="post_author">
                                                <a class="user" href="${go_to_profile}${requestScope.post.user.id}">
                                                    <span>${requestScope.post.user.login}</span>
                                                </a>
                                            </h5>
                                            <div class="post_date">
                                <span class="rel_date ">
                                    <fmt:formatDate value="${requestScope.post.publishedTime}" type="both" dateStyle="long"
                                                    timeStyle="medium"/><br/>
                                </span>
                                            </div>
                                            <c:if test="${not empty requestScope.post.modifiedTime}">
                                                <div class="post_date">
                                        <span class="rel_date "
                                              title="<fmt:formatDate value="${requestScope.post.modifiedTime}" type="both" dateStyle="long" timeStyle="medium"/>">
                                            <fmt:message bundle="${loc}" key="common.post.edited_title"/>
                                        </span>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="post_buttons_right">
                                        <div>
                                            <form action="/Controller" method="post" class="inline">
                                                <input type="hidden" name="command" value="delete_post"/>
                                                <input type="hidden" name="post_id" value="${requestScope.post.id}"/>
                                                <input type="hidden" name="post_user_id" value="${requestScope.post.user.id}"/>
                                                <input type="hidden" name="moderator_id" value="${requestScope.post.categoryInfo.userId}"/>
                                                <button type="submit" class="correct_post">
                                                    <span class="icon icon_cross" title="${deleting_title}"></span>
                                                </button>
                                            </form>
                                            <c:if test="${requestScope.post.categoryInfo.status ne 'CLOSED'}">
                                                <form action="/Controller" method="post" class="inline">
                                                    <input type="hidden" name="command" value="go_to_post_correction"/>
                                                    <input type="hidden" name="post_id" value="${requestScope.post.id}"/>
                                                    <input type="hidden" name="post_user_id" value="${requestScope.post.user.id}"/>
                                                    <input type="hidden" name="moderator_id" value="${requestScope.post.categoryInfo.userId}"/>
                                                    <button type="submit" class="correct_post">
                                                        <span class="icon icon_pencil" title="${correction_title}"></span>
                                                    </button>
                                                </form>
                                            </c:if>
                                            <div class="fl_l">
                                                <fmt:message bundle="${loc}" key="banned_post.q_status"/>
                                                    ${requestScope.post.status}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="page_block wide_block post_content">
                            <div class="post_header">
                                <div class="post_header_left">
                                    <a href="${go_to_profile}${requestScope.post.user.id}" class="post_image">
                                        <p>
                                            <img class="mini_img" src="${requestScope.post.user.avatar}" alt="avatar" onerror="src='/img/no_avatar.jpg'">
                                        </p>
                                    </a>
                                    <div class="post_header_info">
                                        <h5 class="post_author">
                                            <a class="user" href="${go_to_profile}${requestScope.post.user.id}">
                                                <span>${requestScope.post.user.login}</span>
                                            </a>
                                        </h5>
                                        <div class="post_date">
                                <span class="rel_date apost_date">
                                    <fmt:formatDate value="${requestScope.post.publishedTime}" type="both" dateStyle="long"
                                                    timeStyle="medium"/><br/>
                                </span>
                                        </div>
                                        <c:if test="${not empty requestScope.post.modifiedTime}">
                                            <div class="post_date">
                                        <span class="rel_date "
                                              title="<fmt:formatDate value="${requestScope.post.modifiedTime}" type="both" dateStyle="long" timeStyle="medium"/>">
                                            <fmt:message bundle="${loc}" key="common.post.edited_title"/>
                                        </span>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="post_header_right">
                                    <div>
                                        <form action="/Controller" method="post" class="inline">
                                            <input type="hidden" name="command" value="delete_post"/>
                                            <input type="hidden" name="post_id" value="${requestScope.post.id}"/>
                                            <input type="hidden" name="post_user_id" value="${requestScope.post.user.id}"/>
                                            <input type="hidden" name="moderator_id" value="${requestScope.post.categoryInfo.userId}"/>
                                            <button type="submit" class="correct_post">
                                                <span class="icon icon_cross" title="${deleting_title}"></span>
                                            </button>
                                        </form>
                                            <form action="/Controller" method="post" class="inline">
                                                <input type="hidden" name="command" value="go_to_post_correction"/>
                                                <input type="hidden" name="post_id" value="${requestScope.post.id}"/>
                                                <input type="hidden" name="post_user_id" value="${requestScope.post.user.id}"/>
                                                <input type="hidden" name="moderator_id" value="${requestScope.post.categoryInfo.userId}"/>
                                                <button type="submit" class="correct_post">
                                                    <span class="icon icon_pencil" title="${correction_title}"></span>
                                                </button>
                                            </form>
                                    </div>
                                    <div class="fl_r">
                                        <fmt:message bundle="${loc}" key="banned_post.q_status"/>
                                        ${requestScope.post.status}
                                    </div>
                                    <c:if test="${ empty sessionScope.edit_post_id}">
                                        <div class="post_category_edit">
                                            <a href="${go_to_category}${requestScope.post.categoryInfo.id}" title="${go_to_cat_title}">
                                                <c:choose>
                                                    <c:when test="${sessionScope.locale eq 'ru'}">
                                                        ${requestScope.post.categoryInfo.titleRu}
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${requestScope.post.categoryInfo.titleEn}
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
                                        <c:when test="${not empty sessionScope.edit_post_id and sessionScope.edit_post_id eq requestScope.post.id}">
                                            <div class="correct_ask_form_block">
                                                <form class="correct_qq_ask_form" action="/Controller" method="post">
                                                    <input type="hidden" name="command" value="add_corrected_question"/>
                                                    <input type="hidden" name="post_id" value="${requestScope.post.id}"/>

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
                                                            <c:set var="c_post_title" value="${requestScope.post.title}"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:remove var="corrected_post_title" scope="session"/>
                                                    <input class="correct_qq_title_place" type="text" name="corrected_post_title" value="${c_post_title}" placeholder="${quest_title_ph}"/>
                                                    <c:choose>
                                                        <c:when test="${not empty sessionScope.corrected_post_category}">
                                                            <c:set var="c_post_category" value="${sessionScope.corrected_post_category}"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="c_post_category" value="${requestScope.post.categoryInfo.id}"/>
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
                                                            <c:set var="corrected_question" value="${requestScope.post.content}"/>
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
                                            <div class="post_q_title">
                                            <span>Q: ${requestScope.post.title}
                                    </span>
                                            </div>
                                            <div class="post_description">
                                                <span>${requestScope.post.content}</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <div class="return_block">
                <c:choose>
                    <c:when test="${requestScope.back_page eq 'bans'}">
                        <div class="return_to_bans_header">
                            <fmt:message bundle="${config}" key="command.go_to_banned_users" var="go_to_banned_users"/>
                            <a class="answers_title" href="${go_to_banned_users}">
                                <span><fmt:message bundle="${loc}" key="banned_post.return_to_bans.text"/></span>
                            </a>
                        </div>
                    </c:when>
                    <c:when test="${requestScope.back_page eq 'complaints'}">
                        <div class="return_to_bans_header">
                            <fmt:message bundle="${config}" key="command.go_to_complaints" var="go_to_complaints"/>
                            <a class="answers_title" href="${go_to_complaints}">
                                <span><fmt:message bundle="${loc}" key="banned_post.return_to_complaints.text"/></span>
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="return_to_bans_header">
                            <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                            <a class="answers_title" href="${go_to_profile}">
                                <span><fmt:message bundle="${loc}" key="banned_post.return_to_main.text"/></span>
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </div>
</div>
</body>
</html>