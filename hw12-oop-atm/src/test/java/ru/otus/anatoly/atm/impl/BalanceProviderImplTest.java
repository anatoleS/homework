package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.service.CassetteManager;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
