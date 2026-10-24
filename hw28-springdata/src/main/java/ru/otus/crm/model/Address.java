package ru.otus.crm.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
@Getter
@Setter
@ToString
public class Address {

    @Id
    @Column("id")
    private Long id;

    @Column("street")
    private String street;

    @Column("client_id")
    private Long clientId;

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

}
