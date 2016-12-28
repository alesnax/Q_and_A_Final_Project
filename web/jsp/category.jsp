<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 18.12.2016
  Time: 13:42
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
    <meta charset="utf-8">
    <title>Q&A - category</title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="/css/sprite.css">
    <link rel="stylesheet" href="/css/category_style.css">
</head>
<body>


<fmt:message bundle="${config}" key="command.go_to_main_page" var="go_to_main"/>


<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <a href="..${go_to_main}"><img class="header_logo" src="/img/logo.png" alt="Q&A logo"/></a>
        </div>


        <c:import url="template/header_search_block.jsp"/>

        <c:if test="${not empty sessionScope.user}">
            <div class="fl_r h_links">
                <a class="h_link" href="../Controller?command=log_out">log out</a>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <c:import url="template/switch_language.jsp"/>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_registration_page" var="go_to_registration"/>
                <fmt:message bundle="${loc}" key="common.sign_up_text" var="sign_up_text"/>
                <a class="h_link" href="${go_to_registration}">${sign_up_text}</a>
            </div>
            <div class="fl_r h_links">
                <fmt:message bundle="${config}" key="command.go_to_authorization_page" var="go_to_login"/>
                <fmt:message bundle="${loc}" key="common.sign_in_text" var="sign_in_text"/>

                <a class="h_link" href="${go_to_login}">${sign_in_text}</a>
            </div>


        </c:if>


    </div>
</header>
<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp"/>

        <section>



            <div class="wall_content wide_block">
                <c:import url="template/add_question.jsp"/>

                <div class="page_block wide_block post_content">
                    <div class="page_main_header_block">
                        <h1>
                            <c:choose>
                                <c:when test="${sessionScope.locale eq 'ru'}" >${sessionScope.categories_info.get(0).titleRu}</c:when>
                                <c:otherwise>${sessionScope.categories_info.get(0).titleEn}</c:otherwise>
                            </c:choose>
                        </h1>
                    </div>
                </div>


            <c:forEach var="post" items="${requestScope.questions}">
                <div class="page_block wide_block post_content">

                    <div class="post_header">
                        <div class="post_header_left">
                            <a href="/Controller?command=go_to_profile&user_id=${post.user.id}" class="post_image">
                                <img class="mini_img" src="${post.user.avatar}" alt="some">
                            </a>
                            <div class="post_header_info">
                                <h5 class="post_author">
                                    <a class="user"
                                       href="/Controller?command=go_to_profile&user_id=${post.user.id}"><span>${post.user.login}</span></a>
                                </h5>
                                <div class="post_date">
                                    <span class="rel_date apost_date">
                                        <fmt:formatDate value="${post.publishedTime}" type="both" dateStyle="long" timeStyle="medium"/><br/>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="post_header_right">
                            <div>
                                <a class="delete" href="complaint">
                                    <span class="icon icon_bin"
                                          title="If you found bad content, please say to admin about this"></span>
                                </a>
                                <a class="correct_post" href="correct_post">
                                    <span class="icon icon_pencil" title="choose to correct post"></span>
                                </a>
                            </div>
                            <div class="post_category">
                                <fmt:message bundle="${loc}" key="category.txt.category_span" var="category_span"/>
                                <span>${category_span}</span>
                                <a href="/Controller?command=go_to_category&cat_id=${post.categoryInfo.id}" title="go to all category questions">
                                    <c:choose>
                                        <c:when test="${sessionScope.locale eq 'ru'}" >${post.categoryInfo.titleRu}</c:when>
                                        <c:otherwise>${post.categoryInfo.titleEn}</c:otherwise>
                                    </c:choose>
                                </a>
                            </div>

                        </div>
                    </div>
                    <div class="post_inner_content">
                        <div class="wall_text">
                            <div class="post_q_title">
                                <span>${post.title}</span>
                            </div>
                            <div class="post_description">
                                <span>
                                        ${post.content}
                                </span>
                            </div>
                        </div>
                        <div class="post_footer">
                            <a href="rate_1"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_2"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_3"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_4"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_5"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_6"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_7"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_8"><span data-descr="set your mark"
                                                   class="rate icon icon_star-full"></span></a>
                            <a href="rate_9"><span data-descr="set your mark"
                                                   class="rate icon icon_star-half"></span></a>
                            <a href="rate_10"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="show_all_marks" title="average mark. Click to show all marks">
                                <span class="rate_number">${post.averageMark}</span>
                            </a>
                        </div>
                    </div>
                    <div class="answers_block">
                        <div class="answers_header">
                            <div class="answers_title">
                                <span>Answers:</span>
                                <span>2</span>
                            </div>
                        </div>
                    </div>
                </div>
                </c:forEach>

        </section>
    </div>
</div>
</body>
</html>