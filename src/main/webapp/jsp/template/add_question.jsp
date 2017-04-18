<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="locale" var="loc"/>
<fmt:setBundle basename="config" var="config"/>

<html>
<head>
    <link rel="stylesheet" href="../../css/add_question_style.css">
</head>
<body>
<c:choose>
    <c:when test="${not empty sessionScope.user and sessionScope.user.banned eq true}">
        <div class="ban_q_message_block">
            <div class="error_msg">
                <fmt:message bundle="${loc}" key="common.ask_form_block.banned_message"/>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="ask_form_block">
            <h3>
                <fmt:message bundle="${loc}" key="common.ask_form_block.h3_text"/>
            </h3>
            <form class="q_ask_form" action="/Controller" method="get">
                <input type="hidden" name="command" value="add_question"/>
                <div>
                    <c:if test="${not empty sessionScope.question_validation_failed}">
                        <c:forEach var="error" items="${sessionScope.question_validation_failed}">
                            <fmt:message bundle="${loc}" key="${error}" var="msg"/>
                            <span class="errormsg">
                                <c:out value="${msg}"/>
                            <br/>
                        </span>
                        </c:forEach>
                        <c:remove var="question_validation_failed" scope="session"/>
                    </c:if>
                </div>
                <c:if test="${not empty sessionScope.quest_added_status}">
                    <fmt:message bundle="${loc}" key="${sessionScope.quest_added_status}" var="msg"/>
                    <span class="success_msg">
                    <c:out value="${msg}"/>
                    <br/>
                    <c:remove var="quest_added_status" scope="session"/>
                </span>
                </c:if>
                <fmt:message bundle="${loc}" key="common.ask_form_block.input_placeholder" var="quest_title_ph"/>
                <input class="q_title_place" type="text" name="question_title" value="${sessionScope.get('question_title')}"
                       placeholder="${quest_title_ph}"/>
                <select class="q_select_place" name="category">
                    <option selected="selected" disabled>
                        <fmt:message bundle="${loc}" key="common.ask_form_block.select_category"/>
                    </option>
                    <c:forEach var="cat" items="${sessionScope.categories_info}">
                        <c:choose>
                            <c:when test="${sessionScope.locale eq 'ru'}">
                                <c:choose>
                                    <c:when test="${cat.id eq sessionScope.get('category')}">
                                        <option selected="selected" value="${cat.id}">${cat.titleRu}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${cat.id}">${cat.titleRu}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${cat.id eq sessionScope.get('category')}">
                                        <option selected="selected" value="${cat.id}">${cat.titleEn}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${cat.id}">${cat.titleEn}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                </select>
                <fmt:message bundle="${loc}" key="common.ask_form_block.textarea_placeholder" var="textarea_placeholder"/>
                <fmt:message bundle="${loc}" key="common.ask_form_block.add_submit" var="add_submit"/>
                <textarea class="q_form_place" name="description" rows="3" placeholder="${textarea_placeholder}"><c:out value="${sessionScope.get('description')}"/></textarea>
                <input class="q_form_submit" type="submit" value="${add_submit}"/>
            </form>
            <form action="/Controller" method="post">
                <fmt:message bundle="${loc}" key="add_new_question.clean.submit" var="clean_submit" />
                <input type="hidden" name="command" value="clean_question_form"/>
                <input class="q_form_clean" type="submit" value="${clean_submit}"/>
            </form>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>