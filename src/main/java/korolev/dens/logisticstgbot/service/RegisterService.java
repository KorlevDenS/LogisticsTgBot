package korolev.dens.logisticstgbot.service;

import korolev.dens.logisticstgbot.commands.Command;
import korolev.dens.logisticstgbot.commands.RegisterCommand;
import korolev.dens.logisticstgbot.bot.SendMessageMethod;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.model.Client;
import korolev.dens.logisticstgbot.model.ClientType;
import korolev.dens.logisticstgbot.model.Company;
import korolev.dens.logisticstgbot.repositories.ClientRepository;
import korolev.dens.logisticstgbot.repositories.CompanyRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class RegisterService implements CommandService {

    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    public RegisterService(ClientRepository clientRepository, CompanyRepository companyRepository) {
        this.clientRepository = clientRepository;
        this.companyRepository = companyRepository;
    }

    private Company createCompany(RegisterCommand command, Client client) {
        Company company = new Company();
        company.setCode(command.getCode());
        company.setCreator(client);
        return companyRepository.save(company);
    }

    private Client createClient(RegisterCommand command, Integer creatorId) {
        Client client = new Client();
        client.setCreator(creatorId);
        client.setNumber(clientRepository.countByCreator(creatorId) + 1);
        client.setType(command.getType());
        client.setName(command.getName());
        client.setPhone(command.getPhone());
        client.setEmail(command.getEmail());
        return clientRepository.save(client);
    }

    private boolean nextStep(Message message, RegisterCommand command, SendMessageMethod messageMethod)
            throws WrongInputException {
        Integer creatorId = Math.toIntExact(message.getFrom().getId());
        if (command.getType() == null) {
            command.setType(ClientType.getClientType(message.getText().toLowerCase()));
            if (command.getType() == ClientType.UNKNOWN) {
                throw new WrongInputException("Выбрана несуществующая роль, регистрация прервана");
            }
            messageMethod.sendMessage("Введите своё имя", message.getChatId());
            return false;
        } else if (command.getName() == null) {
            command.setName(message.getText());
            messageMethod.sendMessage("Введите номер телефона", message.getChatId());
            return false;
        } else if (command.getPhone() == null) {
            command.setPhone(message.getText());
            messageMethod.sendMessage("Введите адрес электронной почты", message.getChatId());
            return false;
        } else if (command.getEmail() == null) {
            command.setEmail(message.getText());
            if (command.getType() != ClientType.TRANSPORTER) {
                messageMethod.sendMessage("Регистрация успешна!\n" +
                        createClient(command, creatorId),message.getChatId());
                return true;
            } else {
                messageMethod.sendMessage("Введите ваш ИНН", message.getChatId());
                return false;
            }
        } else if (command.getCode() == null) {
            command.setCode(message.getText());
        }
        Client savedClient = createClient(command, creatorId);
        messageMethod.sendMessage("Регистрация успешна!\n" + savedClient + "\n" +
                createCompany(command, savedClient), message.getChatId());
        return true;
    }

    @Override
    public boolean execute(Message message, Command command, SendMessageMethod messageMethod)
            throws WrongInputException {
        if (!command.started) {
            messageMethod.sendMessage("Вы начали регистрацию!", message.getChatId());
            messageMethod.sendMessage("Введите выбранную роль: заказчик/перевозчик", message.getChatId());
            command.started = true;
            return false;
        } else {
            return nextStep(message, (RegisterCommand) command, messageMethod);
        }
    }

}
