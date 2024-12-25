package korolev.dens.logisticstgbot.commands;

public abstract class Command {

    public boolean started;
    public final CommandType commandType;

    public Command(CommandType commandType) {
        this.started = false;
        this.commandType = commandType;
    }

}
