<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>
<html>
<head>
    <meta charset="utf-8">
    <c:choose>
        <c:when test="${sessionScope.locale eq 'ru'}">
            <c:set var="current_category" value="${requestScope.posts.items[0].categoryInfo.titleRu}"/>
        </c:when>
        <c:otherwise>
            <c:set var="current_category" value="${requestScope.posts.items[0].categoryInfo.titleEn}"/>
        </c:otherwise>
    </c:choose>
    <c:set var="current_category_id" value="${requestScope.posts.items[0].categoryInfo.id}"/>
    <title>
        <fmt:message bundle="${loc}" key="category.page_title"/> ${current_category}
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/category_style.css">
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
            <c:import url="template/add_question.jsp"/>
            <div class="page_block wide_block post_content">
                <div class="page_main_header_block">
                    <h1>
                        ${current_category}
                    </h1>
                </div>
            </div>
            <c:choose>
                <c:when test="${requestScope.posts.items[0].id eq 0}">
                    <div class="page_block wide_block post_content">
                        <div class="page_main_header_block">
                            <div class="no_questions">
                                <fmt:message bundle="${loc}" key="category.txt.no_questions"/>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:import url="template/question_list.jsp" />
                    <c:if test="${requestScope.posts.getTotalPagesCount() > 1}">
                        <fmt:message bundle="${config}" key="command.go_to_category" var="go_to_category"/>
                        <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                        <ul class="pagination">
                            <c:if test="${requestScope.posts.getCurrentPage() > 1}">
                                <li>
                                    <a href="${go_to_category}${current_category_id}${page_no}${requestScope.posts.getCurrentPage()-1}">«</a>
                                </li>
                            </c:if>
                            <c:forEach var="number" begin="1" end="${requestScope.posts.getTotalPagesCount()}">
                                <c:choose>
                                    <c:when test="${number eq requestScope.posts.getCurrentPage()}">
                                        <li><a class="active">${number}</a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li>
                                            <a href="${go_to_category}${current_category_id}${page_no}${number}">${number}</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${requestScope.posts.getCurrentPage() < requestScope.posts.getTotalPagesCount()}">
                                <li>
                                    <a href="${go_to_category}${current_category_id}${page_no}${requestScope.posts.getCurrentPage()+1}">»</a>
                                </li>
                            </c:if>
                        </ul>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</div>
</body>
</html>