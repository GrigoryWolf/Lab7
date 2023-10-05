package common.commands;

import client.User;
import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;
import common.core.model.Route;

public class LoginCommand extends Command{
    public LoginCommand(Invoker invoker) {super(invoker);}
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
        //argsСheck(TypeArg.NOARGUMENT, request);
        System.out.println("Z");
        if (getInvoker().getDatabaseManager().loginUser(request)){
            return new Response("Вы успешно вошли в профиль");
        }
        return new Response("Пользователя не существует или введен неверный пароль", false);
    }
    @Override
    public String getDescription() {
        return "login: авторизировать пользователя";
    }
}

