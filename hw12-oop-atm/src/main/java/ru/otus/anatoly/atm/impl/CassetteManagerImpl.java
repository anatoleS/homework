package ru.otus.anatoly.atm.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.CassetteManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CassetteManagerImpl implements CassetteManager {
    private final List<Cassette> cassettes;

    @Override
    public void accept(Nominal nominal, int count) {
        Cassette cassette = findCassette(nominal);
        cassette.accept(count);
    }

    @Override
    public List<Cassette> getCassettes() {
        return Collections.unmodifiableList(cassettes);
    }

    @Override
    public BigDecimal getTotalBalance() {
        return cassettes.stream()
                .map(Cassette::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Cassette findCassette(Nominal nominal) {
        return cassettes.stream()
                .filter(c -> c.getNominal() == nominal)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cassette not found for nominal: " + nominal));
    }
}
