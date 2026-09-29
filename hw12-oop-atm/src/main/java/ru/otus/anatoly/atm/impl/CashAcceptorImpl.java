package ru.otus.anatoly.atm.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.port.CashAcceptor;
import ru.otus.anatoly.atm.service.CassetteManager;

@RequiredArgsConstructor
public class CashAcceptorImpl implements CashAcceptor {
    private final CassetteManager cassetteManager;

    @Override
    public void accept(Nominal nominal, int count) {
        cassetteManager.accept(nominal, count);
    }
}
