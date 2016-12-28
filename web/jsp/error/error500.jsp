<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<html>
<head>
    <title>Q and A</title>
</head>
<body>
    It's error 500 error in JAVA code
    <c:forEach var="entry" items="${sessionScope.entrySet()}">
        <br/>
        <c:out value="${entry.key} : ${entry.value}" />
        <br/>
    </c:forEach>
<br>
<br>
<br>
<br>
<br>
    <c:forEach var="entry" items="${requestScope.entrySet()}">
        <br/>
        <c:out value="${entry.key} : ${entry.value}" />
        <br/>
    </c:forEach>

</body>
</html>