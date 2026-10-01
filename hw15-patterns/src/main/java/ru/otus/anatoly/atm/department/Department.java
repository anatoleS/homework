package ru.otus.anatoly.atm.department;

import ru.otus.anatoly.atm.Atm;
import java.math.BigDecimal;

/**
 * Интерфейс Департамента.
 * Управляет коллекцией ATM.
 * Паттерн Facade для группы ATM.
 */
public interface Department {
    void addNamedAtm(NamedAtm namedAtm);
    void removeNamedAtm(NamedAtm namedAtm);
    BigDecimal getTotalBalance();
    void resetAll();
}
