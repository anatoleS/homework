package ru.otus.anatoly.atm.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.port.BalanceProvider;
import ru.otus.anatoly.atm.service.CassetteManager;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BalanceProviderImpl implements BalanceProvider {
    private final CassetteManager cassetteManager;

    @Override
    public BigDecimal getTotalBalance() {
        return cassetteManager.getTotalBalance();
    }
}
