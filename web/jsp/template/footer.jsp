<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 02.01.2017
  Time: 23:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>


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
