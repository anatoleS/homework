package ru.otus.anatoly.atm.decorator;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.CassetteManager;
import ru.otus.anatoly.atm.service.DispenseStrategy;
import ru.otus.anatoly.atm.port.BalanceProvider;
import ru.otus.anatoly.atm.port.CashAcceptor;
import ru.otus.anatoly.atm.port.CashDispenser;
import ru.otus.anatoly.atm.impl.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggingAtmIntegrationTest {

    @Test
    void shouldDelegateAndLogOperations() {
        var cassette = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette.accept(2);
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        Atm realAtm = buildAtm(manager);

        Atm loggingAtm = new LoggingAtm(realAtm);

        assertEquals(new BigDecimal("1000"), loggingAtm.getBalance());

        loggingAtm.accept(Nominal.FIVE_HUNDRED, 1);
        assertEquals(new BigDecimal("1500"), loggingAtm.getBalance());

        var result = loggingAtm.withdraw(new BigDecimal("500"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1000"), loggingAtm.getBalance());
    }

    @Test
    void shouldAllowChainingDecorators() {
        var cassette = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette.accept(10);
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        Atm realAtm = buildAtm(manager);

        Atm decorated = new LoggingAtm(new LoggingAtm(realAtm));

        assertEquals(new BigDecimal("1000"), decorated.getBalance());
        decorated.withdraw(new BigDecimal("200"));
        assertEquals(new BigDecimal("800"), decorated.getBalance());
    }

    private Atm buildAtm(CassetteManager manager) {
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        return new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
    }
}
