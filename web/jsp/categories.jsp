<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 17.12.2016
  Time: 20:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">

    <title>
        <fmt:message bundle="${loc}" key="categories.title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/sprite.css">
    <link rel="stylesheet" href="../css/categories_style.css">
</head>
<body>


<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>
            <a href="..${go_to_main}">
                <img class="header_logo" src="../img/logo.png" alt="Q&A logo"/>
            </a>
        </div>
        <c:import url="template/header_search_block.jsp"/>
        <c:if test="${not empty sessionScope.user}">
            <div class="fl_r h_links">
                <a class="h_link" href="../Controller?command=log_out">log out</a>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <c:import url="template/switch_language.jsp"/>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_registration_page" var="go_to_registration"/>
                <a class="h_link" href="${go_to_registration}">
                    <fmt:message bundle="${loc}" key="common.sign_up_text"/>
                </a>
            </div>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_authorization_page" var="go_to_login"/>
                <a class="h_link" href="${go_to_login}">
                    <fmt:message bundle="${loc}" key="common.sign_in_text"/>
                </a>
            </div>
        </c:if>
    </div>
</header>
<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp"/>

        <section>
            <div class="wall_content wide_block">
                <c:if test="${not empty wrong_command_message}">
                    <div class="page_block wide_block post_content wrong_message_block">
                        <div class="error_msg">
                            <fmt:message bundle="${loc}" key="${wrong_command_message}"/>
                            <c:remove var="wrong_command_message" scope="session"/>
                        </div>
                    </div>
                </c:if>

                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="categories.txt.categories"/>
                        </h1>
                    </div>
                </div>
                <c:forEach var="cat" items="${requestScope.full_categories}">
                    <div class="page_block wide_block post_content">
                        <div class="cat_img">
                            <a href="/Controller?command=go_to_category&cat_id=${cat.id}" class="cat_image">
                                <img class="cat_mini_img" src="${cat.imageLink}" alt="some">
                            </a>
                        </div>
                        <div class="cat_description">
                            <c:choose>
                                <c:when test="${sessionScope.locale eq 'ru'}">
                                    <a href="/Controller?command=go_to_category&cat_id=${cat.id}" class="cat_title">
                                        <c:out value="${cat.titleRu}"/>
                                    </a>
                                    <div class="cat_content">
                                        <c:out value="${cat.descriptionRu}"/>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <a href="/Controller?command=go_to_category&cat_id=${cat.id}" class="cat_title">
                                        <c:out value="${cat.titleEn}"/>
                                    </a>
                                    <div class="cat_content">
                                        <c:out value="${cat.descriptionEn}"/>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <a href="/Controller?command=go_to_profile&user_id=${cat.userId}" class="cat_moderator">
                                <c:out value="${cat.moderator.login}"/>
                            </a>
                        </div>
                        <div class="right_info">
                            <div class="cat_status">
                                <c:out value="${cat.status}"/>
                            </div>
                            <div class="q_counter">

                                <div class="count">
                                    <fmt:message bundle="${loc}" key="categories.txt.questions"/>
                                    <c:out value=" ${cat.questionQuantity}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:import url="template/add_question.jsp"/>

        </section>
    </div>

</div>
</body>
</html>