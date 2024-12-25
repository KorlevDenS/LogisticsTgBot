package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.BotContextHolder;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import korolev.dens.logisticstgbot.repositories.OrderingRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class OrdersService implements CommandService {

    private final BotContextHolder botContextHolder;
    private final ClientRepository clientRepository;
    private final OrderingRepository orderingRepository;

    public OrdersService(BotContextHolder botContextHolder, ClientRepository clientRepository, OrderingRepository orderingRepository) {
        this.botContextHolder = botContextHolder;
        this.clientRepository = clientRepository;
        this.orderingRepository = orderingRepository;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod) throws WrongInputException {
        Integer selectedProfile = botContextHolder.getLogin(message.getFrom().getId());
        if (selectedProfile == null) {
            messageMethod.sendMessage("Аккаунт для работы не выбран. Вы можете посмотреть ваши аккаунты" +
                    " при помощи команды /accounts и выбрать при помощи /login", message.getChatId());
        } else {
            Client client = clientRepository.findByCreatorAndNumber(
                    Math.toIntExact(message.getFrom().getId()), selectedProfile
            ).orElseThrow(() -> new WrongInputException("Нет доступа к выбранному аккаунту"));

            if (client.getType() == ClientType.CUSTOMER) {
                messageMethod.sendMessage("Ваши заказы в работе:", message.getChatId());
                orderingRepository.findAllUncompletedByCustomer(client.getId()).forEach(order ->
                    messageMethod.sendMessage(order.toString(), message.getChatId())
                );
                messageMethod.sendMessage("Доставленные вам заказы:", message.getChatId());
                orderingRepository.findAllCompletedByCustomer(client.getId()).forEach(order ->
                    messageMethod.sendMessage(order.toString(), message.getChatId())
                );
            } else if (client.getType() == ClientType.TRANSPORTER) {
                messageMethod.sendMessage("Исполняемые вами заказы:", message.getChatId());
                orderingRepository.findAllByTransporter(client).forEach(order ->
                        messageMethod.sendMessage(order.toString(), message.getChatId())
                );
                messageMethod.sendMessage("Доступные к исполнению заказы:", message.getChatId());
                orderingRepository.findAllAvailable().forEach(order ->
                        messageMethod.sendMessage(order.toString(), message.getChatId())
                );
            }
        }
        return true;
    }

}
