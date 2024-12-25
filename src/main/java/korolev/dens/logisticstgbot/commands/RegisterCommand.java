package korolev.dens.logisticstgbot.commands;

import korolev.dens.logisticstgbot.model.ClientType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCommand extends Command {

    private ClientType type;
    private String name;
    private String phone;
    private String email;
    private String code;

    public RegisterCommand() {
        super(CommandType.REGISTER);
    }

}
