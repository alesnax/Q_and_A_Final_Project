package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * This class represents information about user's complaints
 *
 * @author Aliaksandr Nakhankou
 */
public class Complaint extends Entity {

    /**
     * post's id that to be cause of complaint
     */
    private int postId;

    /**
     * user who created complaint
     */
    private ShortUser user;

    /**
     * description of complaint
     */
    private String description;

    /**
     * time complaint was published
     */
    private Date publishedTime;

    /**
     * status of processing complaint
     */
    private ComplaintStatus status;

    /**
     * time when complaint was processed
     */
    private Date processedTime;

    /**
     * moderator who processed complaint
     */
    private ShortUser moderator;

    /**
     * decision of moderator after complaint processing
     */
    private String decision;

    public Complaint() {
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public ShortUser getUser() {
        return user;
    }

    public void setUser(ShortUser user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public Date getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Date processedTime) {
        this.processedTime = processedTime;
    }

    public ShortUser getModerator() {
        return moderator;
    }

    public void setModerator(ShortUser moderator) {
        this.moderator = moderator;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public enum ComplaintStatus{
        NEW("new"),
        APPROVED("approved"),
        CANCELLED("cancelled");

        private String status;

        ComplaintStatus(String status) {
            this.status = status;
        }

        public String getValue() {
            return this.status;
        }

        public static ComplaintStatus fromValue(String v) {
            for (ComplaintStatus c : ComplaintStatus.values()) {
                if (c.status.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Complaint complaint = (Complaint) o;

        if (postId != complaint.postId) return false;
        if (user != null ? !user.equals(complaint.user) : complaint.user != null) return false;
        if (description != null ? !description.equals(complaint.description) : complaint.description != null)
            return false;
        if (publishedTime != null ? !publishedTime.equals(complaint.publishedTime) : complaint.publishedTime != null)
            return false;
        if (status != complaint.status) return false;
        if (processedTime != null ? !processedTime.equals(complaint.processedTime) : complaint.processedTime != null)
            return false;
        if (moderator != null ? !moderator.equals(complaint.moderator) : complaint.moderator != null) return false;
        return decision != null ? decision.equals(complaint.decision) : complaint.decision == null;

    }

    @Override
    public int hashCode() {
        int result = postId;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (publishedTime != null ? publishedTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (processedTime != null ? processedTime.hashCode() : 0);
        result = 31 * result + (moderator != null ? moderator.hashCode() : 0);
        result = 31 * result + (decision != null ? decision.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "postId=" + postId +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", publishedTime=" + publishedTime +
                ", status=" + status +
                ", processedTime=" + processedTime +
                ", moderator=" + moderator +
                ", decision='" + decision + '\'' +
                "} " + super.toString();
    }
}
