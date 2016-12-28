package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 20.12.2016.
 */
public class ShortUser extends Entity {
    private int id;
    private Role role;
    private String login;
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortUser shortUser = (ShortUser) o;

        if (id != shortUser.id) return false;
        if (role != shortUser.role) return false;
        if (login != null ? !login.equals(shortUser.login) : shortUser.login != null) return false;
        return avatar != null ? avatar.equals(shortUser.avatar) : shortUser.avatar == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShortUser{" +
                "id=" + id +
                ", role=" + role +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
