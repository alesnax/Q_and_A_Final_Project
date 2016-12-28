<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 13.12.2016
  Time: 15:44
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
    <link rel="stylesheet" href="/css/header_search_block_style.css">
</head>
<body>

<fmt:message bundle="${loc}" key="common.header.search_form.placeholder" var="s_form_ph"/>
<fmt:message bundle="${loc}" key="common.header.search_form.submit_value" var="s_submit_v"/>

<div class="fl_l search_block">
    <form class="search_form" action="/Controller" method="get" >
        <input type="hidden" name="command" value="search_answer_question"/>
        <input class="s_back_img search_input" name="question" value="" type="text" placeholder="${s_form_ph}" />
        <input class="search_submit" type="submit" name="search" value="${s_submit_v}" />
    </form>
</div>


</body>
</html>
