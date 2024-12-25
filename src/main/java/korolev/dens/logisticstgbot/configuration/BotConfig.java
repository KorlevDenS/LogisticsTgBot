package korolev.dens.logisticstgbot.configuration;

import korolev.dens.logisticstgbot.bot.LogisticsBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ComponentScan
public class BotConfig {

    private final String botToken = "your_token";

    @Bean
    public String botToken() {
        return this.botToken;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(LogisticsBot logisticsBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(logisticsBot);
        return api;
    }

}
