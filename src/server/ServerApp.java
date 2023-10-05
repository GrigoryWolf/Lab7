package server;
import common.core.Exceptions.NotValidArgumentsException;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            ServerInstance serverInstance = new ServerInstance();
            serverInstance.run();
        }
        catch(IOException | ClassNotFoundException | NotValidArgumentsException e) {
            throw new RuntimeException(e);
        }
    }
}
