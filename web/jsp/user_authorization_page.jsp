<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 08.12.2016
  Time: 14:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta charset="utf-8">
    <title>Q&amp;A - Answers and Questions for curious people</title>
    <link rel="shortcut icon" href="/img/q_logo.png" type="image/png">
    <link rel="stylesheet" href="/css/user_authorization_style.css">
    <link rel="stylesheet" href="/css/sprite.css">

</head>
<body>
<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>




<header>
    <div class="back"></div>
    <div class="topbar_wrapper">
        <div class="fl_l ">
            <a href="../Controller?command=go_to_main_page"><img class="header_logo" src="/img/logo.png" alt="Q&amp;A logo"></a>
        </div>


        <c:import url="template/header_search_block.jsp" />


        <c:import url="template/switch_language.jsp" />


        <div class="fl_r h_links">
            <a class="h_link" href="../Controller?command=go_to_registration_page">Sign up</a>
        </div>
    </div>
</header>
<div class="page_layout">
    <div class="content">

        <c:import url="template/left_bar.jsp" />

        <section>
            <div class="wall_content wide_block">
                <div class="validation_header page_block ">
                    <div class="logo_block">
                        <a href="../Controller?command=go_to_main_page">
                            <img class="link_logo" src="/img/logo.png" alt="Q&amp;A logo">
                        </a>
                    </div>
                    <div class="welcome_block">
                        <h1>
                            <c:choose>
                                <c:when test="${not empty welcome_msg}">
                                    <fmt:message bundle="${loc}" key="${welcome_msg}" var="msg"/>
                                    <c:out value="${msg}"/>
                                    <c:remove var="welcome_msg" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:message bundle="${loc}" key="guest.user_authorization_page.welcome_simple_msg" var="simple_msg"/>
                                    <c:out value="${simple_msg}"/>
                                </c:otherwise>
                            </c:choose>
                        </h1>

                        <h2>Please log in!</h2>
                    </div>
                </div>
                <div class="validation_block ">
                    <div class="fl_l left_block">
                        <div class="create_account_form_block page_block">
                            <form onsubmit="return validateLoginForm()" class="create_account_form" id="create_account"
                                  name="create_account" action="../Controller" method="post">
                                <input type="hidden" name="command" value="user_authorization">
                                <div class="form_element name">
                                    <c:if test="${not empty not_registered_user_yet}">
                                        <c:forEach var="error" items="${not_registered_user_yet}">
                                            <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                                            <span class="errormsg">
                                                    ${msg}
                                            </span>
                                        </c:forEach>
                                        <c:remove var="not_registered_user_yet" />
                                    </c:if>
                                </div>

                                <div class="form_element login">
                                    <label>
                                        <strong>Email</strong>
                                        <input type="text" value="" name="email" id="email" class=""
                                               placeholder="Email">
                                    </label>
                                    <span class="errormsg" id="error_0_email"></span>
                                </div>

                                <div class="form_element password_form_element">
                                    <label>
                                        <strong></strong>
                                        <input type="password" value="" name="Passwd" id="Passwd" class=""
                                               placeholder="Password">
                                    </label>
                                    <span class="errormsg" id="error_0_Passwd"></span>
                                </div>
                                <div class="form_element password_forgot_element">
                                    <a class="forgot_pass" href="Controller">Forgot Password?</a>
                                </div>
                                <div class="form_element submit_button">
                                    <span class="errormsg" id="error_0_enter"></span>
                                    <input type="submit" value="Login" name="submit" class="login_button">
                                </div>
                            </form>
                        </div>
                        <div class="or_register_block page_block">
                            <div class="form_element password_form_element">
                                <div class="register_header">
                                    <span>Don't have account? </span>
                                </div>
                                <div class="register_link_block">
                                    <a class="or_reg_link" href="../Controller?command=go_to_registration_page">Create it!</a>
                                </div>
                            </div>
                        </div>


                    </div>
                    <div class="sample_block page_block">
                        <div>
                            <h3>Q&amp;A is a simple way to find out something new, don't wait, registrate and explore.
                                You have question, other have answers!</h3>
                        </div>
                        <img class="sample_img" src="/img/page_sample.jpg" alt="page_sample">
                    </div>
                </div>
            </div>
        </section>
        <footer>
            <span>© Aliaksandar Nakhankou</span>
            <address><a href="mailto:alesnax@gmail.com">alesnax@gmail.com</a></address>
        </footer>
    </div>
</div>
<script src="/js/user_login_validation_script.js">
</script>


</body>
</html>
