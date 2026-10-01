package ru.otus.anatoly.atm.department;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * NamedAtm — обертка для Atm с именем.
 * Паттерн Wrapper/Adapter для добавления метаданных.
 */
@RequiredArgsConstructor
@Getter
public class NamedAtm {
    private final String name;
    private final Atm atm;
    private Map<Nominal, Integer> savedState = Collections.emptyMap();

    public BigDecimal getBalance() {
        return atm.getBalance();
    }

    public void saveState() {
        savedState = atm.getState().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void restoreState() {
        atm.withdraw(atm.getBalance());
        savedState.forEach((nominal, integer) -> atm.accept(nominal, integer));

    }

}
