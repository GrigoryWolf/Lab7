package common.core.manager;

import java.io.IOException;
import java.util.*;

import common.commands.*;
import common.commands.Command;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;

/**
 * Класс, который управляет вызовом команд
 * @author grigoryvolkov
 */
public class CommandManager {
    private Map<String, Command> commandsCollection;
    private final Invoker invoker;
    public CommandManager(Invoker invoker){
        this.invoker = invoker;
        initializeCommands();
    }
    private void initializeCommands(){
        commandsCollection = new HashMap<>();
        commandsCollection.put("login", new LoginCommand(invoker));
        commandsCollection.put("register", new RegisterCommand(invoker));
        commandsCollection.put("help", new CommandHelp(invoker));
        commandsCollection.put("info", new InfoCommand(invoker));
        commandsCollection.put("show", new ShowCommand(invoker));
        commandsCollection.put("insert", new InsertCommand(invoker));
        commandsCollection.put("update", new UpdateCommand(invoker));
        commandsCollection.put("clear", new ClearCommand(invoker));
        commandsCollection.put("remove_key", new RemoveByKeyCommand(invoker));
        commandsCollection.put("exit", new ExitCommand(invoker));
        commandsCollection.put("history", new CommandHistory(invoker));
        commandsCollection.put("replace_if_greater", new ReplaceIfGreaterCommand(invoker));
        commandsCollection.put("remove_lower_key", new RemoveLowerKeyCommand(invoker));
        commandsCollection.put("group_counting_by_name", new GroupContingByNameCommand(invoker));
        commandsCollection.put("count_by_distance", new CountByDistanceCommand(invoker));
        commandsCollection.put("print_field_descending_distance", new PrintFieldDescendingDistanceCommand(invoker));
    }
    public Response callCommand(Request request){
        try {
            Command command = commandsCollection.get(request.getCommand());
            return command.execute(request);
        }
        catch (NotValidArgumentsException | IOException | NullPointerException ex){
            invoker.getPrinter().print("Задача выполнена без отправки результата клиенту");
        }
        return new Response("Не удалось выполнить команду");

    }
    public Collection<Command> getCommands(){
        return commandsCollection.values();
    }

}
