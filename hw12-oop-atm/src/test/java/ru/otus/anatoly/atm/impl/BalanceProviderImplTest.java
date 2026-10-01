package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.CassetteManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BalanceProviderImplTest {

    @Test
    void shouldReturnTotalBalance() {
        CassetteManager manager = mock(CassetteManager.class);
        when(manager.getTotalBalance()).thenReturn(new BigDecimal("1000"));
        
        BalanceProviderImpl provider = new BalanceProviderImpl(manager);
        BigDecimal balance = provider.getTotalBalance();
        
        assertEquals(new BigDecimal("1000"), balance);
        verify(manager).getTotalBalance();
    }

    @Test
    void shouldReturnStateMap() {
        Cassette cassette1 = mock(Cassette.class);
        when(cassette1.getNominal()).thenReturn(Nominal.HUNDRED);
        when(cassette1.getCount()).thenReturn(5);

        Cassette cassette2 = mock(Cassette.class);
        when(cassette2.getNominal()).thenReturn(Nominal.FIVE_HUNDRED);
        when(cassette2.getCount()).thenReturn(2);

        CassetteManager manager = mock(CassetteManager.class);
        when(manager.getCassettes()).thenReturn(Arrays.asList(cassette1, cassette2));
        
        BalanceProviderImpl provider = new BalanceProviderImpl(manager);
        Map<Nominal, Integer> state = provider.getState();
        
        assertEquals(2, state.size());
        assertEquals(5, state.get(Nominal.HUNDRED));
        assertEquals(2, state.get(Nominal.FIVE_HUNDRED));
        verify(manager).getCassettes();
    }

    @Test
    void shouldReturnUnmodifiableStateMap() {
        Cassette cassette = mock(Cassette.class);
        when(cassette.getNominal()).thenReturn(Nominal.HUNDRED);
        when(cassette.getCount()).thenReturn(5);

        CassetteManager manager = mock(CassetteManager.class);
        when(manager.getCassettes()).thenReturn(List.of(cassette));
        
        BalanceProviderImpl provider = new BalanceProviderImpl(manager);
        Map<Nominal, Integer> state = provider.getState();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            state.put(Nominal.FIVE_HUNDRED, 1);
        });
    }
}
