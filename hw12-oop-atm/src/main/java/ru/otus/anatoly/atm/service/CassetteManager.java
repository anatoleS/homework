package ru.otus.anatoly.atm.service;

import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.List;

/**
 * Управление кассетами банкнот.
 */
public interface CassetteManager {
    void accept(Nominal nominal, int count);
    List<Cassette> getCassettes();
    BigDecimal getTotalBalance();
    Cassette findCassette(Nominal nominal);
}
