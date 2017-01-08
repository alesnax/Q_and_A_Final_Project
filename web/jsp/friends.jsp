<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 04.01.2017
  Time: 20:22
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
    <meta charset="utf-8">
    <title>
        <fmt:message bundle="${loc}" key="friends.title_text"/>
    </title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="../css/friends_style.css">
</head>
</head>
<body>


<c:import url="template/header_common.jsp"/>

<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp"/>


        <section>
            <div class="wall_content wide_block">
                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <fmt:message bundle="${loc}" key="friends.main_title"/>
                        </h1>
                    </div>
                </div>


                <c:choose>
                    <c:when test="${empty requestScope.friends}">
                        <div class="page_block wide_block post_content">
                            <div class="page_main_header_block">
                                <div class="no_friends">
                                    <fmt:message bundle="${loc}" key="friends.txt.no_friends"/>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="friend" items="${requestScope.friends}">
                            <div class="page_block wide_block post_content">
                                <div class="user_img">
                                    <fmt:message bundle="${config}" key="command.go_to_profile" var="go_to_profile"/>
                                    <a href="${go_to_profile}${friend.id}" class="user_image">
                                        <img class="mini_img" src="${friend.avatar}" alt="avatar">
                                    </a>
                                </div>
                                <div class="friend_description">
                                    <a href="${go_to_profile}${friend.id}" class="login_title">
                                        <c:out value="${friend.name} ${friend.surname} (${friend.login})"/>
                                    </a>

                                    <div class="f_status_msg_block">
                                        <c:out value="${friend.userStatus}"/>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                    </c:otherwise>
                </c:choose>


            </div>

        </section>
    </div>

</div>


</body>
</html>
