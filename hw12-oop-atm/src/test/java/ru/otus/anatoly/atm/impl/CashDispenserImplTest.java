package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.service.CassetteManager;
import ru.otus.anatoly.atm.service.DispenseStrategy;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CashDispenserImplTest {

    @Test
    void shouldDispenseSuccessfully() {
        CassetteManager manager = mock(CassetteManager.class);
        DispenseStrategy strategy = mock(DispenseStrategy.class);
        
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        cassette.accept(10);
        
        when(manager.getCassettes()).thenReturn(List.of(cassette));
        when(strategy.plan(any(), any())).thenReturn(Map.of(Nominal.HUNDRED, 5));
        when(manager.findCassette(Nominal.HUNDRED)).thenReturn(cassette);
        
        CashDispenserImpl dispenser = new CashDispenserImpl(manager, strategy);
        Optional<Map<Nominal, Integer>> result = dispenser.dispense(new BigDecimal("500"));
        
        Assertions.assertTrue(result.isPresent());
        assertEquals(5, result.get().get(Nominal.HUNDRED));
        assertEquals(5, cassette.getCount());
    }

    @Test
    void shouldReturnEmptyWhenDispenseFails() {
        CassetteManager manager = mock(CassetteManager.class);
        DispenseStrategy strategy = mock(DispenseStrategy.class);
        
        when(manager.getCassettes()).thenReturn(List.of());
        when(strategy.plan(any(), any())).thenThrow(new IllegalArgumentException());
        
        CashDispenserImpl dispenser = new CashDispenserImpl(manager, strategy);
        Optional<Map<Nominal, Integer>> result = dispenser.dispense(new BigDecimal("500"));
        
        Assertions.assertTrue(result.isEmpty());
    }
}
