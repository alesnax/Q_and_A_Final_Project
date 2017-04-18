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
        <fmt:message bundle="${loc}" key="welcome.page_title"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/welcome_style.css">
</head>
<body>
<c:import url="template/header_common.jsp"/>
<div class = "center-div">
    <p class = "first">Q&A</p>
    <p class="second"><fmt:message bundle="${loc}" key="welcome.page.first_welcome"/></p>
    <p class="second"><fmt:message bundle="${loc}" key="welcome.page.second_welcome"/></p>
    <div class="buttonD">
        <fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main_page"/>
        <a href="${go_to_main_page}">
            <p><fmt:message bundle="${loc}" key="welcome.page.link_text"/></p>
        </a>
    </div>
</div>
</body>
</html>
