<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>


<html>
<head>
</head>
<body>
<footer>
    <span>&copy; <fmt:message bundle="${loc}" key="common.creator_name"/></span>
    <address>
        <fmt:message bundle="${config}" key="common.creator.email" var="creator_email"/>
        <a href="mailto:${creator_email}">
            ${creator_email}
        </a>
    </address>
    <%--сделать ссылку на профиль--%>
</footer>
</body>
</html>
