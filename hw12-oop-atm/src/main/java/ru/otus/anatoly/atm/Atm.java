package ru.otus.anatoly.atm;

import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Банкомат.
 */
public interface Atm {
    void accept(Nominal nominal, int count);
    Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount);
    BigDecimal getBalance();
}
