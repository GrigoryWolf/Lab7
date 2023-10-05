package common.core.manager;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.model.Coordinates;
import common.core.model.Location;
import common.core.model.Route;
import server.DatabaseManager;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс, который управляет коллекцией
 * @author grigoryvolkov
 */
public class ModelManager {
    private LinkedHashMap models;
    private Date creationDate;
    private Invoker invoker;
    private DatabaseManager databaseManager;
    public ModelManager(Invoker invoker){
        models = new LinkedHashMap<Integer, Route>();
        creationDate = new Date();
        this.invoker = invoker;
        databaseManager = invoker.getDatabaseManager();
        databaseManager.loadCollectionFromDB(this);

    }
    public synchronized boolean removeLowerKey(Request request){
        if(!databaseManager.removeLowerKeyRoutes(request)) return false;
        Integer id = Integer.parseInt(request.getArg());
        Set<Integer> lowerKeys = models.keySet();
        for (Integer key : lowerKeys){
            if(key<id)models.remove(key);
        }
        return true;
    }
    public synchronized boolean replaceIfGreater(Request request){
        if(!databaseManager.replaceIfGreater(request)) return false;
        Route route = (Route) models.get(request.getRoute().getId());
        if(request.getRoute().getDistance() > route.getDistance()){
            models.remove(request.getRoute().getId());
            models.put(request.getRoute().getId(), request.getRoute());
            return true;
        }
        return false;
    }
    public synchronized boolean putModel(Request request){
        if(!databaseManager.insertRoute(request)) return false;
        models.put(request.getRoute().getId(), request.getRoute());
        return true;
    }
    public synchronized boolean update(Request request){
        if(!databaseManager.updateRoute(request)) return false;
        models.remove(request.getRoute().getId());
        models.put(request.getRoute().getId(), request.getRoute());
        return true;
    }
    public long countByDistance(double distance){
        Stream<Route> routeList = models.values().stream();
        return routeList.filter(x->x.getDistance()==distance).count();
    }
    public synchronized boolean clearCollection(Request request){
        if(!databaseManager.clearRoutes(request)) return false;
        models.clear();
        databaseManager.loadCollectionFromDB(this);
        return true;
    }
    public synchronized boolean deleteModel(Request request){
        if(!databaseManager.removeRoute(request)) return false;
        models.remove(Integer.parseInt(request.getArg()));
        return true;
    }
    public String getInfoAboutCollection() {
        return("Тип коллекции: " + models.getClass().getSimpleName() +
        "\nКоличество элементов в коллекции: " + models.size()+
        "\nДата инициализации: " + creationDate);
    }
    public void modelCheck(LinkedHashMap<Integer,Route > checkedCollection){
        Collection<Route> checkedRoutes = checkedCollection.values();
        for (Route route : checkedRoutes){
            modelCheck(route);
        }
    }
    public boolean modelCheck(Route route){
        try {
            Coordinates coordinates = route.getCoordinates();
            Location from = route.getFrom();
            Location to = route.getTo();
            nullCheck(coordinates, to, from, route.getDistance(),
                    route.getCreationDate(), coordinates.getX(), coordinates.getY(), to.getX(),
                    to.getY(), to.getZ(), from.getX(), from.getY(), from.getZ());
            nameCheck(route.getName(), from.getName(), to.getName());
            if (coordinates.getX()<=-866){
                throw new NotValidArgumentsException();
            }
            if (coordinates.getY()>107){
                throw new NotValidArgumentsException();
            }
            models.put(route.getId(), route);
            return true;
        }
        catch (NotValidArgumentsException exception){
            invoker.getPrinter().print("Один из объектов Route имеет не валидные значения," +
                    " он не будет добавлен в коллекцию");
            return false;
        }
}

    public LinkedHashMap<Integer, Route> getCollection(){
        return models;
    }
    public void nameCheck(String ... names) throws NotValidArgumentsException {
        for(String name : names){
            if(name == null || name.equals("")){
            throw new NotValidArgumentsException("Аргумент" +
                    " не может быть null");}
        }
    }
    public void nullCheck(Object ... objects) throws NotValidArgumentsException {
        for(Object object : objects){if(object == null){
            throw new NotValidArgumentsException("Аргумент" +
                    " не может быть null");}
        }
    }

}
