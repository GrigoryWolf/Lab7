package common.commands;
import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

import static java.lang.Integer.parseInt;
/**
 * Класс осуществляющий реализацию команды remove_lower_key
 * @author grigoryvolkov
 */
public class RemoveLowerKeyCommand extends Command{
    public RemoveLowerKeyCommand(Invoker invoker){
        super(invoker);
    }
    private Integer id;
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        try {
            //argsСheck(TypeArg.STRINGARGUMENT, request);
            id = parseInt(request.getArg());
            if(getInvoker().getModelManager().removeLowerKey(request)) return new Response("Все элементы, id которых меньше %s были удалены " + request.getArg());
            return new Response("Не удалось выполнить команду пользователя");
        }
        catch (NumberFormatException exception){
            return new Response("Аргумент должен быть числом типа Integer!");
        }
    }
    @Override
    public String getDescription() {
        return "remove_lower_key id : удалить из коллекции все элементы, ключ которых меньше, чем заданный.";
    }
}
