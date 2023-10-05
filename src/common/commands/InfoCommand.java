package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;
/**
 * Класс осуществляющий реализацию команды info
 * @author grigoryvolkov
 */
public class InfoCommand extends Command{
    public InfoCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.NOARGUMENT, request);
        return new Response(getInvoker().getModelManager().getInfoAboutCollection());
    }

    @Override
    public String getDescription() {
        return "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }
}
