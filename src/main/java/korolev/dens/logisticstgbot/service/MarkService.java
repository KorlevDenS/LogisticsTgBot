package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.BotContextHolder;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.model.Ordering;
import korolev.dens.logisticstgbot.model.OrderingStatus;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import korolev.dens.logisticstgbot.repositories.OrderingRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Service
public class MarkService implements CommandService {

    private final BotContextHolder botContextHolder;
    private final ClientRepository clientRepository;
    private final OrderingRepository orderingRepository;

    public MarkService(BotContextHolder botContextHolder, ClientRepository clientRepository, OrderingRepository orderingRepository) {
        this.botContextHolder = botContextHolder;
        this.clientRepository = clientRepository;
        this.orderingRepository = orderingRepository;
    }

    private boolean nextStep(Message message, SendMessageMethod messageMethod, Client client)
            throws WrongInputException {
        int number;
        try {
            number = Integer.parseInt(message.getText());
        } catch (NumberFormatException e) {
            throw new WrongInputException("Номер заказа должен быть целым числом");
        }
        Ordering ordering = orderingRepository.findById(number).
                orElseThrow(() -> new WrongInputException("Заказа с номером " + number + " не существует"));
        if (client.getType() == ClientType.CUSTOMER) {
            if (!Objects.equals(ordering.getCustomer().getCreator(), client.getCreator())) {
                throw new WrongInputException("У вас нет заказа с номером " + number);
            }
            if (ordering.getStatus() == OrderingStatus.COMPLETED) {
                throw new WrongInputException("Заказ с номером " + number + " уже помечен как выполненный");
            }
            ordering.setStatus(OrderingStatus.COMPLETED);
            orderingRepository.save(ordering);
            messageMethod.sendMessage("Заказ №" + number + " помечен как принятый вами", message.getChatId());
        } else if (client.getType() == ClientType.TRANSPORTER) {
            if (ordering.getStatus() != OrderingStatus.WAITING) {
                throw new WrongInputException("Заказ с номером " + number + " уже выполняется или выполнен");
            }
            ordering.setStatus(OrderingStatus.DELIVERING);
            ordering.setTransporter(client);
            orderingRepository.save(ordering);
            messageMethod.sendMessage("Заказ №" + number + " успешно взят вами на выполнение", message.getChatId());
        }
        return true;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod) throws WrongInputException {
        Integer selectedProfile = botContextHolder.getLogin(message.getFrom().getId());
        if (selectedProfile == null) {
            throw new WrongInputException("Аккаунт для работы не выбран. Вым можете посмотреть ваши аккаунты" +
                    " при помощи команды /accounts и выбрать при помощи /login");
        } else {
            Client client = clientRepository.findByCreatorAndNumber(
                    Math.toIntExact(message.getFrom().getId()), selectedProfile
            ).orElseThrow(() -> new WrongInputException("Нет доступа к выбранному аккаунту"));

            if (!command.started) {
                if (client.getType() == ClientType.CUSTOMER) {
                    messageMethod.sendMessage("Введите номер вашего заказа, чтобы пометить его как выполненный:",
                            message.getChatId());
                } else if (client.getType() == ClientType.TRANSPORTER) {
                    messageMethod.sendMessage("Введите номер заказа, чтобы взять его на выполнение:",
                            message.getChatId());
                }
                command.started = true;
                return false;
            } else {
                return nextStep(message, messageMethod, client);
            }
        }
    }

}
