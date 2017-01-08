<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 04.01.2017
  Time: 18:46
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
    <title>
        <fmt:message bundle="${loc}" key="reposts.title_text"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="/css/reposts_style.css">


</head>
<body>
    <c:import url="template/header_common.jsp"/>
    <div class="page_layout">
        <div class="content">
            <c:import url="template/left_bar.jsp"/>
            <section>
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
                </div>
            </section>
        </div>
    </div>
</body>
</html>
