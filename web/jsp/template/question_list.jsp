<%--
  Created by IntelliJ IDEA.
  User: alesnax
  Date: 18.12.2016
  Time: 10:58
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

</head>
<body>


<div class="wall_content wide_block">


    <div class="page_block wide_block post_content">
        <div class="post_header">
            <div class="post_header_left">
                <a href="alesnax" class="post_image">
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

</div>





</body>
</html>
