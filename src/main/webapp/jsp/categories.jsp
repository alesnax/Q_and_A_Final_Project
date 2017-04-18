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
        <fmt:message bundle="${loc}" key="categories.title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/categories_style.css">
</head>
<body>
<fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>
<fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>

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
                <c:if test="${empty sessionScope.user}">
                    <div class="welcome_block">
                        <fmt:message bundle="${loc}" key="categories.explain_block"/>
                    </div>
                </c:if>
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="categories.txt.categories"/>
                        </h1>
                    </div>
                </div>
                <c:forEach var="cat" items="${requestScope.full_categories.items}">
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
                    </div>
                </c:forEach>
            </div>
            <c:if test="${requestScope.full_categories.getTotalPagesCount() > 1}">
                <fmt:message bundle="${config}" key="command.go_to_quest_categories" var="go_to_categories"/>
                <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
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

            <c:import url="template/add_question.jsp"/>
        </section>
    </div>
</div>
</body>
</html>