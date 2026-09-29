package ru.otus.anatoly.atm.impl;

import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.DispenseStrategy;
import java.math.BigDecimal;
import java.util.*;

/**
 * Стратегия выдачи минимальным количеством банкнот.
 */
public class DispenseStrategyImpl implements DispenseStrategy {

    @Override
    public Map<Nominal, Integer> plan(List<Cassette> cassettes, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        List<Cassette> sorted = cassettes.stream()
                .filter(c -> c.getCount() > 0)
                .sorted((c1, c2) -> c2.getNominal().getValue().compareTo(c1.getNominal().getValue()))
                .toList();

        Map<Nominal, Integer> result = new LinkedHashMap<>();
        BigDecimal remaining = amount;

        for (Cassette cassette : sorted) {
            BigDecimal nominalValue = cassette.getNominal().getValue();
            int available = cassette.getCount();
            
            if (available == 0 || remaining.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            int maxNeeded = remaining.divide(nominalValue, 0, java.math.RoundingMode.FLOOR).intValue();
            int toTake = Math.min(maxNeeded, available);
            
            if (toTake > 0) {
                result.put(cassette.getNominal(), toTake);
                remaining = remaining.subtract(nominalValue.multiply(BigDecimal.valueOf(toTake)));
            }
        }

        if (remaining.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Cannot dispense amount: " + amount);
        }
        
        return result;
    }
}
