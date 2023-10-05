package client;

import common.core.utility.Printer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientInstance {
    private InetAddress host;
    private int port;
    private ClientConnection connection;
    public ClientInstance(InetAddress host, int port){
        connection = new ClientConnection(host, port);
    }
    public void run(){
        Printer printer = new Printer();
        try{
            ClientInvoker clientInvoker = new ClientInvoker(connection, printer);
            clientInvoker.startReading();
        } catch (IOException e) {
            printer.print("Попытка переподключения к серверу...");
            tryReconnect();
        }
    }
    public void tryReconnect(){
        Printer printer = new Printer();
        boolean connected = false;
        int counter = 0;
        while (!connected) {
            try {
                connection = new ClientConnection(host, port);
                connected = true;
                counter = 0;
                ClientInvoker clientInvoker = new ClientInvoker(connection, printer);
                clientInvoker.startReading();
            } catch (IOException ex) {
                if(counter>=5){
                    printer.print("Превышено время ожидания ответа!");
                    System.exit(1);
                }
                printer.print("Ошибка подключения! Пробуем подключиться, пожалуйста, ожидайте ответа");
                counter+=1;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    printer.print("Ошибка при приостановке потока!");
                }

            }
        }
    }
}

