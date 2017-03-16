package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * This class represents information about user's posts (answers and questions)
 *
 * @author Aliaksandr Nakhankou
 */
public class Post extends Entity {

    /**
     * post's id (unique number)
     */
    private int id;

    /**
     * cut information about category of post
     */
    private CategoryInfo categoryInfo;

    /**
     * type of post
     */
    private PostType type;

    /**
     * post's title (short description)
     */
    private String title;

    /**
     * post's description
     */
    private String content;

    /**
     * id of question for answer, null for questions
     */
    private int parentId;

    /**
     * time of post's publishing
     */
    private Date publishedTime;

    /**
     * status of post (new, modified or deleted)
     */
    private Status status;

    /**
     * time of post's modifying
     */
    private Date modifiedTime;

    /**
     * average rate for post
     */
    private double averageMark;

    /**
     * cut info about post's author
     */
    private ShortUser user;

    /**
     * rate of session user if exists
     */
    private int currentUserMark;

    /**
     * title of question if type of post is 'answer'
     */
    private String parentTitle;

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public ShortUser getUser() {
        return user;
    }

    public void setUser(ShortUser user) {
        this.user = user;
    }

    public int getCurrentUserMark() {
        return currentUserMark;
    }

    public void setCurrentUserMark(int currentUserMark) {
        this.currentUserMark = currentUserMark;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public enum PostType {
        SERVICE("service"),
        QUESTION("question"),
        ANSWER("answer"),
        MESSAGE("message"),
        ADV("adv");

        private String type;

        PostType(String type) {
            this.type = type;
        }

        public String getValue() {
            return this.type;
        }

        public static PostType fromValue(String v) {
            for (PostType c : PostType.values()) {
                if (c.type.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }


    public enum Status {
        NEW("new"),
        DELETED("deleted"),
        MODIFIED("modified");

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getValue() {
            return this.status;
        }

        public static Status fromValue(String v) {
            for (Status c : Status.values()) {
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

        Post post = (Post) o;

        if (id != post.id) return false;
        if (parentId != post.parentId) return false;
        if (Double.compare(post.averageMark, averageMark) != 0) return false;
        if (currentUserMark != post.currentUserMark) return false;
        if (categoryInfo != null ? !categoryInfo.equals(post.categoryInfo) : post.categoryInfo != null) return false;
        if (type != post.type) return false;
        if (title != null ? !title.equals(post.title) : post.title != null) return false;
        if (content != null ? !content.equals(post.content) : post.content != null) return false;
        if (publishedTime != null ? !publishedTime.equals(post.publishedTime) : post.publishedTime != null)
            return false;
        if (status != post.status) return false;
        if (modifiedTime != null ? !modifiedTime.equals(post.modifiedTime) : post.modifiedTime != null) return false;
        if (user != null ? !user.equals(post.user) : post.user != null) return false;
        return parentTitle != null ? parentTitle.equals(post.parentTitle) : post.parentTitle == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (categoryInfo != null ? categoryInfo.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + parentId;
        result = 31 * result + (publishedTime != null ? publishedTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (modifiedTime != null ? modifiedTime.hashCode() : 0);
        temp = Double.doubleToLongBits(averageMark);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + currentUserMark;
        result = 31 * result + (parentTitle != null ? parentTitle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", categoryInfo=" + categoryInfo +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", parentId=" + parentId +
                ", publishedTime=" + publishedTime +
                ", status=" + status +
                ", modifiedTime=" + modifiedTime +
                ", averageMark=" + averageMark +
                ", user=" + user +
                ", currentUserMark=" + currentUserMark +
                ", parentTitle='" + parentTitle + '\'' +
                "} " + super.toString();
    }
}
