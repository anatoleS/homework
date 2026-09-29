package ru.otus.anatoly.atm.port;

import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Адаптер выдачи банкнот.
 */
public interface CashDispenser {
    Optional<Map<Nominal, Integer>> dispense(BigDecimal amount);
}
