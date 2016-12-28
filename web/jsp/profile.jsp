<%--
  User: alesnax
  Date: 03.12.2016
  Time: 17:50
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="resources.locale" var="loc"/>
<fmt:setBundle basename="resources.config" var="config"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Q&amp;A - Answers and Questions for curious people</title>
    <fmt:message bundle="${config}" key="img.common.logo_icon" var="logo_icon"/>
    <link rel="shortcut icon" href="${logo_icon}" type="image/png">
    <link rel="stylesheet" href="/css/sprite.css">
    <link rel="stylesheet" href="/css/user_profile_style.css">
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




<div id="page_layout">
    <div id="content">

        <c:import url="template/left_bar.jsp" />


        <section>
            <div class="top_block">
                <div class="page_block photo_block">
                    <div class="page_avatar">
                        <div class="photo-wrap">
                            <img id="avatar" src="/img/malevich.jpg" alt="/img/alesax.jpg"/>
                        </div>
                        <div class="profile_edit">
                            <a id="profile_edit_act" href="edit_profie">Edit</a>
                        </div>
                    </div>
                </div>
                <div class="page_block short_info_block">
                    <div class="profile_name">
                        <h2>${user.name} ${user.surname} </h2>
                    </div>
                    <div class="short_info">
                        <table>
                            <tbody>
                            <tr>
                                <td class="info_label">Birthday:</td>
                                <td class="info_labeled">${user.birthday} </td>
                            </tr>
                            <c:if test="${not empty user.country}" >
                                <tr>
                                    <td class="info_label">Country:</td>
                                    <td class="info_labeled">${user.country}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty user.city}" >
                                <tr>
                                    <td class="info_label">HomeTown:</td>
                                    <td class="info_labeled">${user.city}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty user.email}" >
                                <tr>
                                    <td class="info_label">Email:</td>
                                    <td class="info_labeled">${user.email}</td>
                                </tr>
                            </c:if>

                            </tbody>
                        </table>
                    </div>
                    <div class="counts_module">
                        <a class="page_counter" href="/show_friends">
                            <div class="count">25</div>
                            <div class="label">friends</div>
                        </a>
                        <a class="page_counter" href="/show_rate">
                            <div class="count">8.5</div>
                            <div class="label">rate</div>
                        </a>
                        <a class="page_counter" href="/show_question_count">
                            <div class="count">12</div>
                            <div class="label">questions</div>
                        </a>
                        <a class="page_counter" href="/show_answer_count">
                            <div class="count">8</div>
                            <div class="label">answers</div>
                        </a>
                        <a class="page_counter" href="/show_best_answer_count">
                            <div class="count">2</div>
                            <div class="label">best answers</div>
                        </a>
                    </div>
                </div>
            </div>

            <c:import url="template/add_question.jsp" />

            <div class="wall_content wide_block">
                <div class="page_block wide_block post_content">
                    <div class="post_header">
                        <div class="post_header_left">
                            <a href="salesnax" class="post_image">
                                <img class="mini_img" src="/img/malevich.jpg" alt="some">
                            </a>
                            <div class="post_header_info">
                                <h5 class="post_author">
                                    <a class="user" href="alesnax"><span>Aliaksandr Nakhankou</span></a>
                                </h5>
                                <div class="post_date">
                                    <a href="apost_date">
                                        <span class="rel_date">25 mar 2016</span>
                                    </a>

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
                                <span>Category:</span>
                                <a href="science" title="go to all category questions">Science</a>
                            </div>

                        </div>
                    </div>
                    <div class="post_inner_content">
                        <div class="wall_text">
                            <div class="post_q_title">
                                <span>Why don't people believe in the Ancient Aliens theory?</span>
                            </div>
                            <div class="post_description">
										<span>Because ancient humans weren’t morons. Believe it or not,
										a particular merchant in Sumeria 5000 years ago is just as smart
										as the CEO of a fortune 500 company now. That builder in Memphis,
										Egypt knew his craft as well as the engineers who built the Taipei Tower?</span>
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
                                <span class="rate_number">8.6</span>
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
                        <div class="answer_block">
                            <div class="answer_header wide_block">

                                <div class="post_header_left">
                                    <a class="post_image" href="/falkovich">
                                        <img class="little_img" src="img/falkovich.jpg" alt="some">
                                    </a>
                                    <div class="post_header_info">
                                        <h5 class="post_author">
                                            <a class="user" href="falkovich"><span>Anastasia Falkovich</span></a>
                                        </h5>
                                        <div class="post_date">
                                            <a href="apost_date">
                                                <span class="rel_date">26 mar 2016</span>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="post_header_right">
                                    <div>

                                        <a class="complain" href="complaint">
                                            <span class="icon icon_cross"
                                                  title="If you found bad content, please say to admin about this"></span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="post_inner_content">
                                <div class="answer_text">
                                    <div class="post_description">
												<span>Because ancient humans weren’t morons. Believe it or not,
												a particular merchant in Sumeria 5000 years ago is just as smart
												as the CEO of a fortune 500 company now. That builder in Memphis,
												Egypt knew his craft as well as the engineers who built the Taipei Tower?</span>
                                    </div>
                                </div>
                                <div class="answer_footer">
                                    <div>
                                        <a class="check_best" href="check_best" title="Marked as the best answer">
                                            <span class="icon gold_trophy" style="width: 35px;"></span>
                                        </a>
                                        <a class="repost" href="" title="repost this answer">
                                            <span class="icon icon_undo2" style="width: 35px;"></span>
                                        </a>
                                    </div>
                                    <div class="rate_block fl_r">
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
                                                               class="rate icon icon_star-empty"></span></a>
                                        <a href="rate_9"><span data-descr="your current mark"
                                                               class="rate icon star-full"></span></a>
                                        <a href="rate_10"><span data-descr="set your mark"
                                                                class="rate icon icon_star-empty"></span></a>
                                        <a href="show_all_marks" title="average mark. Click to show all marks">
                                            <span class="rate_number">7.0</span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="answer_block">
                            <div class="answer_header wide_block">

                                <div class="post_header_left">
                                    <a class="post_image" href="/saproncik">
                                        <img class="little_img" src="img/sapronchic.jpg" alt="some">
                                    </a>
                                    <div class="post_header_info">
                                        <h5 class="post_author">
                                            <a class="user" href="sapronchik"><span>Maxim Sapronchik</span></a>
                                        </h5>
                                        <div class="post_date">
                                            <a href="apost_date">
                                                <span class="rel_date">27 mar 2016</span>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="post_header_right">
                                    <div>

                                        <a class="complain" href="complaint">
                                            <span class="icon icon_cross"
                                                  title="If you found bad content, please say to admin about this"></span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="post_inner_content">
                                <div class="answer_text">
                                    <div class="post_description">
												<span>Because ancient humans weren’t morons. Believe it or not,
												a particular merchant in Sumeria 5000 years ago is just as smart
												as the CEO of a fortune 500 company now. That builder in Memphis,
												Egypt knew his craft as well as the engineers who built the Taipei Tower?</span>
                                    </div>
                                </div>
                                <div class="answer_footer">
                                    <div>
                                        <a class="check_best" href="check_best" title="Mark as the best answer">
                                            <span class="icon icon_trophy" style="width: 35px;"></span>
                                        </a>
                                        <a class="repost" href="" title="repost this answer">
                                            <span class="icon icon_undo2" style="width: 35px;"></span>
                                        </a>
                                    </div>
                                    <div class="rate_block fl_r">
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
                                                               class="rate icon icon_star-half"></span></a>
                                        <a href="rate_8"><span data-descr="your current mark"
                                                               class="rate icon star-full"></span></a>
                                        <a href="rate_9"><span data-descr="set your mark"
                                                               class="rate icon icon_star-empty"></span></a>
                                        <a href="rate_10"><span data-descr="set your mark"
                                                                class="rate icon icon_star-empty"></span></a>
                                        <a href="show_all_marks" title="average mark. Click to show all marks">
                                            <span class="rate_number">6.6</span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>


                    </div>
                </div>

                <!-- end of post -->
                <div class="page_block wide_block post_content">
                    <div class="post_header">
                        <div class="post_header_left">
                            <a href="salesnax" class="post_image">
                                <img class="mini_img" src="/img/sapronchic.jpg" alt="some">
                            </a>
                            <div class="post_header_info">
                                <h5 class="post_author">
                                    <a class="user" href="sapronchic"><span>Maxim Sapronchik</span></a>
                                </h5>
                                <div class="post_date">
                                    <a href="apost_date">
                                        <span class="rel_date">20 mar 2016</span>
                                    </a>

                                </div>
                            </div>
                        </div>
                        <div class="post_header_right">
                            <div>
                                <a class="complain" href="complaint">
                                    <span class="icon icon_cross"
                                          title="If you found bad content, please say to admin about this"></span>
                                </a>
                                <a class="delete" href="complaint">
                                    <span class="icon icon_bin"
                                          title="If you found bad content, please say to admin about this"></span>
                                </a>
                            </div>
                            <div class="post_category">
                                <span>Category:</span>
                                <a href="science" title="go to all category questions">History</a>
                            </div>

                        </div>
                    </div>
                    <div class="post_inner_content">
                        <div class="wall_text">
                            <div class="post_q_title">
                                <span>What did hippies from the 60's look like?</span>
                            </div>
                            <div class="post_description">
										<span>What did hippies from the 60's look like?
										What I mean is you tried it, you all tried it,
										and it didn’t work. All these years later and look at the results.</span>
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
                            <a href="rate_9"><span data-descr="set your mark"
                                                   class="rate icon icon_star-half"></span></a>
                            <a href="rate_10"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="rate_10"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="show_all_marks" title="average mark. Click to show all marks">
                                <span class="rate_number">7.6</span>
                            </a>
                        </div>
                    </div>
                    <div class="answers_block">
                        <div class="answers_header">
                            <div class="answers_title">
                                <span>Answers:</span>
                                <span>1</span>
                            </div>
                        </div>
                        <div class="answer_block">
                            <div class="answer_header wide_block">

                                <div class="post_header_left">
                                    <a class="post_image" href="/falkovich">
                                        <img class="little_img" src="/img/falkovich.jpg" alt="some">
                                    </a>
                                    <div class="post_header_info">
                                        <h5 class="post_author">
                                            <a class="user" href="falkovich"><span>Anastasia Falkovich</span></a>
                                        </h5>
                                        <div class="post_date">
                                            <a href="apost_date">
                                                <span class="rel_date">26 mar 2016</span>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="post_header_right">
                                    <div>

                                        <a class="complain" href="complaint">
                                            <span class="icon icon_cross"
                                                  title="If you found bad content, please say to admin about this"></span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="post_inner_content">
                                <div class="answer_text">
                                    <div class="post_description">
												<span>Listen to this with an open mind,” Noel began. “Let me think of the best way to say this.”
												She paused for a minute. “Let’s say that there were a hundred of you who jumped into the movement;
												that is the correct term, right?’ Jay nodded. OK. One hundred of you decided to get involved one hundred percent,
												because you all believed in it completely. That includes your friends from
												Orange, and the guys who played in the bands, and their girlfriends, all of them.” Noel looked at Jay with her
												palms up and lifter her eyebrows in a tacit request for assent. He gave it with another nod.</span>
                                    </div>
                                </div>
                                <div class="answer_footer">
                                    <div>
                                        <a class="check_best" href="check_best" title="Marked as the best answer">
                                            <span class="icon gold_trophy" style="width: 35px;"></span>
                                        </a>
                                        <a class="repost" href="" title="repost this answer">
                                            <span class="icon icon_undo2" style="width: 35px;"></span>
                                        </a>
                                    </div>
                                    <div class="rate_block fl_r">
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
                                                               class="rate icon icon_star-empty"></span></a>
                                        <a href="rate_9"><span data-descr="your current mark"
                                                               class="rate icon star-full"></span></a>
                                        <a href="rate_10"><span data-descr="set your mark"
                                                                class="rate icon icon_star-empty"></span></a>
                                        <a href="show_all_marks" title="average mark. Click to show all marks">
                                            <span class="rate_number">7.0</span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="add_answer_block">
                            <form class="answer_form" action="ask_about" method="post">
                                <textarea class="q_place" rows="3" maxlength="10000"
                                          placeholder="Add another answer here..."></textarea>
                                <input type="hidden" name="command" value="publish_question"/>
                                <input class="q_submit" type="submit" name="publish" value="publish"/>
                            </form>
                        </div>


                    </div>
                </div>
                <!-- end of post -->

                <div class="page_block wide_block post_content">
                    <div class="post_header">
                        <div class="post_header_left">
                            <a href="kiselev" class="post_image">
                                <img class="mini_img" src="/img/kiselev.jpg" alt="some">
                            </a>
                            <div class="post_header_info">
                                <h5 class="post_author">
                                    <a class="user" href="kiselev"><span>Eugeniy Kiselev</span></a>
                                </h5>
                                <div class="post_date">
                                    <a href="apost_date">
                                        <span class="rel_date">20 mar 2016</span>
                                    </a>

                                </div>
                            </div>
                        </div>
                        <div class="post_header_right">
                            <div>
                                <a class="complain" href="complaint">
                                    <span class="icon icon_cross"
                                          title="If you found bad content, please say to admin about this"></span>
                                </a>
                                <a class="delete" href="complaint">
                                    <span class="icon icon_bin"
                                          title="If you found bad content, please say to admin about this"></span>
                                </a>
                            </div>
                            <div class="post_category">
                                <span>Category:</span>
                                <a href="science" title="go to all category questions">Science</a>
                            </div>

                        </div>
                    </div>
                    <div class="post_inner_content">
                        <div class="wall_text">
                            <div class="post_q_title">
                                <span>How do I find my own star that somebody purchased as a birthday present?</span>
                            </div>
                            <div class="post_description">
										<span>How do I find my own star that somebody purchased as a birthday present?
										Your wife did not purchase you a star; she purchased you an entire galaxy!Officially you
										don’t own a galaxy though. Formal catalogues use catalogue names and don’t use the
										alternate naming conventi...</span>
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
                            <a href="rate_9"><span data-descr="set your mark"
                                                   class="rate icon icon_star-half"></span></a>
                            <a href="rate_7"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="rate_10"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="rate_10"><span data-descr="set your mark" class="rate icon icon_star-empty"></span></a>
                            <a href="show_all_marks" title="average mark. Click to show all marks">
                                <span class="rate_number">6.3</span>
                            </a>
                        </div>
                    </div>
                    <div class="answers_block">
                        <div class="answers_header">
                            <div class="answers_title">
                                <span>Answers:</span>
                                <span>1</span>
                            </div>
                        </div>
                        <div class="answer_block">
                            <div class="answer_header wide_block">

                                <div class="post_header_left">
                                    <a class="post_image" href="/malevich">
                                        <img class="little_img" src="/img/malevich.jpg" alt="some">
                                    </a>
                                    <div class="post_header_info">
                                        <h5 class="post_author">
                                            <a class="user" href="alesnax"><span>Aliaksandr Nakhankou</span></a>
                                        </h5>
                                        <div class="post_date">
                                            <a href="apost_date">
                                                <span class="rel_date">22 mar 2016</span>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="post_header_right">
                                    <div>

                                        <a class="correct_post" href="complaint">
                                            <span class="icon icon_pencil" title="choose to correct your post"></span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="post_inner_content">
                                <div class="answer_text">
                                    <div class="post_description">
												<span>Officially you don’t own a galaxy though. Formal catalogues use catalogue names and don’t
												use the alternate naming conventions that involve selling names of stars and other objects
												to people. Additionally, these organizations don’t work with each other as far as I know, so
												people might very well have purchased the same object already from different organizations.
												Nevertheless, I would say it’s a pretty good gift. Whether it’s official or not, this present helps put some things into perspective. For
												example, NGC97 is an elliptical galaxy located at a distance of 210–220 million light-years away in the constellation of Andromeda, and
												your galaxy’s position in the sky is actually fairly close to the Andromeda Galaxy. NGC97 was discovered by John Herschel in 1828; probably
												using his reflecting telescope with a 460 mm (18 inches) mirror and a 6,1 m (20-foot) focal length.</span>
                                    </div>
                                </div>
                                <div class="answer_footer">
                                    <div>
                                        <a class="check_best" href="check_best" title="Marked as the best answer">
                                            <span class="icon gold_trophy" style="width: 35px;"></span>
                                        </a>
                                        <a class="repost" href="" title="repost this answer">
                                            <span class="icon icon_undo2" style="width: 35px;"></span>
                                        </a>
                                    </div>
                                    <div class="rate_block fl_r">
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
                                        <a href="rate_6"><span data-descr="set your mark"
                                                               class="rate icon icon_star-full"></span></a>
                                        <a href="rate_7"><span data-descr="set your mark"
                                                               class="rate icon icon_star-half"></span></a>
                                        <a href="rate_9"><span data-descr="your current mark"
                                                               class="rate icon star-full"></span></a>
                                        <a href="rate_10"><span data-descr="set your mark"
                                                                class="rate icon icon_star-empty"></span></a>
                                        <a href="show_all_marks" title="average mark. Click to show all marks">
                                            <span class="rate_number">7.5</span>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>


                    </div>
                </div>
                <!-- end of post -->

            </div>
        </section>
    </div>
</div>
</body>
</html>