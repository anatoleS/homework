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

class DepartmentIntegrationTest {

    @Test
    void shouldCalculateTotalBalanceForMultipleAtms() {
        // Given: два ATM с разными балансами
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(2); // 1000
        
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(3); // 300
        
        var manager1 = new CassetteManagerImpl(Arrays.asList(cassette1));
        var manager2 = new CassetteManagerImpl(Arrays.asList(cassette2));
        
        Atm atm1 = createAtm(manager1);
        Atm atm2 = createAtm(manager2);
        
        NamedAtm namedAtm1 = new NamedAtm("ATM-1", atm1);
        NamedAtm namedAtm2 = new NamedAtm("ATM-2", atm2);
        
        Department dept = new DepartmentImpl();
        dept.addNamedAtm(namedAtm1);
        dept.addNamedAtm(namedAtm2);
        
        // When
        BigDecimal total = dept.getTotalBalance();
        
        // Then
        assertEquals(new BigDecimal("1300"), total);
    }

    @Test
    void shouldResetAllAtmsToInitialState() {
        // Given: два ATM
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(2); // 1000
        
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(5); // 500
        
        var manager1 = new CassetteManagerImpl(Arrays.asList(cassette1));
        var manager2 = new CassetteManagerImpl(Arrays.asList(cassette2));
        
        Atm atm1 = createAtm(manager1);
        Atm atm2 = createAtm(manager2);
        
        NamedAtm namedAtm1 = new NamedAtm("ATM-1", atm1);
        NamedAtm namedAtm2 = new NamedAtm("ATM-2", atm2);
        
        Department dept = new DepartmentImpl();
        dept.addNamedAtm(namedAtm1);
        dept.addNamedAtm(namedAtm2);
        
        assertEquals(new BigDecimal("1500"), dept.getTotalBalance());
        
        // When: снимаем деньги
        atm1.withdraw(new BigDecimal("500"));
        atm2.withdraw(new BigDecimal("200"));
        
        assertEquals(new BigDecimal("800"), dept.getTotalBalance());
        
        // When: восстанавливаем
        dept.resetAll();
        
        // Then
        assertEquals(new BigDecimal("1500"), dept.getTotalBalance());
    }

    @Test
    void shouldResetAllAtmsWithDifferentInitialStates() {
        // Given: три ATM с разными начальными состояниями
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(1); // 500
        
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
        cassette2.accept(10); // 1000
        
        var cassette3 = new ru.otus.anatoly.atm.model.Cassette(Nominal.THOUSAND);
        cassette3.accept(1); // 1000
        
        Atm atm1 = createAtm(new CassetteManagerImpl(Arrays.asList(cassette1)));
        Atm atm2 = createAtm(new CassetteManagerImpl(Arrays.asList(cassette2)));
        Atm atm3 = createAtm(new CassetteManagerImpl(Arrays.asList(cassette3)));
        
        NamedAtm namedAtm1 = new NamedAtm("ATM-1", atm1);
        NamedAtm namedAtm2 = new NamedAtm("ATM-2", atm2);
        NamedAtm namedAtm3 = new NamedAtm("ATM-3", atm3);
        
        Department dept = new DepartmentImpl();
        dept.addNamedAtm(namedAtm1);
        dept.addNamedAtm(namedAtm2);
        dept.addNamedAtm(namedAtm3);
        
        assertEquals(new BigDecimal("2500"), dept.getTotalBalance());
        
        // When: снимаем разные суммы, включая полный снос одного ATM
        atm1.withdraw(new BigDecimal("500")); // 0
        atm2.withdraw(new BigDecimal("300")); // 700
        atm3.withdraw(new BigDecimal("1000")); // 0
        
        assertEquals(new BigDecimal("700"), dept.getTotalBalance());
        
        // When: восстанавливаем все
        dept.resetAll();
        
        // Then: каждый ATM вернулся к своему начальному состоянию
        assertEquals(new BigDecimal("2500"), dept.getTotalBalance());
        assertEquals(new BigDecimal("500"), atm1.getBalance());
        assertEquals(new BigDecimal("1000"), atm2.getBalance());
        assertEquals(new BigDecimal("1000"), atm3.getBalance());
    }

    @Test
    void shouldCalculateTotalBalanceAfterOperations() {
        // Given: два ATM
        var cassette1 = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette1.accept(4); // 2000
        
        var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.TWO_HUNDRED);
        cassette2.accept(5); // 1000
        
        Atm atm1 = createAtm(new CassetteManagerImpl(Arrays.asList(cassette1)));
        Atm atm2 = createAtm(new CassetteManagerImpl(Arrays.asList(cassette2)));
        
        NamedAtm namedAtm1 = new NamedAtm("ATM-1", atm1);
        NamedAtm namedAtm2 = new NamedAtm("ATM-2", atm2);
        
        Department dept = new DepartmentImpl();
        dept.addNamedAtm(namedAtm1);
        dept.addNamedAtm(namedAtm2);
        
        assertEquals(new BigDecimal("3000"), dept.getTotalBalance());
        
        // When: операции
        atm1.withdraw(new BigDecimal("1000"));
        atm2.accept(Nominal.TWO_HUNDRED, 3); // +600
        
        // Then: баланс пересчитан корректно
        assertEquals(new BigDecimal("2600"), dept.getTotalBalance());
    }

    private Atm createAtm(CassetteManager manager) {
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        return new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
    }
}
