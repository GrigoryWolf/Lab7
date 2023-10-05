package common.commands;
import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;

import static java.lang.Integer.parseInt;

/**
 * Класс осуществляющий реализацию команды update
 * @author grigoryvolkov
 */
public class UpdateCommand extends Command{
    public UpdateCommand(Invoker invoker){
        super(invoker);
    }
    private Integer id;

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        try {
            if (request.getRoute() == null) {
                id = parseInt(request.getArg());
                if (getInvoker().getDatabaseManager().checkRemovable(request)){
                    return new Response("Маршрут с id " + request.getArg() + " найден во владении пользователя " + request.getUser().getUserLogin());
                }
                return new Response("Маршрут с таким id не найден у пользователя "+request.getUser().getUserLogin(), false);
            } else {
                getInvoker().getModelManager().update(request);
                return new Response("Маршрут успешно обновлен", false);
            }
        }
        catch (NumberFormatException exception){return new Response("Аргумент должен быть числом типа Integer!", false);}
    }
    @Override
    public String getDescription() {
        return "update id: обновить значение элемента коллекции, id которого равен заданному";
    }
}
