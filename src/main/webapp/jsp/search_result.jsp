<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <title>
        <fmt:message bundle="${loc}" key="search_result.title_text"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/reposts_style.css">
</head>
<body>

<c:import url="template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="template/left_bar.jsp"/>
        <section>
            <c:if test="${not empty sessionScope.wrong_command_message}">
                <div class="wrong_message_block">
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
                        <fmt:message bundle="${loc}" key="search_result.main_title_text"/>
                    </h1>
                </div>
            </div>
            <div class="wall_content wide_block">
                <c:choose>
                    <c:when test="${empty requestScope.posts.items}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="result_notice">
                                    <fmt:message bundle="${loc}" key="search_result.txt.no_posts_first"/>
                                        ${requestScope.search_query}
                                    <fmt:message bundle="${loc}" key="search_result.txt.no_posts_second"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="result_notice">
                                    <fmt:message bundle="${loc}" key="search_result.txt.posts_found_first"/>
                                        ${requestScope.search_query}
                                    <fmt:message bundle="${loc}" key="search_result.txt.posts_found_second"/>
                                        ${requestScope.posts.totalCount}
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                <c:import url="template/question_list.jsp"/>
                <c:if test="${requestScope.posts.getTotalPagesCount() > 1}">
                    <fmt:message bundle="${config}" key="command.search_posts" var="search_posts"/>
                    <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                    <ul class="pagination">
                        <c:if test="${requestScope.posts.getCurrentPage() > 1}">
                            <li>
                                <a href="${search_posts}${requestScope.search_query}${page_no}${requestScope.posts.getCurrentPage()-1}">«</a>
                            </li>
                        </c:if>
                        <c:forEach var="number" begin="1" end="${requestScope.posts.getTotalPagesCount()}">
                            <c:choose>
                                <c:when test="${number eq requestScope.posts.getCurrentPage()}">
                                    <li><a class="active">${number}</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li>
                                        <a href="${search_posts}${requestScope.search_query}${page_no}${number}">${number}</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${requestScope.posts.getCurrentPage() < requestScope.posts.getTotalPagesCount()}">
                            <li>
                                <a href="${search_posts}${requestScope.search_query}${page_no}${requestScope.posts.getCurrentPage()+1}">»</a>
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
