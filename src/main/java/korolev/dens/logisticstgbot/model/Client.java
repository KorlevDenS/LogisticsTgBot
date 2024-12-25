package korolev.dens.logisticstgbot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_gen")
    @SequenceGenerator(name = "client_id_gen", sequenceName = "client_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "number", nullable = false)
    private Integer number;

    @NotNull
    @Column(name = "creator", nullable = false)
    private Integer creator;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "phone", nullable = false, length = Integer.MAX_VALUE)
    private String phone;

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private ClientType type;

    @OneToOne(mappedBy = "creator")
    private Company companies;

    @OneToMany(mappedBy = "customer")
    private Set<Ordering> customer_orderings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "transporter")
    private Set<Ordering> transporter_orderings = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Пользователь: " + number + '\n' +
                "Роль: " + type.getTitle() + '\n' +
                "Имя: " + name + '\n' +
                "Телефон: " + phone + '\n' +
                "Email: " + email + '\n';
    }
}