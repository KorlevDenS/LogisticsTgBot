package korolev.dens.logisticstgbot.bot;

import korolev.dens.logisticstgbot.commands.Command;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class BotContextHolder {

    private final HashMap<Long, Command> context = new HashMap<>();
    private final HashMap<Long, Integer> logins = new HashMap<>();

    public Integer getLogin(Long id) {
        return logins.get(id);
    }

    public void setLogin(Long id, Integer login) {
        logins.put(id, login);
    }

    public Command findCommand(Long id) {
        return context.get(id);
    }

    public void removeCommand(Long id) {
        context.remove(id);
    }

    public void addCommand(Command command, Long id) {
        context.remove(id);
        context.put(id, command);
    }

}
