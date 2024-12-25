package korolev.dens.logisticstgbot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ordering")
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ordering_id_gen")
    @SequenceGenerator(name = "ordering_id_gen", sequenceName = "ordering_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer")
    private Client customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter")
    private Client transporter;

    @NotNull
    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private OrderingStatus status;

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    @NotNull
    @Column(name = "length", nullable = false)
    private Double length;

    @NotNull
    @Column(name = "width", nullable = false)
    private Double width;

    @NotNull
    @Column(name = "height", nullable = false)
    private Double height;

    @NotNull
    @Column(name = "weight", nullable = false)
    private Double weight;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "location", nullable = false, length = Integer.MAX_VALUE)
    private String location;

    @Override
    public String toString() {
        return "Заказ: " + id + '\n' +
                "Тип груза: " + type + '\n' +
                "Длина (см): " + length + '\n' +
                "Ширина (см): " + width + '\n' +
                "Высота (см): " + height + '\n' +
                "Вес (кг): " + weight + '\n' +
                "Дата: " + date + '\n' +
                "Город получения: " + location + '\n' +
                "Статус: " + status.getTitle() + '\n';
    }

}