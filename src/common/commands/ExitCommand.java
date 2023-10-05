package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

/**
 * Класс осуществляющий реализацию команды exit
 * @author grigoryvolkov
 */
public class ExitCommand extends Command {
    public ExitCommand(Invoker invoker){
        super(invoker);
    }
    public Response execute(Request request) throws NotValidArgumentsException {return null;}

    @Override
    public String getDescription() {
        return "exit : завершить программу (без сохранения в файл)";
    }
}
