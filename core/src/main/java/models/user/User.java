package models.user;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            User user = (User) obj;
            return username.equals(user.username) && password.equals(user.password);
        } catch (ClassCastException e) {
        }
        return false;
    }
}
