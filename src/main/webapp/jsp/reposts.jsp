<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>
<html>
<head>
    <title>
        <fmt:message bundle="${loc}" key="reposts.title_text"/>
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
                            <fmt:message bundle="${loc}" key="reposts.main_title_text"/>
                        </h1>
                    </div>
                </div>
                <div class="wall_content wide_block">
                    <c:import url="template/question_list.jsp"/>
                    <c:if test="${requestScope.posts.getTotalPagesCount() > 1}">
                        <fmt:message bundle="${config}" key="command.go_to_reposts" var="go_to_reposts"/>
                        <fmt:message bundle="${config}" key="command.page_query_part" var="page_no"/>
                        <ul class="pagination">
                            <c:if test="${requestScope.posts.getCurrentPage() > 1}">
                                <li>
                                    <a href="${go_to_reposts}${page_no}${requestScope.posts.getCurrentPage()-1}">«</a>
                                </li>
                            </c:if>
                            <c:forEach var="number" begin="1" end="${requestScope.posts.getTotalPagesCount()}">
                                <c:choose>
                                    <c:when test="${number eq requestScope.posts.getCurrentPage()}">
                                        <li><a class="active">${number}</a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li>
                                            <a href="${go_to_reposts}${page_no}${number}">${number}</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${requestScope.posts.getCurrentPage() < requestScope.posts.getTotalPagesCount()}">
                                <li>
                                    <a href="${go_to_reposts}${page_no}${requestScope.posts.getCurrentPage()+1}">»</a>
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
