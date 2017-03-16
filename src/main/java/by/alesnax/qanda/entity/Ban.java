package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * This class represents information about user's ban.
 *
 * @author Aliaksandr Nakhankou
 */
public class Ban extends Entity {

    /**
     * ban's id number
     */
    private int id;

    /**
     * post's id that has content for what user was banned
     */
    private int postId;

    /**
     * cause user was banned for
     */
    private String cause;

    /**
     * Date of ban's start
     */
    private Date start;

    /**
     * Date of ban's end
     */
    private Date end;

    /**
     * user who was banned
     */
    private ShortUser user;

    /**
     * user who created ban
     */
    private ShortUser moderator;


    public Ban() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public ShortUser getUser() {
        return user;
    }

    public void setUser(ShortUser user) {
        this.user = user;
    }

    public ShortUser getModerator() {
        return moderator;
    }

    public void setModerator(ShortUser moderator) {
        this.moderator = moderator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ban ban = (Ban) o;

        if (id != ban.id) return false;
        if (postId != ban.postId) return false;
        if (cause != null ? !cause.equals(ban.cause) : ban.cause != null) return false;
        if (start != null ? !start.equals(ban.start) : ban.start != null) return false;
        if (end != null ? !end.equals(ban.end) : ban.end != null) return false;
        if (user != null ? !user.equals(ban.user) : ban.user != null) return false;
        return moderator != null ? moderator.equals(ban.moderator) : ban.moderator == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + postId;
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (moderator != null ? moderator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ban{" +
                "id=" + id +
                ", postId=" + postId +
                ", cause='" + cause + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", user=" + user +
                ", moderator=" + moderator +
                "} " + super.toString();
    }
}