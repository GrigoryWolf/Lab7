package common.core;
import common.core.manager.CommandManager;
import common.core.manager.ModelManager;
import common.core.utility.Printer;
import server.DatabaseManager;

/**
 * Класс вызывающий команды, организует работу всех manager-классов
 * @author grigoryvolkov
 */
public class Invoker {
    private final ModelManager modelManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private final Printer printer;
    public Invoker(Printer printer){
        this.printer = printer;
        databaseManager = new DatabaseManager(this);
        modelManager = new ModelManager(this);
        commandManager =new CommandManager(this);

    }
    public ModelManager getModelManager() {
        return modelManager;
    }

    public Printer getPrinter() {
        return printer;
    }

    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    public CommandManager getCommandManager(){return commandManager;}
}

