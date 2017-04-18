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
        <fmt:message bundle="${loc}" key="moderated_categories.title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/categories_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>
<fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
<fmt:message bundle="${config}" key="command.go_to_moderated_categories" var="go_to_categories"/>
<fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <div class="wall_content wide_block">
                <c:if test="${not empty sessionScope.success_category_create_message}">
                    <div class="page_block wide_block post_content success_message_block">
                        <div class="success_created_msg">
                            <fmt:message bundle="${loc}" key="${sessionScope.success_category_create_message}"/>
                            <c:remove var="success_category_create_message" scope="session"/>
                        </div>
                    </div>
                </c:if>
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
                            <fmt:message bundle="${loc}" key="moderated_categories.txt.moderated_categories"/>
                        </h1>
                    </div>
                </div>

                <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                    <c:choose>
                        <c:when test="${not empty sessionScope.show_category_creation and sessionScope.show_category_creation eq true}">
                            <div class="create_category_form_block page_block">
                                <div class="create_category_header">
                                    <h2>
                                        <fmt:message bundle="${loc}" key="create_category_form.main_header"/>
                                    </h2>
                                </div>
                                <form class="create_category_form"
                                      id="create_category" name="create_category" action="/Controller" method="post">
                                    <input type="hidden" name="command" value="create_new_category"/>
                                    <input type="hidden" name="page_no" value="${requestScope.full_categories.getCurrentPage()}"/>
                                    <div class="form_element ">
                                        <c:if test="${not empty sessionScope.category_validation_error}">
                                            <c:forEach var="error" items="${sessionScope.category_validation_error}">
                                                <span class="errormsg">
                                                    <fmt:message bundle="${loc}" key="${error}"/>
                                                </span>
                                            </c:forEach>
                                            <c:remove var="category_validation_error"/>
                                        </c:if>
                                    </div>


                                    <div class="form_element title_en">
                                        <label>
                                            <div class="left_form_text">
                                                <fmt:message bundle="${loc}" key="create_category_form.title_en.ph" var="title_en_ph"/>
                                                <strong>
                                                    <fmt:message bundle="${loc}" key="create_category_form.title_en.lab"/>
                                                    <span class="notice_star">*</span>
                                                </strong>
                                            </div>
                                            <div class="right_form_field">
                                                <input type="text" value="${sessionScope.created_title_en}" name="title_en" id="title_en" class="" placeholder="${title_en_ph}">
                                                <span class="errormsg" id="error_0_title_en"></span>
                                                <c:remove var="created_title_en" scope="session"/>
                                            </div>
                                        </label>
                                    </div>

                                    <div class="form_element title_ru">
                                        <label>
                                            <div class="left_form_text">
                                                <fmt:message bundle="${loc}" key="create_category_form.title_ru.ph" var="title_ru_ph"/>
                                                <strong>
                                                    <fmt:message bundle="${loc}" key="create_category_form.title_ru.lab"/>
                                                    <span class="notice_star">*</span>
                                                </strong>
                                            </div>
                                            <div class="right_form_field">
                                                <input type="text" value="${sessionScope.created_title_ru}" name="title_ru" id="title_ru" class="" placeholder="${title_ru_ph}">
                                                <span class="errormsg" id="error_0_title_ru"></span>
                                                <c:remove var="created_title_ru" scope="session"/>
                                            </div>
                                        </label>
                                    </div>


                                    <div class="form_element description_en">
                                        <label>
                                            <div class="left_form_text">
                                                <fmt:message bundle="${loc}" key="create_category_form.description_en.ph" var="description_en_ph"/>
                                                <strong>
                                                    <fmt:message bundle="${loc}" key="create_category_form.description_en.lab"/>
                                                    <span class="notice_star">*</span>
                                                </strong>
                                            </div>
                                            <div class="right_form_field">
                                                <input type="text" value="${sessionScope.created_description_en}" name="description_en" id="description_en" class="" placeholder="${description_en_ph}">
                                                <span class="errormsg" id="error_0_description_en"></span>
                                                <c:remove var="created_description_en" scope="session"/>
                                            </div>
                                        </label>
                                    </div>

                                    <div class="form_element description_ru">
                                        <label>
                                            <div class="left_form_text">
                                                <fmt:message bundle="${loc}" key="create_category_form.description_ru.ph" var="description_ru_ph"/>
                                                <strong>
                                                    <fmt:message bundle="${loc}" key="create_category_form.description_ru.lab"/>
                                                    <span class="notice_star">*</span>
                                                </strong>
                                            </div>
                                            <div class="right_form_field">
                                                <input type="text" value="${sessionScope.created_description_ru}" name="description_ru" id="description_ru" class="" placeholder="${description_ru_ph}">
                                                <span class="errormsg" id="error_0_description_ru"></span>
                                                <c:remove var="created_description_ru" scope="session"/>
                                            </div>
                                        </label>
                                    </div>
                                    <div class="form_element ">
                                       <span class="errormsg">
                                            <fmt:message bundle="${loc}" key="user_registration.form.msg_oblig"/>
                                        </span>
                                    </div>
                                    <div class="form_element submit_button">
                                        <fmt:message bundle="${loc}" key="create_category_form.submit_button" var="submit_v"/>
                                        <input type="submit" value="${submit_v}" class="create_button">
                                        <div class="cancel_submit_block" >
                                            <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                            <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                            <a class="cancel_link" href="${go_to_categories}${page_no}${requestScope.full_categories.getCurrentPage()}">${cancel_text}</a>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <c:remove var="show_category_creation" scope="session" />
                        </c:when>
                        <c:otherwise>
                            <div class="creation_link_block">
                                <fmt:message bundle="${config}" key="command.go_to_category_creation" var="go_to_category_creation"/>
                                <fmt:message bundle="${loc}" key="cat.link.go_to_category_creation" var="go_to_creation_text"/>
                                <div class="creation_link_header">
                                    <a class="creation_link_title" href="${go_to_category_creation}">
                                        <span>${go_to_creation_text}</span>
                                    </a>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:choose>
                    <c:when test="${empty requestScope.full_categories.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="no_friends">
                                    <fmt:message bundle="${loc}" key="moderated_categories.txt.no_categories"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <fmt:message bundle="${loc}" key="moderated_categories.close_cat_title" var="close_cat_title"/>
                        <fmt:message bundle="${loc}" key="moderated_categories.correct_cat_title" var="correct_cat_title"/>

                        <c:forEach var="cat" items="${requestScope.full_categories.items}">
                            <c:choose>
                                <c:when test="${not empty sessionScope.show_category_correction and sessionScope.show_category_correction eq cat.id}">

                                    <div class="create_category_form_block page_block">
                                        <div class="create_category_header">
                                            <h2>
                                                <fmt:message bundle="${loc}" key="correct_category_form.main_header"/>
                                            </h2>
                                        </div>
                                        <form class="create_category_form"
                                              id="correct_category" name="correct_category" action="/Controller" method="post">
                                            <input type="hidden" name="command" value="correct_category"/>
                                            <input type="hidden" name="category_id" value="${cat.id}"/>
                                            <input type="hidden" name="page_no" value="${requestScope.full_categories.getCurrentPage()}"/>
                                            <div class="form_element ">
                                                <c:if test="${not empty sessionScope.correct_category_validation_error}">
                                                    <c:forEach var="error" items="${sessionScope.correct_category_validation_error}">
                                                        <span class="errormsg">
                                                            <fmt:message bundle="${loc}" key="${error}"/>
                                                        </span>
                                                    </c:forEach>
                                                    <c:remove var="correct_category_validation_error" scope="session"/>
                                                </c:if>
                                            </div>


                                            <div class="form_element title_en">
                                                <label>
                                                    <div class="left_form_text">
                                                        <fmt:message bundle="${loc}" key="create_category_form.title_en.ph" var="title_en_ph"/>
                                                        <strong>
                                                            <fmt:message bundle="${loc}" key="create_category_form.title_en.lab"/>
                                                            <span class="notice_star">*</span>
                                                        </strong>
                                                    </div>
                                                    <div class="right_form_field">
                                                        <c:choose>
                                                            <c:when test="${not empty sessionScope.corrected_title_en}">
                                                                <c:set var="corrected_titleEn" value="${sessionScope.corrected_title_en}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="corrected_titleEn" value="${cat.titleEn}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:remove var="corrected_title_en" scope="session"/>
                                                        <input type="text" value="${corrected_titleEn}" name="corrected_title_en" id="corrected_title_en" class="" placeholder="${title_en_ph}">
                                                        <span class="errormsg" id="error_00_title_en"></span>
                                                    </div>
                                                </label>
                                            </div>

                                            <div class="form_element title_ru">
                                                <label>
                                                    <div class="left_form_text">
                                                        <fmt:message bundle="${loc}" key="create_category_form.title_ru.ph" var="title_ru_ph"/>
                                                        <strong>
                                                            <fmt:message bundle="${loc}" key="create_category_form.title_ru.lab"/>
                                                            <span class="notice_star">*</span>
                                                        </strong>
                                                    </div>
                                                    <div class="right_form_field">
                                                        <c:choose>
                                                            <c:when test="${not empty sessionScope.corrected_title_ru}">
                                                                <c:set var="corrected_titleRu" value="${sessionScope.corrected_title_ru}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="corrected_titleRu" value="${cat.titleRu}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:remove var="corrected_title_ru" scope="session"/>
                                                        <input type="text" value="${corrected_titleRu}" name="corrected_title_ru" id="corrected_title_ru" class="" placeholder="${title_ru_ph}">
                                                        <span class="errormsg" id="error_00_title_ru"></span>
                                                    </div>
                                                </label>
                                            </div>


                                            <div class="form_element description_en">
                                                <label>
                                                    <div class="left_form_text">
                                                        <fmt:message bundle="${loc}" key="create_category_form.description_en.ph" var="description_en_ph"/>
                                                        <strong>
                                                            <fmt:message bundle="${loc}" key="create_category_form.description_en.lab"/>
                                                            <span class="notice_star">*</span>
                                                        </strong>
                                                    </div>
                                                    <div class="right_form_field">
                                                        <c:choose>
                                                            <c:when test="${not empty sessionScope.corrected_description_en}">
                                                                <c:set var="corrected_descriptionEn" value="${sessionScope.corrected_description_en}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="corrected_descriptionEn" value="${cat.descriptionEn}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:remove var="corrected_description_en" scope="session"/>
                                                        <input type="text" value="${corrected_descriptionEn}" name="corrected_description_en" id="corrected_description_en" class="" placeholder="${description_en_ph}">
                                                        <span class="errormsg" id="error_00_description_en"></span>
                                                    </div>
                                                </label>
                                            </div>

                                            <div class="form_element description_ru">
                                                <label>
                                                    <div class="left_form_text">
                                                        <fmt:message bundle="${loc}" key="create_category_form.description_ru.ph" var="description_ru_ph"/>
                                                        <strong>
                                                            <fmt:message bundle="${loc}" key="create_category_form.description_ru.lab"/>
                                                            <span class="notice_star">*</span>
                                                        </strong>
                                                    </div>
                                                    <div class="right_form_field">
                                                        <c:choose>
                                                            <c:when test="${not empty sessionScope.corrected_description_ru}">
                                                                <c:set var="corrected_descriptionRu" value="${sessionScope.corrected_description_ru}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="corrected_descriptionRu" value="${cat.descriptionRu}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:remove var="corrected_description_ru" scope="session"/>
                                                        <input type="text" value="${corrected_descriptionRu}" name="corrected_description_ru" id="corrected_description_ru" class="" placeholder="${description_ru_ph}">
                                                        <span class="errormsg" id="error_00_description_ru"></span>
                                                    </div>
                                                </label>
                                            </div>
                                            <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                                                <div class="form_element moderator">
                                                    <label>
                                                        <div class="left_form_text">
                                                            <fmt:message bundle="${loc}" key="correct_category_form.moderator.ph" var="moderator_ph"/>
                                                            <strong>
                                                                <fmt:message bundle="${loc}" key="correct_category_form.moderator.lab"/>
                                                                <span class="notice_star">*</span>
                                                            </strong>
                                                        </div>
                                                        <div class="right_form_field">
                                                            <c:choose>
                                                                <c:when test="${not empty sessionScope.corrected_moderator}">
                                                                    <c:set var="corrected_moderator" value="${sessionScope.corrected_moderator}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:set var="corrected_moderator" value="${cat.moderator.login}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:remove var="corrected_moderator" scope="session"/>
                                                            <input type="text" value="${corrected_moderator}" name="corrected_moderator" id="corrected_moderator" class="" placeholder="${moderator_ph}">
                                                            <span class="errormsg" id="error_00_moderator"></span>
                                                        </div>
                                                    </label>
                                                </div>
                                            </c:if>

                                            <div class="form_element">
                                                <label>
                                                    <div class="left_form_text">
                                                        <strong>
                                                            <fmt:message bundle="${loc}" key="correct_category_form.status.lab"/>
                                                            <span class="notice_star">*</span>
                                                        </strong>
                                                    </div>
                                                    <div class="right_form_field">
                                                        <select class="cat_status_select" name="category_status">
                                                            <c:choose>
                                                                <c:when test="${not empty sessionScope.category_status}">
                                                                    <c:set var="corrected_status" value="${sessionScope.category_status}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:set var="corrected_status" value="${cat.status}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <c:remove var="category_status" scope="session"/>
                                                            <c:choose>
                                                                <c:when test="${corrected_status eq 'NEW'}">
                                                                    <option selected="selected" value="NEW">
                                                                        <fmt:message bundle="${loc}" key="category.status.new"/>
                                                                    </option>
                                                                    <option value="HOT">
                                                                        <fmt:message bundle="${loc}" key="category.status.hot"/>
                                                                    </option>
                                                                    <option value="OLD">
                                                                        <fmt:message bundle="${loc}" key="category.status.old"/>
                                                                    </option>
                                                                    <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                                                                        <option value="CLOSED">
                                                                            <fmt:message bundle="${loc}" key="category.status.closed"/>
                                                                        </option>
                                                                    </c:if>
                                                                </c:when>
                                                                <c:when test="${corrected_status eq 'HOT'}">
                                                                    <option value="NEW">
                                                                        <fmt:message bundle="${loc}" key="category.status.new"/>
                                                                    </option>
                                                                    <option selected="selected" value="HOT">
                                                                        <fmt:message bundle="${loc}" key="category.status.hot"/>
                                                                    </option>
                                                                    <option value="OLD">
                                                                        <fmt:message bundle="${loc}" key="category.status.old"/>
                                                                    </option>
                                                                    <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                                                                        <option value="CLOSED">
                                                                            <fmt:message bundle="${loc}" key="category.status.closed"/>
                                                                        </option>
                                                                    </c:if>
                                                                </c:when>
                                                                <c:when test="${corrected_status eq 'OLD'}">
                                                                    <option value="NEW">
                                                                        <fmt:message bundle="${loc}" key="category.status.new"/>
                                                                    </option>
                                                                    <option value="HOT">
                                                                        <fmt:message bundle="${loc}" key="category.status.hot"/>
                                                                    </option>
                                                                    <option selected="selected" value="OLD">
                                                                        <fmt:message bundle="${loc}" key="category.status.old"/>
                                                                    </option>
                                                                    <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                                                                        <option value="CLOSED">
                                                                            <fmt:message bundle="${loc}" key="category.status.closed"/>
                                                                        </option>
                                                                    </c:if>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:choose>
                                                                        <c:when test="${sessionScope.user.role eq 'ADMIN'}">
                                                                            <option value="NEW">
                                                                                <fmt:message bundle="${loc}" key="category.status.new"/>
                                                                            </option>
                                                                            <option value="HOT">
                                                                                <fmt:message bundle="${loc}" key="category.status.hot"/>
                                                                            </option>
                                                                            <option value="OLD">
                                                                                <fmt:message bundle="${loc}" key="category.status.old"/>
                                                                            </option>
                                                                            <option selected="selected" value="CLOSED">
                                                                                <fmt:message bundle="${loc}" key="category.status.closed"/>
                                                                            </option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option selected="selected" value="CLOSED">
                                                                                <fmt:message bundle="${loc}" key="category.status.closed"/>
                                                                            </option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </select>
                                                        <span class="errormsg" id="error_0_gender"></span>
                                                    </div>
                                                </label>
                                            </div>

                                            <div class="form_element ">
                                               <span class="errormsg">
                                                    <fmt:message bundle="${loc}" key="user_registration.form.msg_oblig"/>
                                                </span>
                                            </div>
                                            <div class="form_element submit_button">
                                                <fmt:message bundle="${loc}" key="corrected_category_form.submit_button" var="cor_submit_v"/>
                                                <input type="submit" value="${cor_submit_v}" class="create_button">
                                                <div class="cancel_submit_block" >
                                                    <fmt:message bundle="${config}" key="command.go_to_current_page" var="go_to_current_page"/>
                                                    <fmt:message bundle="${loc}" key="common.post.cancel_text" var="cancel_text"/>
                                                    <a class="cancel_link" href="${go_to_categories}${page_no}${requestScope.full_categories.getCurrentPage()}">${cancel_text}</a>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                    <c:remove var="show_category_correction" scope="session" />


                                </c:when>
                                <c:otherwise>
                                    <div class="page_block wide_block post_content">
                                        <div class="cat_img">
                                            <a href="${go_to_category}${cat.id}" class="cat_image">
                                                <img class="cat_mini_img" src="${cat.imageLink}" alt="some" onerror="src='/img/no_avatar.jpg'">
                                            </a>
                                        </div>
                                        <div class="cat_description">
                                            <c:choose>
                                                <c:when test="${sessionScope.locale eq 'ru'}">
                                                    <a href="${go_to_category}${cat.id}" class="cat_title">
                                                        <c:out value="${cat.titleRu}"/>
                                                    </a>
                                                    <div class="cat_content">
                                                        <c:out value="${cat.descriptionRu}"/>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${go_to_category}${cat.id}" class="cat_title">
                                                        <c:out value="${cat.titleEn}"/>
                                                    </a>
                                                    <div class="cat_content">
                                                        <c:out value="${cat.descriptionEn}"/>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <a href="${go_to_profile}${cat.userId}" class="cat_moderator">
                                                <c:out value="${cat.moderator.login}"/>
                                            </a>
                                        </div>
                                        <div class="right_info">
                                            <div class="cat_status">
                                                <c:out value="${cat.status}"/>
                                            </div>
                                            <div class="q_counter">
                                                <a href="${go_to_category}${cat.id}" class="cat_title">
                                                    <div class="count">
                                                        <fmt:message bundle="${loc}" key="categories.txt.posts"/>
                                                        <c:out value=" ${cat.questionQuantity}"/>
                                                    </div>
                                                </a>
                                            </div>
                                        </div>
                                        <div class="correct_cat_block">
                                            <div class="fl_r">
                                                <c:if test="${sessionScope.user.role eq 'ADMIN' and cat.status ne 'CLOSED'}">
                                                    <form action="/Controller" method="post" class="inline">
                                                        <input type="hidden" name="command" value="close_category"/>
                                                        <input type="hidden" name="category_id" value="${cat.id}"/>
                                                        <input type="hidden" name="page_no" value="${requestScope.full_categories.getCurrentPage()}"/>
                                                        <button type="submit" class="correct_post">
                                                            <span class="icon icon_cross" title="${close_cat_title}"></span>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <form action="/Controller" method="post" class="inline">
                                                    <input type="hidden" name="command" value="go_to_category_correction"/>
                                                    <input type="hidden" name="category_id" value="${cat.id}"/>
                                                    <input type="hidden" name="page_no" value="${requestScope.full_categories.getCurrentPage()}"/>
                                                    <button type="submit" class="correct_post">
                                                        <span class="icon icon_pencil" title="${correct_cat_title}"></span>
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
            <c:if test="${requestScope.full_categories.getTotalPagesCount() > 1}">
                <ul class="pagination">
                    <c:if test="${requestScope.full_categories.getCurrentPage() > 1}">
                        <li>
                            <a href="${go_to_categories}${page_no}${requestScope.full_categories.getCurrentPage()-1}">«</a>
                        </li>
                    </c:if>
                    <c:forEach var="number" begin="1" end="${requestScope.full_categories.getTotalPagesCount()}">
                        <c:choose>
                            <c:when test="${number eq requestScope.full_categories.getCurrentPage()}">
                                <li><a class="active">${number}</a></li>
                            </c:when>
                            <c:otherwise>
                                <li>
                                    <a href="${go_to_categories}${page_no}${number}">${number}</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${requestScope.full_categories.getCurrentPage() < requestScope.full_categories.getTotalPagesCount()}">
                        <li>
                            <a href="${go_to_categories}${page_no}${requestScope.full_categories.getCurrentPage()+1}">»</a>
                        </li>
                    </c:if>
                </ul>
            </c:if>
        </section>
    </div>
</div>
</body>
</html>