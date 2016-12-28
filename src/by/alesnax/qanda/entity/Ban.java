package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Ban extends Entity {
    private int id;
    private int userId;
    private int adminId;
    private int postId;
    private String cause;
    private Date start;
    private Date end;

    public Ban() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ban ban = (Ban) o;

        if (id != ban.id) return false;
        if (userId != ban.userId) return false;
        if (adminId != ban.adminId) return false;
        if (postId != ban.postId) return false;
        if (cause != null ? !cause.equals(ban.cause) : ban.cause != null) return false;
        if (start != null ? !start.equals(ban.start) : ban.start != null) return false;
        return end != null ? end.equals(ban.end) : ban.end == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + adminId;
        result = 31 * result + postId;
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
