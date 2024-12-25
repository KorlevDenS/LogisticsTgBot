package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.BotContextHolder;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Service
public class ProfileService implements CommandService {

    private final BotContextHolder botContextHolder;
    private final ClientRepository clientRepository;

    public ProfileService(BotContextHolder botContextHolder, ClientRepository clientRepository) {
        this.botContextHolder = botContextHolder;
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod) throws WrongInputException {
        Integer selectedProfile = botContextHolder.getLogin(message.getFrom().getId());
        if (selectedProfile == null) {
            messageMethod.sendMessage("Аккаунт для работы не выбран. Вым можете посмотреть ваши аккаунты" +
                    " при помощи команды /accounts и выбрать при помощи /login", message.getChatId());
        } else {
            Optional<Client> profile = clientRepository.findByCreatorAndNumber(
                    Math.toIntExact(message.getFrom().getId()), selectedProfile
            );
            if (profile.isPresent()) {
                messageMethod.sendMessage("Ваш действующий профиль: \n" + profile.get()
                                + (profile.get().getType() == ClientType.TRANSPORTER ? "\n"
                                + profile.get().getCompanies() : ""),
                        message.getChatId()
                );
            } else {
                throw new WrongInputException("Нет доступа к выбранному аккаунту");
            }
        }
        return true;
    }

}
