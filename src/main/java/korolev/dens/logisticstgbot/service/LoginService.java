package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.BotContextHolder;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Service
public class LoginService implements CommandService {

    private final ClientRepository clientRepository;
    private final BotContextHolder botContextHolder;

    public LoginService(ClientRepository clientRepository, BotContextHolder botContextHolder) {
        this.clientRepository = clientRepository;
        this.botContextHolder = botContextHolder;
    }

    private boolean nextStep(Message message, SendMessageMethod messageMethod)
            throws WrongInputException {
        Integer number;
        try {
            number = Integer.valueOf(message.getText());
        } catch (NumberFormatException e) {
            throw new WrongInputException("Номер пользователя должен быть целым числом");
        }
        Optional<Client> client = clientRepository
                .findByCreatorAndNumber(Math.toIntExact(message.getFrom().getId()), number);
        if (client.isPresent()) {
            botContextHolder.setLogin(message.getFrom().getId(), number);
            messageMethod.sendMessage("Вы успешно вошли как пользователь №" + number, message.getChatId());
        } else {
            throw new WrongInputException("У вас нету пользователя под номером " + number);
        }
        return true;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod) throws WrongInputException {
        if (!command.started) {
            messageMethod.sendMessage("Введите номер выбранного пользователя:", message.getChatId());
            command.started = true;
            return false;
        } else {
            return nextStep(message, messageMethod);
        }
    }

}
