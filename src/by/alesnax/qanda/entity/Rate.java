package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Rate extends Entity{
    private int userId;
    private int postId;
    private int value;
    private Date addingTime;

    public Rate() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getAddingTime() {
        return addingTime;
    }

    public void setAddingTime(Date addingTime) {
        this.addingTime = addingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rate rate = (Rate) o;

        if (userId != rate.userId) return false;
        if (postId != rate.postId) return false;
        if (value != rate.value) return false;
        return addingTime != null ? addingTime.equals(rate.addingTime) : rate.addingTime == null;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + postId;
        result = 31 * result + value;
        result = 31 * result + (addingTime != null ? addingTime.hashCode() : 0);
        return result;
    }
}
