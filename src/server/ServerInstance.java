package server;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.utility.Printer;
import common.core.manager.CommandManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInstance {
    private Invoker invoker;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private Request request;
    private Response response;
    private ServerSocket server;
    private Socket clientSocket;

    public void run() throws IOException, ClassNotFoundException, NotValidArgumentsException {
        Printer printer = new Printer();
        invoker = new Invoker(printer);
        CommandManager commandManager = new CommandManager(invoker);
        Thread consoleThread = new Thread(() -> {
            Scanner commandServer = new Scanner(System.in);
            while (true) {
                printer.print("Для сервера доступна команда exit");
                String commandLine = commandServer.nextLine();
                if ("exit".equals(commandLine)) {
                    printer.print("До свидания!");
                    try {
                        server.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(0);
                }
            }
        });
        consoleThread.start();
        server = new ServerSocket(3231);
        while (true){
        clientSocket = server.accept();
        cachedThreadPool.execute(new ServerConnectionHandler(server, clientSocket, invoker));
        }
    }
}