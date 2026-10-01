package ru.otus.anatoly.atm.department;

import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Фасад департамента для упрощённого API.
 * Паттерн Facade.
 */
public interface DepartmentFacade {
    void deposit(String atmName, Nominal nominal, int count);
    Optional<Map<Nominal, Integer>> withdraw(String atmName, BigDecimal amount);
    BigDecimal getBalance(String atmName);
    BigDecimal getTotalBalance();
    void addAtm(String name, Atm atm);
    void removeAtm(String name);
}
