package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

/**
 * Класс осуществляющий реализацию команды clear
 * @author grigoryvolkov
 */
public class ClearCommand extends Command{
    public ClearCommand(Invoker invoker){super(invoker);}

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.NOARGUMENT, request);
        if(getInvoker().getModelManager().clearCollection(request)) return new Response("Коллекция успешно очищена");
        return new Response("Не удалось очистить коллекцию");
    }

    @Override
    public String getDescription() {
        return "clear : очистить коллекцию";
    }
}
