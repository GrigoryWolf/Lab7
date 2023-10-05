package server;
import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.manager.ModelManager;
import common.core.message.Request;
import common.core.model.Coordinates;
import common.core.model.Location;
import common.core.model.Route;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DatabaseManager {
    private Invoker invoker;
    private String URL = "jdbc:postgresql://localhost:5432/studs";
    private String username = "s367988";
    private String password = "";
    private java.sql.Connection connection;
    private static final String ADD_USER = "INSERT INTO users(login, password) VALUES (?, ?)";
    private static final String CHECK_USER_EXISTS = "SELECT COUNT(*) AS count from users WHERE login = ?";
    private static final String CHECK_USER_AUTHORIZATION = "SELECT COUNT(*) AS count from users WHERE login = ? AND password = ?";

    private static final String CHECK_ID_EXISTS = "SELECT COUNT(*) AS count from routes where id = ?";
    private static final String CHECK_ROUTE_AFFILIATION = "SELECT COUNT(*) AS count from routes where id = ? AND user_login = ?";
    private static final String ADD_ROUTE = "INSERT INTO routes " +
            "(\"name\", coordinate_x, coordinate_y, creationdate, from_x, from_y, from_z, from_name," +
            " to_x, to_y, to_z, to_name, distance, user_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";
    private static final String GET_ROUTES = "SELECT * FROM routes";
    private static final String REMOVE_ROUTE = "DELETE FROM routes WHERE id = ? AND user_login = ?;";
    private static final String UPDATE_ROUTE = "UPDATE routes SET \"name\" = ?, coordinate_x = ?, coordinate_y = ?, creationdate = ?, from_x = ?, from_y = ?, from_z = ?, " +
            "from_name = ?, to_x = ?, to_y = ?, to_z = ?, to_name = ?, distance = ? WHERE id = ? AND user_login = ?;";
    private static final String CLEAR_ROUTES = "DELETE FROM routes WHERE user_login = ?";
    private static final String REMOVE_LOWER_KEY_ROUTES = "DELETE FROM routes WHERE id < ? AND user_login = ?";
    private static final String REPLACE_IF_GREATER = "UPDATE routes SET \"name\" = ?, coordinate_x = ?, coordinate_y = ?, creationdate = ?, from_x = ?, from_y = ?, from_z, = ?," +
            "from_name = ?, to_x = ?, to_y = ?, to_z = ?, to_name = ?, distance = ? WHERE id = ? AND user_login = ? AND distance > ?";
    public DatabaseManager(Invoker invoker){
        this.invoker = invoker;
        connectToDatabase();
    }
    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Подкл");
        } catch (SQLException exception) {
            invoker.getPrinter().print("Не удалось присоединиться к базе данных");
            System.exit(-1);
        }
    }

   public boolean createMissingTables() throws SQLException {
        try (PreparedStatement createTables = connection.prepareStatement("create table if not exists users(\n" +
                "    login text primary key not null,\n" +
                "    password text not null\n" +
                ")\n" +
                "create table if not exists routes (\n" +
                "    id serial primary key,\n" +
                "    \"name\" text not null,\n" +
                "    coordinate_x bigint not null,\n" +
                "    coordinate_y bigint not null,\n" +
                "    creationdate timestamp not null,\n" +
                "    from_x bigint not null,\n" +
                "    from_y bigint not null,\n" +
                "    from_z bigint not null,\n" +
                "    from_name text not null,\n" +
                "    to_x bigint not null,\n" +
                "    to_y bigint not null,\n" +
                "    to_z bigint not null,\n" +
                "    to_name text not null,\n" +
                "    distance double precision not null,\n" +
                "    user_login text references users(login)\n" +
                ")")){
                createTables.execute();
                System.out.println("создано");
                return true;
        }
        catch (SQLException exception){
            invoker.getPrinter().print("Не удалось присоединиться к базе данных");
            System.exit(-1);
            return false;
        }

    }
    public boolean registerUser(Request request) {
        try {
            PreparedStatement registerUser = connection.prepareStatement(ADD_USER);
            if (checkUserExists(request)) return false;
            User user = request.getUser();
            System.out.println(user.toString());
            registerUser.setString(1, user.getUserLogin());
            registerUser.setString(2, DatabaseHasher.encryptStringSHA1(user.getUserPassword()));
            registerUser.execute();
            registerUser.close();
            return true;
        }
        catch (SQLException exception){
            return false;
        }
    }

    public boolean loginUser(Request request) {
        try(PreparedStatement authUser = connection.prepareStatement(CHECK_USER_AUTHORIZATION)){
            if (!checkUserExists(request)) return false;
            User user = request.getUser();
            authUser.setString(1, user.getUserLogin());
            authUser.setString(2, DatabaseHasher.encryptStringSHA1(user.getUserPassword()));
            ResultSet result = authUser.executeQuery();
            result.next();
            int count = result.getInt(1);
            if (count == 1) return true;
            return false;
        }
        catch (SQLException exception){
            return true;
        }
    }
    public boolean checkUserExists(Request request){
        try(PreparedStatement checkExistence = connection.prepareStatement(CHECK_USER_EXISTS)){
            checkExistence.setString(1, request.getUser().getUserLogin());
            ResultSet result = checkExistence.executeQuery();
            result.next();
            int count = result.getInt(1);
            if (count == 1) return true;
            return false;
        }
        catch (SQLException exception){
            return false;
        }
    }
    public boolean insertRoute(Request request)  { //проверить генерацию id
        try(PreparedStatement insertRoute = connection.prepareStatement(ADD_ROUTE)) {
            Route route = request.getRoute();
            insertRoute.setString(1, route.getName());
            insertRoute.setLong(2, route.getCoordinates().getX());
            insertRoute.setLong(3, route.getCoordinates().getY());
            insertRoute.setDate(4, (Date) route.getCreationDate());
            insertRoute.setLong(5, route.getFrom().getX());
            insertRoute.setLong(6, route.getFrom().getY());
            insertRoute.setLong(7, route.getFrom().getZ());
            insertRoute.setString(8, route.getFrom().getName());
            insertRoute.setLong(9, route.getTo().getX());
            insertRoute.setLong(10, route.getTo().getY());
            insertRoute.setLong(11, route.getTo().getZ());
            insertRoute.setString(12, route.getTo().getName());
            insertRoute.setDouble(13, route.getDistance());
            route.setUserLogin(request.getUser().getUserLogin());
            insertRoute.setString(14, route.getUserLogin());
            ResultSet resultSet = insertRoute.executeQuery();
            if(resultSet.next()){
                route.setId(resultSet.getInt("id"));
            }
            return true;
        }
        catch(SQLException exception){
            return false;
        } catch (NotValidArgumentsException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean removeRoute(Request request) {
        try(PreparedStatement removeRoute = connection.prepareStatement(REMOVE_ROUTE)) {
            Integer id = Integer.parseInt(request.getArg());
            if (!checkRemovable(request)) return false;
            removeRoute.setInt(1, id);
            removeRoute.setString(2, request.getUser().getUserLogin());
            removeRoute.execute();
            return true;
        }
        catch(SQLException exception){
            return false;
        }
    }
    public boolean checkRemovable(Request request){
        System.out.println("test");
        try( PreparedStatement check = connection.prepareStatement(CHECK_ROUTE_AFFILIATION)) {
            check.setInt(1, Integer.parseInt(request.getArg()));
            check.setString(2, request.getUser().getUserLogin());
            ResultSet result = check.executeQuery();
            result.next();
            int count = result.getInt(1);
            if (count == 1) return true;
            return false;
        }
        catch (SQLException exception){
            return false;
        }
    }

    public boolean updateRoute(Request request) {
        try(PreparedStatement update = connection.prepareStatement(UPDATE_ROUTE)) {
            if (!checkRouteAffiliation(request)) return false;
            Route route = request.getRoute();
            update.setString(1, route.getName());
            update.setLong(2, route.getCoordinates().getX());
            update.setLong(3, route.getCoordinates().getY());
            update.setDate(4, (Date) route.getCreationDate());
            update.setLong(5, route.getFrom().getX());
            update.setLong(6, route.getFrom().getY());
            update.setLong(7, route.getFrom().getZ());
            update.setString(8, route.getFrom().getName());
            update.setLong(9, route.getTo().getX());
            update.setLong(10, route.getTo().getY());
            update.setLong(11, route.getTo().getZ());
            update.setString(12, route.getTo().getName());
            update.setDouble(13, route.getDistance());
            update.setInt(14, route.getId());
            update.setString(15, request.getUser().getUserLogin());
            update.execute();
            update.close();
            return true;
        }
        catch (SQLException exception){
            return false;
        }
    }
    public boolean checkIDExists(Request request) {
        try (PreparedStatement check = connection.prepareStatement(CHECK_ID_EXISTS)) {
            check.setInt(1, request.getRoute().getId());
            ResultSet result = check.executeQuery();
            result.next();
            int count = result.getInt(1);
            if (count == 1) return true;
            return false;
        }
        catch (SQLException exception){
            return false;
        }
    }

    public boolean checkRouteAffiliation(Request request){
        try(PreparedStatement check = connection.prepareStatement(CHECK_ROUTE_AFFILIATION)) {
            check.setInt(1, request.getRoute().getId());
            check.setString(2, request.getRoute().getUserLogin());
            ResultSet result = check.executeQuery();
            result.next();
            int count = result.getInt(1);
            if (count == 1) return true;
            return false;
        }
        catch (SQLException exception){
            return false;
        }
    }
    public void loadCollectionFromDB(ModelManager manager){
        LinkedHashMap<Integer, Route> collection = new LinkedHashMap<>();
        try {
            PreparedStatement getRoutes = connection.prepareStatement(GET_ROUTES);
            ResultSet result = getRoutes.executeQuery();
            while (result.next()){
                try {
                    Route route = getRouteFromResult(result);
                    collection.put(route.getId(), route);
                } catch (NotValidArgumentsException e) {
                    invoker.getPrinter().print("Неверный объект.");
                    continue;
                }
            }
            getRoutes.close();
            invoker.getPrinter().print("Коллекция успешно загружена из базы данных. Количество элементов: " + collection.size());
        } catch (SQLException ex) {
            invoker.getPrinter().print(ex.getMessage()+ "Произошла ошибка при загрузке коллекции из базы данных. Завершение работы.");
            System.exit(-1);
        }
        if (collection.isEmpty()) return;
        manager.modelCheck(collection);
    }


    public Route getRouteFromResult(ResultSet result) throws SQLException, NotValidArgumentsException{
            Route route = new Route();
            route.setId(result.getInt("id"));
            route.setName(result.getString("name"));
            route.setCoordinates(new Coordinates(result.getLong("coordinate_x"), result.getLong("coordinate_y")));
            route.setFrom(new Location(result.getLong("from_x"), result.getLong("from_y"), result.getLong("from_z"),
                    result.getString("from_name")));
            route.setTo(new Location(result.getLong("to_x"), result.getLong("to_y"), result.getLong("to_z"),
                    result.getString("to_name")));
            route.setCreationDate(result.getDate("creationdate"));
            route.setDistance(result.getDouble("distance"));
            route.setUserLogin(result.getString("user_login"));
            return route;
        }

    public boolean clearRoutes(Request request) {
        try (PreparedStatement clear = connection.prepareStatement(CLEAR_ROUTES)) {
            clear.setString(1, request.getUser().getUserLogin());
            clear.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean removeLowerKeyRoutes(Request request) {
        try (PreparedStatement removeLower = connection.prepareStatement(REMOVE_LOWER_KEY_ROUTES)) {
            removeLower.setInt(1, request.getRoute().getId());
            removeLower.setString(2, request.getRoute().getUserLogin());
            removeLower.execute();
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }
    public boolean replaceIfGreater(Request request){
        try(PreparedStatement replaceStatement = connection.prepareStatement(REPLACE_IF_GREATER)) {
            if (!checkRouteAffiliation(request)) return false;
            Route route = request.getRoute();
            replaceStatement.setString(1, route.getName());
            replaceStatement.setLong(2, route.getCoordinates().getX());
            replaceStatement.setLong(3, route.getCoordinates().getY());
            replaceStatement.setDate(4, (Date) route.getCreationDate());
            replaceStatement.setLong(5, route.getFrom().getX());
            replaceStatement.setLong(6, route.getFrom().getY());
            replaceStatement.setLong(7, route.getFrom().getZ());
            replaceStatement.setString(8, route.getFrom().getName());
            replaceStatement.setLong(9, route.getTo().getX());
            replaceStatement.setLong(10, route.getTo().getY());
            replaceStatement.setLong(11, route.getTo().getZ());
            replaceStatement.setString(12, route.getTo().getName());
            replaceStatement.setDouble(13, route.getDistance());
            replaceStatement.setInt(14, route.getId());
            replaceStatement.setString(15, route.getUserLogin());
            replaceStatement.setDouble(16, route.getDistance());
            replaceStatement.execute();
            return true;
        }
        catch (SQLException exception){
            return false;
        }
    }

}

