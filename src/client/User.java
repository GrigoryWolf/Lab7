package client;

import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String password;
    private boolean status;
    public User(String login, String password){
        this.login = login;
        this.password = password;
    }
    public String getUserPassword() {
        return password;
    }
    public String getUserLogin() {
        return login;
    }
}
