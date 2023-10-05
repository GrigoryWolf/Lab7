package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
/**
 * Класс осуществляющий реализацию команды replace_if_greater
 * @author grigoryvolkov
 */
public class ReplaceIfGreaterCommand extends Command{
    public ReplaceIfGreaterCommand(Invoker invoker){
        super(invoker);
    }
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.BOTH, request);
        if(getInvoker().getModelManager().replaceIfGreater(request)) return new Response("Объект был успешно заменен");
        return new Response("Объект не был заменен");
    }

    @Override
    public String getDescription() {
        return "replace_if_greater id : заменить значение по ключу, если новое значение больше старого";
    }
}
