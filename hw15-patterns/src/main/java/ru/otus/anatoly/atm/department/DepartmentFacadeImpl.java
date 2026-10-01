package ru.otus.anatoly.atm.department;

import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация фасада департамента.
 * Скрывает работу с NamedAtm и Department.
 */
public class DepartmentFacadeImpl implements DepartmentFacade {
    private final Department department;
    private final Map<String, NamedAtm> atmByName = new HashMap<>();

    public DepartmentFacadeImpl(Department department) {
        this.department = department;
    }

    @Override
    public void addAtm(String name, Atm atm) {
        NamedAtm namedAtm = new NamedAtm(name, atm);
        department.addNamedAtm(namedAtm);
        atmByName.put(name, namedAtm);
    }

    @Override
    public void removeAtm(String name) {
        NamedAtm namedAtm = atmByName.remove(name);
        if (namedAtm != null) {
            department.removeNamedAtm(namedAtm);
        }
    }

    @Override
    public void deposit(String atmName, Nominal nominal, int count) {
        NamedAtm namedAtm = getNamedAtm(atmName);
        namedAtm.getAtm().accept(nominal, count);
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(String atmName, BigDecimal amount) {
        NamedAtm namedAtm = getNamedAtm(atmName);
        return namedAtm.getAtm().withdraw(amount);
    }

    @Override
    public BigDecimal getBalance(String atmName) {
        NamedAtm namedAtm = getNamedAtm(atmName);
        return namedAtm.getBalance();
    }

    @Override
    public BigDecimal getTotalBalance() {
        return department.getTotalBalance();
    }

    private NamedAtm getNamedAtm(String name) {
        NamedAtm namedAtm = atmByName.get(name);
        if (namedAtm == null) {
            throw new IllegalArgumentException("ATM not found: " + name);
        }
        return namedAtm;
    }
}
