package ru.otus.anatoly.atm.department;

import ru.otus.anatoly.atm.Atm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Департамент — управляет коллекцией NamedAtm.
 * Реализует интерфейс Department.
 * Паттерн Composite + Facade.
 */
public class DepartmentImpl implements Department {
    private final List<NamedAtm> atms = new ArrayList<>();

    @Override
    public void addNamedAtm(NamedAtm namedAtm)  {
        namedAtm.saveState();
        atms.add(namedAtm);
    }

    @Override
    public void removeNamedAtm(NamedAtm namedAtm) {
        atms.remove(namedAtm);
    }

    @Override
    public BigDecimal getTotalBalance() {
        return atms.stream()
                .map(NamedAtm::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void resetAll() {
        atms.forEach(NamedAtm::restoreState);
    }

}
