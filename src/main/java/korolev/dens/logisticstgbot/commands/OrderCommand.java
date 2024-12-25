package korolev.dens.logisticstgbot.commands;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderCommand extends Command {

    private String type;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;
    private LocalDate date;
    private String location;

    public OrderCommand() {
        super(CommandType.ORDER);
    }

}
