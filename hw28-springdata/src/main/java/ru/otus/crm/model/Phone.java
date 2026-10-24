package ru.otus.crm.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
@Getter
@Setter
@ToString
public class Phone {

    @Id
    @Column("id")
    private Long id;

    @Column("number")
    private String number;

    @Column("client_id")
    private Long clientId;

    public Phone() {
    }

    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }
}
