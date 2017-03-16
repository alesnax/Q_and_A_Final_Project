package by.alesnax.qanda.entity;

/**
 * Class represents cut information about Category of questions
 *
 * @author Aliaksandr Nakhankou
 */
public class CategoryInfo extends Entity {

    /**
     * category's id number
     */
    private int id;

    /**
     * english title of category
     */
    private String titleEn;

    /**
     * russian title of category
     */
    private String titleRu;

    /**
     * id of user who moderates category
     */
    private int userId;

    /**
     * status of category
     */
    private CategoryStatus status;

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

    public CategoryStatus getStatus() {
        return status;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    /**
     * status of category
     */
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

        CategoryInfo that = (CategoryInfo) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (titleEn != null ? !titleEn.equals(that.titleEn) : that.titleEn != null) return false;
        if (titleRu != null ? !titleRu.equals(that.titleRu) : that.titleRu != null) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (titleEn != null ? titleEn.hashCode() : 0);
        result = 31 * result + (titleRu != null ? titleRu.hashCode() : 0);
        result = 31 * result + userId;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryInfo{" +
                "id=" + id +
                ", titleEn='" + titleEn + '\'' +
                ", titleRu='" + titleRu + '\'' +
                ", userId=" + userId +
                ", status=" + status +
                "} " + super.toString();
    }
}
