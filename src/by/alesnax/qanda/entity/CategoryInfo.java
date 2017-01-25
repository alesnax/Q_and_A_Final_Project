package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 17.12.2016.
 */
public class CategoryInfo extends Entity {
    private int id;
    private String titleEn;
    private String titleRu;
    private int userId;

    public CategoryInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryInfo that = (CategoryInfo) o;

        if (id != that.id) return false;
        if (titleEn != null ? !titleEn.equals(that.titleEn) : that.titleEn != null) return false;
        return titleRu != null ? titleRu.equals(that.titleRu) : that.titleRu == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (titleEn != null ? titleEn.hashCode() : 0);
        result = 31 * result + (titleRu != null ? titleRu.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryInfo{" +
                "id=" + id +
                ", titleEn='" + titleEn + '\'' +
                ", titleRu='" + titleRu + '\'' +
                '}';
    }
}
