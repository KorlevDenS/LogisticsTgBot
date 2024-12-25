package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandService {

    boolean execute(Message message, Command command, SendMessageMethod messageMethod)
            throws WrongInputException;

}
