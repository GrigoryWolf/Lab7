package client;
import common.core.Deserializer;
import common.core.message.Request;
import common.core.message.Response;
import common.core.utility.Printer;
import common.core.manager.ModelManager;
import common.core.reader.IReader;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientInvoker{
    private Printer printer;
    private ClientCLIReader reader;
    private RequestManager requestManager;
    private ClientConnection connection;
    private User user;

    public ClientInvoker(ClientConnection connection, Printer printer){
        this.connection = connection;
        this.printer = printer;
        this.user = null;
        reader = new ClientCLIReader(this);
        requestManager = new RequestManager(this);
    }
    public ModelManager getModelManager() {
        return null;
    }

    public void startReading() throws IOException{
        printer.print("Введите команду help для получения списка доступных команд");
        reader.start();
    }
    public void setUser(User user){
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public Printer getPrinter() {
        return printer;
    }

    public IReader getReader() {
        return reader;
    }
    public ClientConnection getConnection(){return connection;}
    public RequestManager getRequestManager() {
        return requestManager;
    }
}