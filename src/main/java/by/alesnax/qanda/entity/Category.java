package by.alesnax.qanda.entity;

import java.util.Date;

/**
 * This class represents full information about category
 *
 * @author Aliaksandr Nakhankou
 */
public class Category extends CategoryInfo {

    /**
     * date when category was created
     */
    private Date creationDate;

    /**
     * english description of category
     */
    private String descriptionEn;

    /**
     * russian description of category
     */
    private String descriptionRu;

    /**
     * information about user who moderates this category
     */
    private ShortUser moderator;

    /**
     * number of questions category contains
     */
    private int questionQuantity;

    /**
     * path to image of category
     */
    private String imageLink;

    public Category() {
        super();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Category category = (Category) o;

        if (questionQuantity != category.questionQuantity) return false;
        if (creationDate != null ? !creationDate.equals(category.creationDate) : category.creationDate != null)
            return false;
        if (descriptionEn != null ? !descriptionEn.equals(category.descriptionEn) : category.descriptionEn != null)
            return false;
        if (descriptionRu != null ? !descriptionRu.equals(category.descriptionRu) : category.descriptionRu != null)
            return false;
        if (moderator != null ? !moderator.equals(category.moderator) : category.moderator != null) return false;
        return imageLink != null ? imageLink.equals(category.imageLink) : category.imageLink == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (descriptionEn != null ? descriptionEn.hashCode() : 0);
        result = 31 * result + (descriptionRu != null ? descriptionRu.hashCode() : 0);
        result = 31 * result + (moderator != null ? moderator.hashCode() : 0);
        result = 31 * result + questionQuantity;
        result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                ", creationDate=" + creationDate +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", descriptionRu='" + descriptionRu + '\'' +
                ", moderator=" + moderator +
                ", questionQuantity=" + questionQuantity +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
