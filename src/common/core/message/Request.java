package common.core.message;
import client.User;
import common.core.model.Route;
import java.io.Serializable;

public class Request implements Serializable{
    private String command;
    private String arg;
    private Route route;
    private User user;
    public Request(String command, String arg, Route route, User user) {
        this.command = command;
        this.arg = arg;
        this.route = route;
        this.user = user;
    }

    public String getCommand() {
        return command;
    }
    public String getArg(){
        return arg;
    }
    public Route getRoute(){return route;}
    public User getUser(){
        return user;
    }
}
