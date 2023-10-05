package common.commands;

import common.core.Exceptions.NotValidArgumentsException;
import common.core.Invoker;
import common.core.message.Request;
import common.core.message.Response;

public class RegisterCommand extends Command{
    public RegisterCommand(Invoker invoker) {super(invoker);}
    @Override
    public Response execute(Request request) throws NotValidArgumentsException {
    //argsСheck(TypeArg.NOARGUMENT, request);
    if (getInvoker().getDatabaseManager().registerUser(request)){
        return new Response("Вы успешно зарегистрировались");
        }
    return new Response("Не удалось зарегистрировать пользователя. Скорее всего введенный вами логин не уникален", false);
}
    @Override
    public String getDescription() {
        return "register: зарегистрировать пользователя";
    }
}

