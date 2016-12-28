package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Category extends CategoryInfo {
    private int userId;
    private Date creationDate;
    private String descriptionEn;
    private String descriptionRu;
    private CategoryStatus status;
    private ShortUser moderator;
    private int questionQuantity;
    private String imageLink;

    public Category() {
        super();
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public CategoryStatus getStatus() {
        return status;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    public ShortUser getModerator() {
        return moderator;
    }

    public void setModerator(ShortUser moderator) {
        this.moderator = moderator;
    }

    public int getQuestionQuantity() {
        return questionQuantity;
    }

    public void setQuestionQuantity(int questionQuantity) {
        this.questionQuantity = questionQuantity;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public enum CategoryStatus {
        NEW("new"),
        HOT("hot"),
        OLD("old"),
        CLOSED("closed");

        private String status;

        CategoryStatus(String status){
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public static CategoryStatus fromValue(String v) {
            for (CategoryStatus c : CategoryStatus.values()) {
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
        if (!super.equals(o)) return false;

        Category category = (Category) o;

        if (userId != category.userId) return false;
        if (questionQuantity != category.questionQuantity) return false;
        if (creationDate != null ? !creationDate.equals(category.creationDate) : category.creationDate != null)
            return false;
        if (descriptionEn != null ? !descriptionEn.equals(category.descriptionEn) : category.descriptionEn != null)
            return false;
        if (descriptionRu != null ? !descriptionRu.equals(category.descriptionRu) : category.descriptionRu != null)
            return false;
        if (status != category.status) return false;
        if (moderator != null ? !moderator.equals(category.moderator) : category.moderator != null) return false;
        return imageLink != null ? imageLink.equals(category.imageLink) : category.imageLink == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + userId;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (descriptionEn != null ? descriptionEn.hashCode() : 0);
        result = 31 * result + (descriptionRu != null ? descriptionRu.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (moderator != null ? moderator.hashCode() : 0);
        result = 31 * result + questionQuantity;
        result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "userId=" + userId +
                ", creationDate=" + creationDate +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", descriptionRu='" + descriptionRu + '\'' +
                ", status=" + status +
                ", moderator=" + moderator +
                ", questionQuantity=" + questionQuantity +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
