package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Service
public class AccountsService implements CommandService {


    private final ClientRepository clientRepository;

    public AccountsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod) {
        messageMethod.sendMessage("Вы можете добавить новый аккаунт при помощи /register \n Ваши аккаунты:",
                message.getChatId());
        List<Client> clients = clientRepository.findAllByCreator(Math.toIntExact(message.getFrom().getId()));
        clients.forEach(c ->
            messageMethod.sendMessage(c + (c.getType() == ClientType.TRANSPORTER ? "\n" + c.getCompanies() : ""),
                    message.getChatId())
        );
        return true;
    }
}
