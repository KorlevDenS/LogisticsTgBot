package korolev.dens.logisticstgbot.commands;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum CommandType {

    REGISTER("/register"),
    ACCOUNTS("/accounts"),
    LOGIN("/login"),
    PROFILE("/profile"),
    ORDER("/order"),
    ORDERS("/orders"),

    MARK("/mark"),
    UNKNOWN("Неизвестная команда");

    private final String title;

    CommandType(String title) {
        this.title = title;
    }

    public static List<String> getCommandsList() {
        return Arrays.stream(CommandType.values()).map(CommandType::getTitle).collect(Collectors.toList());
    }

    public static CommandType getCommand(String title) {
        for (CommandType command : CommandType.values()) {
            if (command.getTitle().equals(title)) {
                return command;
            }
        }
        return UNKNOWN;
    }

}
