package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

/**
 * Класс осуществляющий реализацию команды history
 * @author grigoryvolkov
 */
public class CommandHistory extends Command{
    public CommandHistory(Invoker invoker){
        super(invoker);
    }
    public Response execute(Request request) throws NotValidArgumentsException {return null;}

    @Override
    public String getDescription() {
        return "history : вывести последние 13 команд (без их аргументов)";
    }
}

