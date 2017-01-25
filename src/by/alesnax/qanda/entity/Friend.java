package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Friend extends ShortUser {
    private boolean friend;
    private String name;
    private String surname;
    private String userStatus;
    private int followers;
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

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
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
        if (followers != friend1.followers) return false;
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
        result = 31 * result + followers;
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
                ", followers=" + followers +
                ", userRate=" + userRate +
                "} " + super.toString();
    }
}
