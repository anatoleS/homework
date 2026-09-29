package ru.otus.anatoly.atm.model;

import lombok.Getter;
import java.math.BigDecimal;

/**
 * Номинал банкноты.
 * Фиксированный набор значений для АТМ.
 */
@Getter
public enum Nominal {
    FIFTY(new BigDecimal("50")),
    HUNDRED(new BigDecimal("100")),
    TWO_HUNDRED(new BigDecimal("200")),
    FIVE_HUNDRED(new BigDecimal("500")),
    THOUSAND(new BigDecimal("1000")),
    TWO_THOUSAND(new BigDecimal("2000")),
    FIVE_THOUSAND(new BigDecimal("5000"));

    private final BigDecimal value;

    Nominal(BigDecimal value) {
        this.value = value;
    }
}
