<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 18.12.2016
  Time: 13:42
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
    <c:choose>
        <c:when test="${sessionScope.locale eq 'ru'}">
            <c:set var="current_category" value="${requestScope.questions[0].categoryInfo.titleRu}"/>
        </c:when>
        <c:otherwise>
            <c:set var="current_category" value="${requestScope.questions[0].categoryInfo.titleEn}"/>
        </c:otherwise>
    </c:choose>
    <title>
        <fmt:message bundle="${loc}" key="category.page_title"/> ${current_category}
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="/css/category_style.css">
</head>
<body>


<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>

<c:import url="template/header_common.jsp"/>

<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp"/>

        <section>

            <c:import url="template/add_question.jsp"/>

            <div class="page_block wide_block post_content">
                <div class="page_main_header_block">
                    <h1>
                        ${current_category}
                    </h1>
                </div>
            </div>

            <c:choose>
                <c:when test="${requestScope.questions[0].id eq 0}">
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

                </c:otherwise>
            </c:choose>


        </section>
    </div>
</div>
</body>
</html>