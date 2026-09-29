package ru.otus.anatoly.atm.service;

import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Планировщик выдачи суммы.
 * Бросает IllegalArgumentException если сумму выдать нельзя.
 */
public interface DispenseStrategy {
    Map<Nominal, Integer> plan(List<Cassette> cassettes, BigDecimal amount);
}
