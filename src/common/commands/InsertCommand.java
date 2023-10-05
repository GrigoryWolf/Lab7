package common.commands;
import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;

/**
 * Класс осуществляющий реализацию команды insert
 * @author grigoryvolkov
 */
public class InsertCommand extends Command{
    public InsertCommand(Invoker invoker){
        super(invoker);
    }
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.ROUTE, request);
        System.out.println("test");
        if (getInvoker().getModelManager().putModel(request)){
            return new Response("Новый маршрут успешно добавлен в коллекцию");
        }
        return new Response("Произошла ошибка во время создания маршрута");
    }

    @Override
    public String getDescription() {
        return "insert: добавить новый элемент с заданным ключом";
    }
}
