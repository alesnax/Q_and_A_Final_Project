<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setBundle basename="config" var="config"/>

<html>
<head>
</head>
<body>

<fmt:message bundle="${config}" key="command.go_to_first_page" var="go_to_first_page"/>

<c:redirect url="${go_to_first_page}"/>

</body>
</html>