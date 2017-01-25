package by.alesnax.qanda.entity;

/**
 * Created by alesnax on 04.01.2017.
 */
public class UserStatistics extends Entity {
    private int followingUsersCount;
    private int followersCount;
    private double rate;
    private int questionsCount;
    private int answersCount;

    public UserStatistics() {
    }

    public int getFollowingUsersCount() {
        return followingUsersCount;
    }

    public void setFollowingUsersCount(int followingUsersCount) {
        this.followingUsersCount = followingUsersCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public int getAnswersCount() {
        return answersCount;
    }

    public void setAnswersCount(int answersCount) {
        this.answersCount = answersCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserStatistics that = (UserStatistics) o;

        if (followingUsersCount != that.followingUsersCount) return false;
        if (followersCount != that.followersCount) return false;
        if (Double.compare(that.rate, rate) != 0) return false;
        if (questionsCount != that.questionsCount) return false;
        return answersCount == that.answersCount;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = followingUsersCount;
        result = 31 * result + followersCount;
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + questionsCount;
        result = 31 * result + answersCount;
        return result;
    }

    @Override
    public String toString() {
        return "UserStatistics{" +
                "followingUsersCount=" + followingUsersCount +
                ", followersCount=" + followersCount +
                ", rate=" + rate +
                ", questionsCount=" + questionsCount +
                ", answersCount=" + answersCount +
                "} " + super.toString();
    }
}
