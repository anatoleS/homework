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

class DepartmentFacadeIntegrationTest {

    @Test
    void shouldAddAtmAndGetBalance() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        Atm atm = createAtm(500);
        facade.addAtm("ATM-1", atm);

        assertEquals(new BigDecimal("500"), facade.getBalance("ATM-1"));
        assertEquals(new BigDecimal("500"), facade.getTotalBalance());
    }

    @Test
    void shouldDepositViaFacade() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        Atm atm = createAtm(500);
        facade.addAtm("ATM-1", atm);

        facade.deposit("ATM-1", Nominal.FIVE_HUNDRED, 1);

        assertEquals(new BigDecimal("1000"), facade.getBalance("ATM-1"));
        assertEquals(new BigDecimal("1000"), facade.getTotalBalance());
    }

    @Test
    void shouldWithdrawViaFacade() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        Atm atm = createAtm(1000);
        facade.addAtm("ATM-1", atm);

        Optional<Map<Nominal, Integer>> result = facade.withdraw("ATM-1", new BigDecimal("500"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("500"), facade.getBalance("ATM-1"));
    }

    @Test
    void shouldGetTotalBalanceForMultipleAtms() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        Atm atm1 = createAtm(1000);
        Atm atm2 = createAtm(300);

        facade.addAtm("ATM-1", atm1);
        facade.addAtm("ATM-2", atm2);

        assertEquals(new BigDecimal("1300"), facade.getTotalBalance());
    }

    @Test
    void shouldRemoveAtm() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        Atm atm1 = createAtm(500);
        Atm atm2 = createAtm(500);

        facade.addAtm("ATM-1", atm1);
        facade.addAtm("ATM-2", atm2);
        assertEquals(new BigDecimal("1000"), facade.getTotalBalance());

        facade.removeAtm("ATM-1");
        assertEquals(new BigDecimal("500"), facade.getTotalBalance());

        assertThrows(IllegalArgumentException.class, () -> facade.getBalance("ATM-1"));
    }

    @Test
    void shouldThrowWhenAtmNotFound() {
        Department department = new DepartmentImpl();
        DepartmentFacade facade = new DepartmentFacadeImpl(department);

        assertThrows(IllegalArgumentException.class, () -> facade.getBalance("UNKNOWN"));
        assertThrows(IllegalArgumentException.class, () -> facade.deposit("UNKNOWN", Nominal.HUNDRED, 1));
        assertThrows(IllegalArgumentException.class, () -> facade.withdraw("UNKNOWN", BigDecimal.ONE));
    }

    private Atm createAtm(int amountInFiveHundred) {
        var cassette = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette.accept(amountInFiveHundred / 500);
        // For simplicity, assume amount divisible by 500
        // If amount not divisible, add extra nominal
        int remaining = amountInFiveHundred % 500;
        if (remaining > 0) {
            var cassette2 = new ru.otus.anatoly.atm.model.Cassette(Nominal.HUNDRED);
            cassette2.accept(remaining / 100);
            CassetteManager manager = new CassetteManagerImpl(Arrays.asList(cassette, cassette2));
            return buildAtm(manager);
        }
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        return buildAtm(manager);
    }

    private Atm createAtm(int amount, Nominal nominal) {
        var cassette = new ru.otus.anatoly.atm.model.Cassette(nominal);
        cassette.accept(amount);
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        return buildAtm(manager);
    }

    private Atm buildAtm(CassetteManager manager) {
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        return new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
    }
}
