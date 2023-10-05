package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;
import common.core.utility.DistanceComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс осуществляющий реализацию команды print_field_descending_distance
 * @author grigoryvolkov
 */
public class PrintFieldDescendingDistanceCommand extends Command{
    private DistanceComparator distanceComparator = new DistanceComparator();
    public PrintFieldDescendingDistanceCommand(Invoker invoker){
        super(invoker);
    }

    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.NOARGUMENT, request);
        if (getInvoker().getModelManager().getCollection().isEmpty()){
            return new Response("Коллекция пуста");
        }
        ArrayList<Double> distances = new ArrayList<>();
        getInvoker().getModelManager().getCollection()
                .values()
                .stream()
                .sorted(new DistanceComparator())
                .forEach(route -> distances.add(route.getDistance()));
        return new Response("Дистанции маршрутов в порядке убывания: " + distances.toString());
    }

    @Override
    public String getDescription() {
        return "print_field_descending_distance : вывести значения поля distance всех элементов в порядке убывания";
    }
}
