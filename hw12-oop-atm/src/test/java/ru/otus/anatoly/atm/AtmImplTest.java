package ru.otus.anatoly.atm;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.port.BalanceProvider;
import ru.otus.anatoly.atm.port.CashAcceptor;
import ru.otus.anatoly.atm.port.CashDispenser;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AtmImplTest {

    @Test
    void shouldAcceptBanknotes() {
        CashAcceptor acceptor = mock(CashAcceptor.class);
        CashDispenser dispenser = mock(CashDispenser.class);
        BalanceProvider provider = mock(BalanceProvider.class);
        
        Atm atm = new AtmImpl(acceptor, dispenser, provider);
        atm.accept(Nominal.HUNDRED, 5);
        
        verify(acceptor).accept(Nominal.HUNDRED, 5);
    }

    @Test
    void shouldWithdraw() {
        CashAcceptor acceptor = mock(CashAcceptor.class);
        CashDispenser dispenser = mock(CashDispenser.class);
        BalanceProvider provider = mock(BalanceProvider.class);
        
        when(dispenser.dispense(new BigDecimal("500"))).thenReturn(Optional.of(Map.of(Nominal.HUNDRED, 5)));
        
        Atm atm = new AtmImpl(acceptor, dispenser, provider);
        Optional<Map<Nominal, Integer>> result = atm.withdraw(new BigDecimal("500"));
        
        assertTrue(result.isPresent());
        verify(dispenser).dispense(new BigDecimal("500"));
    }

    @Test
    void shouldGetBalance() {
        CashAcceptor acceptor = mock(CashAcceptor.class);
        CashDispenser dispenser = mock(CashDispenser.class);
        BalanceProvider provider = mock(BalanceProvider.class);
        
        when(provider.getTotalBalance()).thenReturn(new BigDecimal("1000"));
        
        Atm atm = new AtmImpl(acceptor, dispenser, provider);
        BigDecimal balance = atm.getBalance();
        
        assertEquals(new BigDecimal("1000"), balance);
        verify(provider).getTotalBalance();
    }
}
