package by.alesnax.qanda.entity;

/**
 * This class represents information about users showed in
 * list of followers or 'following' users
 *
 * @author Aliaksandr Nakhankou
 */
public class Friend extends ShortUser {

    /**
     * showed if user is 'following' for definite 'session' user
     */
    private boolean friend;

    /**
     * name of user
     */
    private String name;

    /**
     * surname of user
     */
    private String surname;

    /**
     * quote or motto of user
     */
    private String userStatus;

    /**
     * average user's rate based on rates for posts by they authority
     */
    private double userRate;

    public Friend() {
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public double getUserRate() {
        return userRate;
    }

    public void setUserRate(double userRate) {
        this.userRate = userRate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Friend friend1 = (Friend) o;

        if (friend != friend1.friend) return false;
        if (Double.compare(friend1.userRate, userRate) != 0) return false;
        if (name != null ? !name.equals(friend1.name) : friend1.name != null) return false;
        if (surname != null ? !surname.equals(friend1.surname) : friend1.surname != null) return false;
        return userStatus != null ? userStatus.equals(friend1.userStatus) : friend1.userStatus == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (friend ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (userStatus != null ? userStatus.hashCode() : 0);
        temp = Double.doubleToLongBits(userRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Friend{" +
                ", friend=" + friend +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", userRate=" + userRate +
                "} " + super.toString();
    }
}
