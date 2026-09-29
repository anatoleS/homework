package ru.otus.anatoly.atm.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.port.CashDispenser;
import ru.otus.anatoly.atm.service.CassetteManager;
import ru.otus.anatoly.atm.service.DispenseStrategy;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CashDispenserImpl implements CashDispenser {
    private final CassetteManager cassetteManager;
    private final DispenseStrategy dispenseStrategy;

    @Override
    public Optional<Map<Nominal, Integer>> dispense(BigDecimal amount) {
        try {
            Map<Nominal, Integer> plan = dispenseStrategy.plan(cassetteManager.getCassettes(), amount);

            // Применение плана, что бы изменить баланс автомата АТМ (фактически выдача)
            for (Map.Entry<Nominal, Integer> entry : plan.entrySet()) {
                Cassette cassette = cassetteManager.findCassette(entry.getKey());
                cassette.dispense(entry.getValue());
            }

            return Optional.of(plan);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
