package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 05.12.2016.
 */
public class Friend extends Entity {
    private int userId;
    private int friendId;
    private FriendState state;
    private FriendStatus status;

    public Friend() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public FriendState getState() {
        return state;
    }

    public void setState(FriendState state) {
        this.state = state;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    enum FriendState {
        REQUEST("request"),
        FRIEND("friend"),
        SUBSCRIBER("subscriber"),
        BANNED("banned");

        private String state;

        FriendState(String state) {
            this.state = state;
        }
    }

    enum FriendStatus {
        BEST_FRIEND("best friend"),
        COLLEAGUE("colleague"),
        RELATIVE("relative"),
        WIFE("wife"),
        HUSBAND("husband"),
        OTHER("other");

        private String status;

        FriendStatus(String status) {
            this.status = status;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Friend friend = (Friend) o;

        if (userId != friend.userId) return false;
        if (friendId != friend.friendId) return false;
        if (state != friend.state) return false;
        return status == friend.status;

    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + friendId;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
