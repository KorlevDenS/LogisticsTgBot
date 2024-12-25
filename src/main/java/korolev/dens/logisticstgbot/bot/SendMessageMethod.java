package korolev.dens.logisticstgbot.bot;

@FunctionalInterface
public interface SendMessageMethod {

    void sendMessage(String text, Long chatId);

}
