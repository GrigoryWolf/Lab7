package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;
import static java.lang.Integer.parseInt;
/**
 * Класс осуществляющий реализацию команды remove_key
 * @author grigoryvolkov
 */
public class RemoveByKeyCommand extends Command{
    private Integer id;
    public RemoveByKeyCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.STRINGARGUMENT, request);
        id = parseInt(request.getArg());
        if(getInvoker().getModelManager().deleteModel(request)) return new Response(String.format("Маршрут по id: %s успешно удален", request.getArg()));
        return new Response("Не найден маршрут с таким id у пользователя " +request.getUser().getUserLogin());
    }
    @Override
    public String getDescription() {
        return "remove_key id: удалить элемент из коллекции по его ключу";
    }
}
