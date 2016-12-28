package by.alesnax.qanda.entity;

public enum Role {
    ADMIN("admin"),
    MODERATOR("moderator"),
    USER("user");

    private String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static Role fromValue(String v) {
        for (Role c : Role.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}