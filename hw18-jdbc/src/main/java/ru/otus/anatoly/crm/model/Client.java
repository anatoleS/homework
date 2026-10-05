package ru.otus.anatoly.crm.model;

import lombok.Getter;
import lombok.Setter;
import ru.otus.anatoly.annotation.Id;

@Getter
@Setter
public class Client {
    @Id
    private Long id;
    private String name;

    public Client() {}

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
