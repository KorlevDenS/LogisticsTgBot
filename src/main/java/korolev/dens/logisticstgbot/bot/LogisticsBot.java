package korolev.dens.logisticstgbot.bot;

import korolev.dens.logisticstgbot.commands.*;
import korolev.dens.logisticstgbot.exceptions.WrongInputException;
import korolev.dens.logisticstgbot.service.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class LogisticsBot extends TelegramLongPollingBot {

    private final String username = "LogisticsBot";
    private final BotContextHolder botContextHolder;
    private final RegisterService registerService;
    private final AccountsService accountsService;
    private final ProfileService profileService;
    private final LoginService loginService;
    private final OrderService orderService;
    private final OrdersService ordersService;
    private final MarkService markService;

    public LogisticsBot(String botToken, BotContextHolder botContextHolder, RegisterService registerService, AccountsService accountsService, ProfileService profileService, LoginService loginService, OrderService orderService, OrdersService ordersService, MarkService markService) {
        super(botToken);
        this.botContextHolder = botContextHolder;
        this.registerService = registerService;
        this.accountsService = accountsService;
        this.profileService = profileService;
        this.loginService = loginService;
        this.orderService = orderService;
        this.ordersService = ordersService;
        this.markService = markService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().charAt(0) == '/') {
                startNewCommand(update.getMessage());
            } else {
                continueCommand(update.getMessage());
            }
        }
    }

    private void startNewCommand(Message message) {
        CommandType commandType = CommandType.getCommand(message.getText().toLowerCase());
        try {
            boolean commandFinished;
            switch (commandType) {
                case REGISTER -> {
                    RegisterCommand registerCommand = new RegisterCommand();
                    botContextHolder.addCommand(registerCommand, message.getFrom().getId());
                    commandFinished = registerService.execute(message, registerCommand, this::sendMessage);
                }
                case ACCOUNTS -> {
                    AccountsCommand accountsCommand = new AccountsCommand();
                    botContextHolder.addCommand(accountsCommand, message.getFrom().getId());
                    commandFinished = accountsService.execute(message, accountsCommand, this::sendMessage);
                }
                case PROFILE -> {
                    ProfileCommand profileCommand = new ProfileCommand();
                    botContextHolder.addCommand(profileCommand, message.getFrom().getId());
                    commandFinished = profileService.execute(message, profileCommand, this::sendMessage);
                }
                case LOGIN -> {
                    LoginCommand loginCommand = new LoginCommand();
                    botContextHolder.addCommand(loginCommand, message.getFrom().getId());
                    commandFinished = loginService.execute(message, loginCommand, this::sendMessage);
                }
                case ORDER -> {
                    OrderCommand orderCommand = new OrderCommand();
                    botContextHolder.addCommand(orderCommand, message.getFrom().getId());
                    commandFinished = orderService.execute(message, orderCommand, this::sendMessage);
                }
                case ORDERS -> {
                    OrdersCommand ordersCommand = new OrdersCommand();
                    botContextHolder.addCommand(ordersCommand, message.getFrom().getId());
                    commandFinished = ordersService.execute(message, ordersCommand, this::sendMessage);
                }
                case MARK -> {
                    MarkCommand markCommand = new MarkCommand();
                    botContextHolder.addCommand(markCommand, message.getFrom().getId());
                    commandFinished = markService.execute(message, markCommand, this::sendMessage);
                }
                default -> {
                    sendMessage(commandType.getTitle(), message.getChatId());
                    commandFinished = true;
                }
            }
            if (commandFinished) {
                botContextHolder.removeCommand(message.getFrom().getId());
            }
        } catch (WrongInputException e) {
            botContextHolder.removeCommand(message.getFrom().getId());
            sendMessage(e.getMessage(), message.getChatId());
        }
    }

    private void continueCommand(Message message) {
        try {
            Command cmd = botContextHolder.findCommand(message.getFrom().getId());
            if (cmd == null) {
                sendMessage("Текст не распознан, введите одну из доступных команд", message.getChatId());
            } else {
                boolean commandFinished;
                switch (cmd.commandType) {
                    case REGISTER -> commandFinished = registerService.execute(message, cmd, this::sendMessage);
                    case LOGIN -> commandFinished = loginService.execute(message, cmd, this::sendMessage);
                    case ORDER -> commandFinished = orderService.execute(message, cmd, this::sendMessage);
                    case MARK -> commandFinished = markService.execute(message, cmd, this::sendMessage);
                    default -> commandFinished = true;
                }
                if (commandFinished) {
                    botContextHolder.removeCommand(message.getFrom().getId());
                }
            }
        } catch (WrongInputException e) {
            botContextHolder.removeCommand(message.getFrom().getId());
            sendMessage(e.getMessage(), message.getChatId());
        }
    }

    public void sendMessage(String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

}
