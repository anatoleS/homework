package ru.otus.anatoly.atm.proxy;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AtmProxyIntegrationTest {

    @Test
    void shouldAllowOperationWhenPermissionGranted() {
        Atm realAtm = buildAtm(1000);
        AccessControl control = (userId, op) -> Set.of("alice").contains(userId);
        Atm proxy = new AtmProxy(realAtm, control, "alice");

        proxy.accept(Nominal.FIVE_HUNDRED, 1);
        assertEquals(new BigDecimal("1500"), proxy.getBalance());

        var result = proxy.withdraw(new BigDecimal("500"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1000"), proxy.getBalance());
    }

    @Test
    void shouldDenyOperationWhenPermissionDenied() {
        Atm realAtm = buildAtm(1000);
        AccessControl control = (userId, op) -> Set.of("alice").contains(userId);
        Atm proxy = new AtmProxy(realAtm, control, "bob");

        assertThrows(SecurityException.class, () -> proxy.accept(Nominal.FIVE_HUNDRED, 1));
        assertThrows(SecurityException.class, () -> proxy.withdraw(new BigDecimal("100")));
        
        // getBalance should still work
        assertEquals(new BigDecimal("1000"), proxy.getBalance());
    }

    @Test
    void shouldAllowGetBalanceWithoutPermission() {
        Atm realAtm = buildAtm(500);
        AccessControl control = (userId, op) -> false;
        Atm proxy = new AtmProxy(realAtm, control, "guest");

        assertEquals(new BigDecimal("500"), proxy.getBalance());
        assertNotNull(proxy.getState());
    }

    private Atm buildAtm(int amount) {
        var cassette = new ru.otus.anatoly.atm.model.Cassette(Nominal.FIVE_HUNDRED);
        cassette.accept(amount / 500);
        CassetteManager manager = new CassetteManagerImpl(List.of(cassette));
        CashAcceptor acceptor = new CashAcceptorImpl(manager);
        BalanceProvider provider = new BalanceProviderImpl(manager);
        DispenseStrategy strategy = new DispenseStrategyImpl();
        CashDispenser dispenser = new CashDispenserImpl(manager, strategy);
        return new ru.otus.anatoly.atm.AtmImpl(acceptor, dispenser, provider);
    }
}
