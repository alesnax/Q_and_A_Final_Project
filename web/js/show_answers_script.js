function show_answers(event, hasAnswers) {
    var answerTitleHiddenLabel = document.getElementById("showed_label_" + event),
        answerTitleShowedLabel = document.getElementById("hide_answers_label_" + +event),
        answersBlock = document.getElementById("answers_blocks_" + event);

    if (hasAnswers) {
        if (answerTitleHiddenLabel.className == "hidden_title") {
            answerTitleHiddenLabel.classList.remove("hidden_title");
            answerTitleShowedLabel.className = "hidden_title";
            answersBlock.className = "hide_messages_status";
        } else {
            answerTitleShowedLabel.classList.remove("hidden_title");
            answerTitleHiddenLabel.className = "hidden_title";
            answersBlock.classList.remove("hide_messages_status");
        }

    }


};