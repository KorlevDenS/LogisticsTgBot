package korolev.dens.logisticstgbot.model;

import lombok.Getter;

@Getter
public enum ClientType {

    CUSTOMER("заказчик"),
    TRANSPORTER("перевозчик"),
    UNKNOWN("тип неизвестен");

    private final String title;

    ClientType(String title) {
        this.title = title;
    }

    public static ClientType getClientType(String title) {
        for (ClientType type : ClientType.values()) {
            if (type.getTitle().equals(title)) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
