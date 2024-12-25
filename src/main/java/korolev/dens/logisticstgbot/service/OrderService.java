package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.bot.BotContextHolder;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.commands.OrderCommand;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.model.Ordering;
import korolev.dens.logisticstgbot.model.OrderingStatus;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import korolev.dens.logisticstgbot.repositories.OrderingRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class OrderService implements CommandService {

    private final BotContextHolder botContextHolder;
    private final ClientRepository clientRepository;
    private final OrderingRepository orderingRepository;

    public OrderService(BotContextHolder botContextHolder, ClientRepository clientRepository, OrderingRepository orderingRepository) {
        this.botContextHolder = botContextHolder;
        this.clientRepository = clientRepository;
        this.orderingRepository = orderingRepository;
    }

    private Ordering createOrdering(OrderCommand command, Client creator) {
        Ordering ordering = new Ordering();
        ordering.setType(command.getType());
        ordering.setLength(command.getLength());
        ordering.setWidth(command.getWidth());
        ordering.setHeight(command.getHeight());
        ordering.setWeight(command.getWeight());
        ordering.setDate(command.getDate());
        ordering.setLocation(command.getLocation());
        ordering.setStatus(OrderingStatus.WAITING);
        ordering.setCustomer(creator);
        return orderingRepository.save(ordering);
    }

    private Double messageToValidDouble(String message) throws WrongInputException {
        double validDouble;
        try {
            validDouble = Double.parseDouble(message.replaceAll(",", "."));
        } catch (NumberFormatException e) {
            throw new WrongInputException("Добавление прервано: ожидалось целое или дробное число.");
        }
        if (validDouble <= 0) {
            throw new WrongInputException("Добавление прервано: габариты должны быть больше чем 0");
        }
        return validDouble;
    }

    private boolean nextStep(Message message, OrderCommand command, SendMessageMethod messageMethod, Client client)
            throws WrongInputException {
        if (command.getType() == null) {
            command.setType(message.getText());
            messageMethod.sendMessage("Введите длину заказа в сантиметрах", message.getChatId());
            return false;
        } else if (command.getLength() == null) {
            command.setLength(messageToValidDouble(message.getText()));
            messageMethod.sendMessage("Введите ширину заказа в сантиметрах", message.getChatId());
            return false;
        } else if (command.getWidth() == null) {
            command.setWidth(messageToValidDouble(message.getText()));
            messageMethod.sendMessage("Введите высоту заказа в сантиметрах", message.getChatId());
            return false;
        } else if (command.getHeight() == null) {
            command.setHeight(messageToValidDouble(message.getText()));
            messageMethod.sendMessage("Введите вес заказа в килограммах", message.getChatId());
            return false;
        } else if (command.getWeight() == null) {
            command.setWeight(messageToValidDouble(message.getText()));
            messageMethod.sendMessage("Введите дату доставки заказа в формате YYYY-MM-DD", message.getChatId());
            return false;
        } else if (command.getDate() == null) {
            LocalDate d;
            try {
                d = LocalDate.parse(message.getText());
            } catch (DateTimeParseException e) {
                throw new WrongInputException("Добавление прервано: дата должна быть в формате YYYY-MM-DD");
            }
            if (d.isBefore(LocalDate.now())) {
                throw new WrongInputException("Добавление прервано: дата не должна быть в прошлом");
            }
            command.setDate(d);
            messageMethod.sendMessage("Введите город получения заказа", message.getChatId());
            return false;
        } else if (command.getLocation() == null) {
            command.setLocation(message.getText());
        }
        messageMethod.sendMessage("Заказ успешно добавлен!\n" + createOrdering(command, client), message.getChatId());
        return true;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod)
            throws WrongInputException {
        if (botContextHolder.getLogin(message.getFrom().getId()) == null) {
            throw new WrongInputException("Аккаунт для работы не выбран. Вы можете посмотреть ваши аккаунты" +
                    " при помощи команды /accounts и выбрать при помощи /login");
        }
        Client client = clientRepository.findByCreatorAndNumber(
                Math.toIntExact(message.getFrom().getId()),
                botContextHolder.getLogin(message.getFrom().getId())
        ).orElseThrow(() -> new WrongInputException("Нет доступа к выбранному аккаунту"));
        if (client.getType() != ClientType.CUSTOMER) {
            throw new WrongInputException("Добавление заказа доступно только для заказчиков. Выберите профиль " +
                    "заказчика при помощи /login или зарегистрируйте новый профиль заказчика при помощи /register");
        }
        if (!command.started) {
            messageMethod.sendMessage("Вы начали добавление заказа!", message.getChatId());
            messageMethod.sendMessage("Введите тип перевозимого груза", message.getChatId());
            command.started = true;
            return false;
        } else {
            return nextStep(message, (OrderCommand) command, messageMethod, client);
        }
    }

}
