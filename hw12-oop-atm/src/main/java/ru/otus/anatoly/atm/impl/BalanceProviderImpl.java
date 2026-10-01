package ru.otus.anatoly.atm.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.port.BalanceProvider;
import ru.otus.anatoly.atm.service.CassetteManager;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public class BalanceProviderImpl implements BalanceProvider {
    private final CassetteManager cassetteManager;

    @Override
    public BigDecimal getTotalBalance() {
        return cassetteManager.getTotalBalance();
    }

    @Override
    public Map<Nominal, Integer> getState() {
        Map<Nominal, Integer> state = new EnumMap<>(Nominal.class);
        for (Cassette cassette : cassetteManager.getCassettes()) {
            state.put(cassette.getNominal(), cassette.getCount());
        }
        return Collections.unmodifiableMap(state);
    }
}
