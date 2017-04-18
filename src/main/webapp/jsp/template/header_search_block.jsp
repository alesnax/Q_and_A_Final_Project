<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <link rel="stylesheet" href="../../css/header_search_block_style.css">
</head>
<body>

<fmt:message bundle="${loc}" key="common.header.search_form.placeholder" var="s_form_ph"/>
<fmt:message bundle="${loc}" key="common.header.search_form.submit_value" var="s_submit_v"/>

<div class="fl_l search_block">
    <form class="search_form" action="/Controller" method="get">
        <input type="hidden" name="command" value="search_posts"/>
        <input class="s_back_img search_input" name="content" value="" type="text" placeholder="${s_form_ph}"/>
        <input class="search_submit" type="submit" value="${s_submit_v}"/>
    </form>
</div>


</body>
</html>
