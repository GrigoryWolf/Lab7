package common.commands;

import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;

public class LogoutCommand extends Command{
    public LogoutCommand(Invoker invoker){super(invoker);}
    @Override
    public Response execute(Request request){return null;}
    @Override
    public String getDescription(){
        return "logout: разлогиниться";
    }
}
