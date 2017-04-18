package by.alesnax.qanda.tag;

import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * The class {@code TopProfileTag} extends {@code TagSupport} class and implementation of custom user tag,
 * that shows user login and a little avatar at the header of page.
 * Tag is showed when user is authorised.
 *
 * @author Aliaksandr Nakhankou
 * @see javax.servlet.jsp.tagext.TagSupport
 */
@SuppressWarnings("serial")
public class TopProfileTag extends TagSupport {

    /**
     * key of command located in config.properties file
     */
    private final static String GO_TO_PROFILE = "command.go_to_profile";

    /**
     * key of default avatar link located in config.properties file
     */
    private final static String DEFAULT_AVATAR = "img.common.default_avatar";

    /**
     * method takes user's login and avatar from session and writes it as a tag on jsp page
     *
     * @return SKIP_BODY constant
     * @throws JspException if error while writing was occurred.
     */
    @Override
    public int doStartTag() throws JspException {
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute("user");
        if(user ==null){
            return SKIP_BODY;
        }
        ConfigurationManager configurationManager = new ConfigurationManager();
        String goToProfileCommand = configurationManager.getProperty(GO_TO_PROFILE);
        String defaultAvatar = configurationManager.getProperty(DEFAULT_AVATAR);
        String returnedContent = "<a href=\"" + goToProfileCommand + user.getId() + "\" class=\"header_avatar_image\">" +
                "<span class=\"header_login\">" + user.getLogin() + "</span>" +
                "<img class=\"mini_header_avatar_img\" src=\"" + user.getAvatar() + "\" alt=\"avatar\" onerror=\"src='" + defaultAvatar + "'\">" + "</a>";

        try {
            JspWriter out = pageContext.getOut();
            out.write(returnedContent);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    /**
     * method let following page processing
     *
     * @return EVAL_PAGE constant
     * @throws JspException
     */
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}