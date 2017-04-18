<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>
        <fmt:message bundle="${loc}" key="best_a.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/category_style.css">
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
                        <fmt:message bundle="${loc}" key="best_a.main_title"/>
                    </h1>
                </div>
            </div>
            <c:import url="template/question_list.jsp"/>
            <c:if test="${requestScope.posts.getTotalPagesCount() > 1}">
                <fmt:message bundle="${config}" key="command.find_best_answers" var="find_best_answers"/>
                <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                <ul class="pagination">
                    <c:if test="${requestScope.posts.getCurrentPage() > 1}">
                        <li>
                            <a href="${find_best_answers}${page_no}${requestScope.posts.getCurrentPage()-1}">«</a>
                        </li>
                    </c:if>
                    <c:forEach var="number" begin="1" end="${requestScope.posts.getTotalPagesCount()}">
                        <c:choose>
                            <c:when test="${number eq requestScope.posts.getCurrentPage()}">
                                <li><a class="active">${number}</a></li>
                            </c:when>
                            <c:otherwise>
                                <li>
                                    <a href="${find_best_answers}${page_no}${number}">${number}</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${requestScope.posts.getCurrentPage() < requestScope.posts.getTotalPagesCount()}">
                        <li>
                            <a href="${find_best_answers}${page_no}${requestScope.posts.getCurrentPage()+1}">»</a>
                        </li>
                    </c:if>
                </ul>
            </c:if>
        </section>
    </div>
</div>
</body>
</html>