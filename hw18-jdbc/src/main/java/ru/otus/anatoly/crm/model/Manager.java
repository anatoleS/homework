package ru.otus.anatoly.crm.model;

import lombok.Getter;
import lombok.Setter;
import ru.otus.anatoly.annotation.Id;

@Getter
@Setter
public class Manager {
    @Id
    private Long no;
    private String label;
    private String param1;

    public Manager() {}

    public Manager(String label) {
        this.label = label;
    }

    public Manager(Long no, String label, String param1) {
        this.no = no;
        this.label = label;
        this.param1 = param1;
    }

    @Override
    public String toString() {
        return "Manager{" + "no=" + no + ", label='" + label + '\'' + '}';
    }
}
