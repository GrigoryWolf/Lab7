package client;
import common.core.message.Response;
import common.core.reader.IReader;
import common.core.manager.CommandManager;

import java.io.IOException;
import java.util.Scanner;

public class ClientCLIReader implements IReader {
    private Boolean isWorking = true;
    private RequestManager requestManager;
    private Scanner scanner;
    private ClientInvoker clientInvoker;
    public ClientCLIReader(ClientInvoker clientInvoker){
        this.clientInvoker = clientInvoker;
    }
    @Override
    public void start() throws IOException {
        scanner = new Scanner(System.in);
        requestManager = new RequestManager(clientInvoker);
        clientInvoker.getPrinter().print("Клиент не авторизован! Введите \"register\" для создания нового профиля, \"login\" для входа в уже существующий.");
        while(true){
            String line = nextLine();
            if (line == null) {
                System.exit(0);
                return;
            }
            try {
                requestManager.parseLine(line);
            }
            catch (ClassNotFoundException exception){
                clientInvoker.getPrinter().print("Не удалось получить ответ от сервера");
            }
        }
    }
    public void stop(){
        isWorking = false;
    }
    @Override
    public String nextLine() {
        System.out.printf(">");
        if (scanner.hasNextLine()){return scanner.nextLine();}
        stop();
        return null;

    }
    public Boolean getWorking(){
        return isWorking;
    }

    @Override
    public CommandManager getCommandManager() {
        return null;
    }
    public RequestManager getRequestManager() {
        return requestManager;
    }

}
