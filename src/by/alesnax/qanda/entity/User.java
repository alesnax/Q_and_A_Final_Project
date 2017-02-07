package by.alesnax.qanda.entity;


import java.util.Date;

/**
 * This class represents full information about users.
 *
 * @author Aliaksandr Nakhankou
 */
public class User extends ShortUser  {

    /**
     * first name of user
     */
    private String name;

    /**
     * user's surname
     */
    private String surname;

    /**
     * password
     */
    private String password;

    /**
     * email, used for site entering
     */
    private String email;

    /**
     * user's birthday
     */
    private Date birthday;

    /**
     * user's gender
     */
    private boolean sex;

    /**
     * date of user's registration
     */
    private Date registrationDate;

    /**
     * user's account state('active' or 'deleted')
     */
    private UserState state;

    /**
     * country where user lives
     */
    private String country;

    /**
     * user's hometown
     */
    private String city;

    /**
     * quote or motto
     */
    private String status;

    /**
     * language used while session
     */
    private Language language;

    /**
     * Type of key word  used for password recovering
     */
    private KeyWord keyWord;

    /**
     * Key word used for password recovering
     */
    private String keyWordValue;

    /**
     * shows if user is 'following' for current session user
     */
    private boolean friend;

    /**
     * user's statistics
     */
    private UserStatistics statistics;

    /**
     * shows if user currently banned
     */
    private boolean banned;

    public User() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public KeyWord getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(KeyWord keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWordValue() {
        return keyWordValue;
    }

    public void setKeyWordValue(String keyWordValue) {
        this.keyWordValue = keyWordValue;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public UserStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(UserStatistics statistics) {
        this.statistics = statistics;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public enum UserState {
        ACTIVE("active"),
        DELETED("deleted");

        private String state;

        UserState(String state) {
            this.state = state;
        }
    }

    public enum Language{
        RU,
        EN,
        NONE
    }

    public enum KeyWord{
        MOTHERS_MAIDEN_NAME(1),
        FIRST_PET_NICKNAME(2),
        PASSPORT_NUMBER(3),
        CODEWORD(4);

        private int keyWord;

        private KeyWord(int keyWord){
            this.keyWord = keyWord;
        }

        public int getValue() {
            return this.keyWord;
        }

        public static KeyWord fromValue(int v) {
            for (KeyWord c : KeyWord.values()) {
                if (c.keyWord == v) {
                    return c;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (sex != user.sex) return false;
        if (banned != user.banned) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (surname != null ? !surname.equals(user.surname) : user.surname != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (birthday != null ? !birthday.equals(user.birthday) : user.birthday != null) return false;
        if (registrationDate != null ? !registrationDate.equals(user.registrationDate) : user.registrationDate != null)
            return false;
        if (state != user.state) return false;
        if (country != null ? !country.equals(user.country) : user.country != null) return false;
        if (city != null ? !city.equals(user.city) : user.city != null) return false;
        if (status != null ? !status.equals(user.status) : user.status != null) return false;
        if (language != user.language) return false;
        return statistics != null ? statistics.equals(user.statistics) : user.statistics == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (sex ? 1 : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (statistics != null ? statistics.hashCode() : 0);
        result = 31 * result + (banned ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", sex=" + sex +
                ", registrationDate=" + registrationDate +
                ", state=" + state +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                ", language=" + language +
                ", statistics=" + statistics +
                ", banned=" + banned +
                "} " + super.toString();
    }
}
