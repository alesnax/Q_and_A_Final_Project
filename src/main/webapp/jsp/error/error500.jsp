<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>
<html>
<head>
    <meta charset="utf-8">

    <title>
        <fmt:message bundle="${loc}" key="error500_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../../css/categories_style.css">
</head>
<body>

<c:import url="../template/header_common.jsp"/>
<div class="page_layout">
    <div class="content">
        <c:import url="../template/left_bar.jsp"/>
        <section>
            <div class="page_block wide_block post_content">
                <div class="page_main_header_block">
                    <h1>
                        <fmt:message bundle="${loc}" key="error500_main_title"/>
                    </h1>
                </div>
            </div>
            <div class="wall_content wide_block">
                <div class="page_block wide_block post_content wrong_message_block">
                    <div class="error_msg">
                        <c:choose>
                            <c:when test="${requestScope.service_error.contains('NumberFormatException')}">
                                <fmt:message bundle="${loc}" key="error_500.number_format_error"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:message bundle="${loc}" key="error_500.common_error"/>
                                ${requestScope.service_error}
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="owl_img_block">
                <img class="owl_img" src="../../img/no_avatar.jpg" alt="some">
            </div>
            <div class="return_block">
                <div class="return_to_bans_header">
                    <fmt:message bundle="${config}" key="command.go_to_first_page" var="go_to_first_page"/>
                    <a class="answers_title" href="${go_to_first_page}">
                        <span><fmt:message bundle="${loc}" key="error.return_to_main_page"/></span>
                    </a>
                </div>
            </div>
        </section>
    </div>
</div>
</body>
</html>