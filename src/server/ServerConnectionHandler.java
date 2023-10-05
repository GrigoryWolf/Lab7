package server;
import client.User;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnectionHandler extends Thread{
    private ServerSocket server;
    private Socket clientSocket;
    private Invoker invoker;
    private ExecutorService cachedPool = Executors.newCachedThreadPool();


    public ServerConnectionHandler(ServerSocket server, Socket clientSocket, Invoker invoker) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        Request request;
        Response response;
        try {
            ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream serverOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            while (true) {
                request = (Request) clientInput.readObject();
                System.out.println(request);
                response = invoker.getCommandManager().callCommand(request);
                Response finalResponse = response;
                cachedPool.execute(() -> {
                    try {
                        serverOutput.writeObject(finalResponse);
                        serverOutput.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException exception) {
            System.out.printf("Разрыв соединения с клиентом %s:%s\n", clientSocket.getInetAddress().toString(), clientSocket.getPort());
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
