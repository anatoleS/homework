package ru.otus.anatoly.atm.port;

import java.math.BigDecimal;

/**
 * Адаптер предоставления остатка.
 */
public interface BalanceProvider {
    BigDecimal getTotalBalance();
}
