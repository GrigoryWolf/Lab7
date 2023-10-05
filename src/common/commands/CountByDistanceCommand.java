package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

import static java.lang.Double.parseDouble;

/**
 * Класс осуществляющий реализацию команды count_by_distance
 * @author grigoryvolkov
 */
public class CountByDistanceCommand extends Command{
    public CountByDistanceCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        try {
            //argsСheck(TypeArg.STRINGARGUMENT, request);
            Double distance = parseDouble(request.getArg().trim().replace(",","."));
            return new Response(String.format("Число объектов класса route со значением distance %s: %s", distance,
                    getInvoker().getModelManager().countByDistance(distance)));
        }
        catch (NumberFormatException exception){
            return new Response("Аргумент должен быть числом типа Double!");
        }
    }

    @Override
    public String getDescription() {
        return "count_by_distance : вывести количество элементов, значение поля distance которых равно заданному";
    }
}
