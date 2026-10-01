package ru.otus.anatoly.atm.port;

import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Адаптер предоставления остатка.
 */
public interface BalanceProvider {
    BigDecimal getTotalBalance();
    Map<Nominal, Integer> getState();
}
