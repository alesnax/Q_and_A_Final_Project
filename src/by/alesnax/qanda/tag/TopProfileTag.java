package by.alesnax.qanda.tag;

import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings("serial")
public class TopProfileTag extends TagSupport {
    private final static String GO_TO_PROFILE = "command.go_to_profile";


    @Override
    public int doStartTag() throws JspException {
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute("user");
        if(user ==null){
            return SKIP_BODY;
        }
        ConfigurationManager configurationManager = new ConfigurationManager();
        String goToProfileCommand = configurationManager.getProperty(GO_TO_PROFILE);
        String returnedContent = "<a href=\"" + goToProfileCommand + user.getId() + "\" class=\"header_avatar_image\">" +
                "<span class=\"header_login\">" + user.getLogin() + "</span>" +
                "<img class=\"mini_header_avatar_img\" src=\"" + user.getAvatar() + "\" alt=\"avatar\">" + "</a>";



        try {
            JspWriter out = pageContext.getOut();
            out.write(returnedContent);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}