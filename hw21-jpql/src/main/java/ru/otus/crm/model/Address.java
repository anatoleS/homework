package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    
    @Id
    @SequenceGenerator(name = "address_gen", sequenceName = "address_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @Column(name = "id")
    private Long id;
    
    @Column(name = "street")
    private String street;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Client client;
    
    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }
}
