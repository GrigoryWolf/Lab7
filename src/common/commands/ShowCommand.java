package common.commands;
import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

/**
 * Класс осуществляющий реализацию команды show
 * @author grigoryvolkov
 */
public class ShowCommand extends Command{
    public ShowCommand(Invoker invoker){
        super(invoker);
    }
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.NOARGUMENT, request);
        if (getInvoker().getModelManager().getCollection().isEmpty()){return new Response("Коллекция пуста");}
        String response = "";
        for (Route routes : getInvoker().getModelManager().getCollection().values()){
            response += (routes.toString());
        }
        return new Response(response);
    }

    @Override
    public String getDescription() {
        return "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
