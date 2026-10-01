package ru.otus.anatoly.atm.department;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NamedAtmIntegrationTest {

    @Test
    void shouldRestoreStateAfterWithdrawAndAccept() {
        // Given: создаем реальный ATM с начальным состоянием
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(1);
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(1);
        
        List<ru.otus.anatoly.atm.model.Cassette> cassettes = Arrays.asList(cassette1, cassette2);
        CassetteManager manager = new CassetteManagerImpl(cassettes);
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        
        Atm atm = new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
        
        // Начальный баланс 600
        assertEquals(new BigDecimal("600"), atm.getBalance());
        
        NamedAtm namedAtm = new NamedAtm("ATM-1", atm);
        namedAtm.saveState();
        
        // When: снимаем 500
        Optional<Map<Nominal, Integer>> result = atm.withdraw(new BigDecimal("500"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("100"), atm.getBalance());
        
        // When: восстанавливаем
        namedAtm.restoreState();
        
        // Then: баланс должен быть 600
        assertEquals(new BigDecimal("600"), atm.getBalance());
    }

    @Test
    void shouldRestoreStateWhenBalanceIsZero() {
        // Given: ATM с начальным балансом 600
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(1);
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(1);
        
        List<ru.otus.anatoly.atm.model.Cassette> cassettes = Arrays.asList(cassette1, cassette2);
        CassetteManager manager = new CassetteManagerImpl(cassettes);
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        
        Atm atm = new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
        assertEquals(new BigDecimal("600"), atm.getBalance());
        
        NamedAtm namedAtm = new NamedAtm("ATM-1", atm);
        namedAtm.saveState();
        
        // When: снимаем весь баланс до нуля
        Optional<Map<Nominal, Integer>> result = atm.withdraw(new BigDecimal("600"));
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.ZERO, atm.getBalance());
        
        // When: восстанавливаем из нулевого состояния
        namedAtm.restoreState();
        
        // Then: баланс должен вернуться к 600
        assertEquals(new BigDecimal("600"), atm.getBalance());
    }

    @Test
    void shouldRestoreStateAfterMultipleOperations() {
        // Given: ATM с балансом 1000
        var cassette = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette.accept(2);
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        Atm atm = new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);

        NamedAtm namedAtm = new NamedAtm("ATM-1", atm);
        namedAtm.saveState();

        // When: несколько операций
        atm.withdraw(new BigDecimal("500")); // 500
        atm.accept(Nominal.FIVE_HUNDRED, 1); // 1000
        atm.withdraw(new BigDecimal("500")); // 500
        atm.accept(Nominal.FIVE_HUNDRED, 2); // 1500

        assertEquals(new BigDecimal("1500"), atm.getBalance());

        // When: восстанавливаем
        namedAtm.restoreState();

        // Then: баланс вернулся к начальному 1000
        assertEquals(new BigDecimal("1000"), atm.getBalance());
    }

    @Test
    void shouldRestoreStateAfterWithdrawAndAcceptCycle() {
        // Given: ATM с двумя номиналами
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(1);
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(5); // 1000
        
        CassetteManager manager = new CassetteManagerImpl(Arrays.asList(cassette1, cassette2));
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        Atm atm = new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);

        NamedAtm namedAtm = new NamedAtm("ATM-1", atm);
        namedAtm.saveState();

        // When: цикл операций
        atm.withdraw(new BigDecimal("300")); // 700
        atm.accept(Nominal.HUNDRED, 3); // 1000
        atm.withdraw(new BigDecimal("200")); // 800
        atm.accept(Nominal.FIVE_HUNDRED, 1); // 1300

        assertEquals(new BigDecimal("1300"), atm.getBalance());

        // When: восстанавливаем
        namedAtm.restoreState();

        // Then: баланс 1000
        assertEquals(new BigDecimal("1000"), atm.getBalance());
    }
}

