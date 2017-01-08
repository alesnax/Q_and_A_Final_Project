package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Friend extends ShortUser {
    private FriendState state;
    private String name;
    private String surname;
    private String userStatus;


    public Friend() {
    }

    public FriendState getState() {
        return state;
    }

    public void setState(FriendState state) {
        this.state = state;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Friend friend = (Friend) o;

        if (state != friend.state) return false;
        if (name != null ? !name.equals(friend.name) : friend.name != null) return false;
        if (surname != null ? !surname.equals(friend.surname) : friend.surname != null) return false;
        return userStatus != null ? userStatus.equals(friend.userStatus) : friend.userStatus == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (userStatus != null ? userStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "state=" + state +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userStatus='" + userStatus + '\'' +
                '}';
    }
}
