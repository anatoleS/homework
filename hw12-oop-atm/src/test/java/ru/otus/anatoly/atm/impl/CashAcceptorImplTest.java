package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.CassetteManager;

import static org.mockito.Mockito.*;

class CashAcceptorImplTest {

    @Test
    void shouldAcceptBanknotes() {
        CassetteManager manager = mock(CassetteManager.class);
        CashAcceptorImpl acceptor = new CashAcceptorImpl(manager);
        
        acceptor.accept(Nominal.HUNDRED, 5);
        
        verify(manager).accept(Nominal.HUNDRED, 5);
    }
}
