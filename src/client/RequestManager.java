package client;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Exceptions.RecursionException;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Coordinates;
import common.core.model.Location;
import common.core.model.Route;
import java.io.Console;
import java.io.IOException;
import java.util.*;

import common.core.reader.IReader;
import common.core.utility.Printer;

import static java.lang.Integer.parseInt;

public class RequestManager{
    private ClientInvoker clientInvoker;

    private LinkedList<String> commandHistory = new LinkedList<>();
    private HashMap<String, Boolean> commandsCollection;
    private User user;
    private Printer printer;
    private static LinkedList<IReader> readersQueue = new LinkedList<>();
    private static boolean recursionFlag = false;
    private ReaderFiles reader;
    private static LinkedList<String> pathChain = new LinkedList<>();
    private static final String pattern = "/(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,}/g";
    public RequestManager(ClientInvoker clientInvoker){
        this.clientInvoker=clientInvoker;
        printer = clientInvoker.getPrinter();
        initializeCommands();
    }
    private void initializeCommands(){
        commandsCollection = new HashMap<String, Boolean>();
        commandsCollection.put("login", false);
        commandsCollection.put("register", false);
        commandsCollection.put("logout", false);
        commandsCollection.put("help", false);
        commandsCollection.put("info", false);
        commandsCollection.put("show", false);
        commandsCollection.put("insert", false);
        commandsCollection.put("update", true);
        commandsCollection.put("clear", false);
        commandsCollection.put("remove_key", true);
        commandsCollection.put("exit", false);
        commandsCollection.put("history", false);
        commandsCollection.put("replace_if_greater", false);
        commandsCollection.put("remove_lower_key", true);
        commandsCollection.put("group_counting_by_name", true);
        commandsCollection.put("count_by_distance", true);
        commandsCollection.put("print_field_descending_distance", false);
    }
    public Request buildRequest(String command, String arg, Route route, User user){
        return new Request(command, arg, route, user);
    }
    public void parseLine(String line) throws IOException, ClassNotFoundException {
        String[] commandLine;
        line = line.trim();
        if(line.equals("")) return;
        if (line.contains(" ")){
            commandLine = line.split(" ",2);
        }
        else{
            commandLine = new String[]{line.trim(), null};
        }
        callCommand(commandLine);

    }
    public void callCommand(String[] commandLine) throws IOException, ClassNotFoundException {
        String command = commandLine[0].trim();
        Response response = null;
        if(!commandsCollection.containsKey(command)) {
            printer.print("Введите help, чтобы узнать о командах");
            return;}
        if (commandsCollection.get(command) == (commandLine[1] == null)) {
            printer.print("Неверное число аргументов");
            return;}
        if (clientInvoker.getUser() == null){
            switch (command){
                case "login", "register" -> {
                    registration();
                    clientInvoker.getConnection().write(buildRequest(command, null,null, clientInvoker.getUser()));
                    response = clientInvoker.getConnection().read();
                    if(!response.getResult()){clientInvoker.setUser(null);}
                    printer.print(response.toString());
                    return;
                }
                case "exit" -> {
                    printer.print("До свидания!");
                    System.exit(0);
                }
                default -> {
                    printer.print("Вы не авторизированный пользователь! \n" +
                        "Для вас доступны команды:\n" +
                        "login: авторизировать пользователя\n" +
                        "register: зарегистрировать пользователя\n" +
                        "exit: выйти из программы");
                        return;
                }
            }
        }
        switch(command){
            case "logout" -> {clientInvoker.setUser(null);
            return;}
            case "insert", "replace_if_greater" ->{
                Route route = new Route();
                if (!setRoute(route)){
                    printer.print("Не удалось создать маршрут");
                    return;
                }
                clientInvoker.getConnection().write(buildRequest(command, null, route, clientInvoker.getUser()));
                response = clientInvoker.getConnection().read();
            }
            case "update" ->{
                clientInvoker.getConnection().write(buildRequest(command, commandLine[1], null, clientInvoker.getUser()));
                response = clientInvoker.getConnection().read();
                clientInvoker.getPrinter().print(response.toString());
                if(!response.getResult()) return;
                Integer id = parseInt(commandLine[1]);
                Route route = new Route();
                if(!setRoute(route)) return;
                try {
                    route.setId(id);
                    route.setUserLogin(clientInvoker.getUser().getUserLogin());
                } catch (NotValidArgumentsException e) {
                    printer.print("Не удалось создать маршрут с таким id");
                    return;
                }
                clientInvoker.getConnection().write(buildRequest(command, null, route, clientInvoker.getUser()));
                response = clientInvoker.getConnection().read();
            }
            case "remove_lower_key", "group_counting_by_name", "count_by_distance", "remove_key" -> {
                clientInvoker.getConnection().write(buildRequest(command, commandLine[1], null, clientInvoker.getUser()));
                response = clientInvoker.getConnection().read();
            }
            case "history" -> printer.print(getHistory());
            case "execute" ->{String file = commandLine[1].trim();
                try {
                    if (recursionCheck(file)){
                        reader = new ReaderFiles(file, clientInvoker);
                        readersQueue.add(reader);
                        reader.start();
                        pathChain.remove(file);
                    }
                    else{
                        for(IReader reader : readersQueue){
                            reader.stop();
                        }
                        throw new RecursionException("Рекурсия! Execute вызывает тот же файл внутри себя");
                    }
                    printer.print("Команды из файла успешно выполнены");
                }
                catch (RecursionException ex){
                    printer.print(ex.getMessage());
                }
            }
            default -> {
                clientInvoker.getConnection().write(buildRequest(command, null, null, clientInvoker.getUser()));
                response = clientInvoker.getConnection().read();
            }
        }
        if (response != null) printer.print(response.toString());
        rememberCommand(command);
    }

    public void registration(){
        IReader reader = clientInvoker.getReader();
        String login = "";
        do {
            printer.print("Введите логин: ");
            login = reader.nextLine().strip();
        } while (login.isEmpty());
        String password = "";
        Console console = System.console();
        do {
            printer.print("Введите пароль: ");
            if (console != null) {
                char[] symbols = console.readPassword();
                if (symbols == null) continue;
                password = String.valueOf(symbols);
            }
            else password = reader.nextLine();
        } while (password.isEmpty());
        clientInvoker.setUser(new User(login, password));
    }
    private void rememberCommand(String command){
        if (commandHistory.size() == 13){
            commandHistory.remove(0);
        }
        commandHistory.add(command);
    }
    public String getHistory(){
        StringBuilder answer = new StringBuilder("Последние 13 введенных команд:\n");
        for(String command : commandHistory){
            if(command != null) answer.append(command).append(" \n");
        }
        return answer.toString();
    }

    public java.sql.Date generateDate(){
        return new java.sql.Date(System.currentTimeMillis());
    }
    public String requestName(String message){
        while(clientInvoker.getReader().getWorking()){
            try{
                clientInvoker.getPrinter().print(message);
                String name = clientInvoker.getReader().nextLine().trim();
                nameCheck(name);
                return name;
            }
            catch (NotValidArgumentsException ex){
                clientInvoker.getPrinter().print(ex.getMessage());
            }
        }
        return null;
    }
    public Long requestCoordinate(String message){
        while(clientInvoker.getReader().getWorking()){
            try{
                clientInvoker.getPrinter().print(message);
                String stringCoordinate = clientInvoker.getReader().nextLine().trim();
                nullCheck(stringCoordinate);
                return Long.parseLong(stringCoordinate);
            }
            catch (NotValidArgumentsException | NumberFormatException ex){
                clientInvoker.getPrinter().print("Координата должна быть Long и не null");
            }
        }
        return null;
    }
    public Double requestDistance(){
        while(clientInvoker.getReader().getWorking()){
            try{
                clientInvoker.getPrinter().print("Введите дистанцию маршрута в формате Double больше 1: ");
                String stringDistance = clientInvoker.getReader().nextLine().trim().replace(",",".");
                nullCheck(stringDistance);
                if (Double.parseDouble(stringDistance)>1){
                    return Double.parseDouble(stringDistance);
                }
            }
            catch (NotValidArgumentsException | NumberFormatException ex){
                clientInvoker.getPrinter().print("distance должен быть double и не null");
            }
        }
        return null;
    }
    public Coordinates requestCoordinates(String message) throws NotValidArgumentsException {
        long x;
        Long y;
        clientInvoker.getPrinter().print(message);
        do {
            x = requestCoordinate("Введите координату x в формате long." +
                    " Значение поля должно быть больше -866: ");
        } while (clientInvoker.getReader().getWorking() && x <= -866);
        do {
            y = requestCoordinate("Введите координату y в формате Long." +
                    " Максимальное значение поля 107, Поле не может быть null: ");
        } while (clientInvoker.getReader().getWorking() && y > 107);
        return new Coordinates(x, y);
    }
    public Location requestLocation(String message) throws NotValidArgumentsException {
        clientInvoker.getPrinter().print(message);
        long x = requestCoordinate("Введите координату x в формате long: ");
        long y = requestCoordinate("Введите координату y в формате long: ");
        Long z = requestCoordinate("Введите координату z в формате Long." +
                " Поле не может быть null: ");
        String name = requestName("Введите название Локации." +
                " Название Локации не может быть null: ");
        return new Location(x, y, z, name);
    }
    public boolean setRoute(Route route){
        try{
            route.setName(requestName("Введите название маршрута. " +
                    "Название не может быть пустым или null: "));
            route.setCoordinates(requestCoordinates("Введите координаты маршрута: "));
            route.setCreationDate((java.sql.Date) generateDate());
            route.setFrom(requestLocation("Введите данные для локации " +
                    "из которой идет маршрут: "));
            route.setTo(requestLocation("Введите данные для локации " +
                    "в которую идет маршрут: "));
            route.setDistance(requestDistance());
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    public void nameCheck(String ... names) throws NotValidArgumentsException {
        for(String name : names){
            if(name == null || name.equals("")){
                throw new NotValidArgumentsException("Аргумент" +
                        " не может быть null");}
        }
    }
    public void nullCheck(Object ... objects) throws NotValidArgumentsException {
        for(Object object : objects){if(object == null){
            throw new NotValidArgumentsException("Аргумент" +
                    " не может быть null");}
        }
    }
    private boolean recursionCheck(String filePath){
        if (pathChain.contains(filePath)){
            return false;
        }
        pathChain.add(filePath);
        return true;
    }
}
