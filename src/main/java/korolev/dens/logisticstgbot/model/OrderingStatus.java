package korolev.dens.logisticstgbot.model;

import lombok.Getter;

@Getter
public enum OrderingStatus {

    WAITING("Ожидает исполнителя"),
    DELIVERING("Доставляется"),
    COMPLETED("Доставлен"),
    UNKNOWN("Статус неизвестен");

    private final String title;

    OrderingStatus(String title) {
        this.title = title;
    }

    public static OrderingStatus getOrderingStatus(String title) {
        for (OrderingStatus status : OrderingStatus.values()) {
            if (status.getTitle().equals(title)) {
                return status;
            }
        }
        return UNKNOWN;
    }

}
