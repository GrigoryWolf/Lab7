package client;
import common.core.message.Request;
import common.core.message.Response;
import common.core.utility.Printer;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {
    private Socket socket;
    private InetAddress host;
    private int port;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final int RECONNECTION_DELAY = 5000;
    private final int MAX_RECONNECTION_ATTEMPTS = 5;
    Printer printer = new Printer();
    public ClientConnection(InetAddress host, int port){
        this.host = host;
        this.port = port;
        try{
            connect();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            reconnect();
        }
    }
    public void connect() throws IOException{
        socket = new Socket(host, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        printer.print("Соединение установлено.");
    }
    public void reconnect(){
        int reconnectionAttempt = 0;
        do {
            try {
                if (reconnectionAttempt > 0) {
                    System.out.printf("Пытаюсь переподключиться (попытка %d)\n", reconnectionAttempt);
                    Thread.sleep(RECONNECTION_DELAY);
                }
                connect();
                return;
            } catch (IOException e) {
                printer.print("Ошибка подключения.");
                reconnectionAttempt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (reconnectionAttempt <= MAX_RECONNECTION_ATTEMPTS);
        exit();
        System.exit(0);
    }
    public Response read(){
        try {
            return (Response) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            reconnect();
            return null;
        }
    }
    public void write(Request request){
        try {
            objectOutputStream.writeObject(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void exit(){
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
