package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientApp {
    public static void main(String[] args)  {
        ClientInstance clientInstance = null;
        try {
            clientInstance = new ClientInstance(InetAddress.getLocalHost(), 3231);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        clientInstance.run();
    }
}