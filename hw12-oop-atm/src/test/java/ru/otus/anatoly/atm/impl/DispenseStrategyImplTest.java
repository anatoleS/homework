package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DispenseStrategyImplTest {

    @Test
    void shouldPlanDispenseWithMultipleDenominations() {
        Cassette cassette5000 = new Cassette(Nominal.FIVE_THOUSAND);
        cassette5000.accept(1);
        
        Cassette cassette1000 = new Cassette(Nominal.THOUSAND);
        cassette1000.accept(10);
        
        List<Cassette> cassettes = Arrays.asList(cassette5000, cassette1000);
        DispenseStrategyImpl strategy = new DispenseStrategyImpl();
        
        Map<Nominal, Integer> plan = strategy.plan(cassettes, new BigDecimal("6000"));
        
        assertNotNull(plan);
        assertEquals(1, plan.get(Nominal.FIVE_THOUSAND));
        assertEquals(1, plan.get(Nominal.THOUSAND));
    }

    @Test
    void shouldThrowWhenAmountNotPossible() {
        Cassette cassette5000 = new Cassette(Nominal.FIVE_THOUSAND);
        cassette5000.accept(1);
        
        List<Cassette> cassettes = List.of(cassette5000);
        DispenseStrategyImpl strategy = new DispenseStrategyImpl();
        
        assertThrows(IllegalArgumentException.class, () -> strategy.plan(cassettes, new BigDecimal("6000")));
    }

    @Test
    void shouldThrowWhenAmountNotDivisible() {
        Cassette cassette1000 = new Cassette(Nominal.THOUSAND);
        cassette1000.accept(5);
        
        List<Cassette> cassettes = List.of(cassette1000);
        DispenseStrategyImpl strategy = new DispenseStrategyImpl();
        
        assertThrows(IllegalArgumentException.class, () -> strategy.plan(cassettes, new BigDecimal("1500")));
    }

    @Test
    void shouldPlanWithGreedyApproach() {
        Cassette cassette5000 = new Cassette(Nominal.FIVE_THOUSAND);
        cassette5000.accept(1);
        
        Cassette cassette2000 = new Cassette(Nominal.TWO_THOUSAND);
        cassette2000.accept(2);
        
        Cassette cassette1000 = new Cassette(Nominal.THOUSAND);
        cassette1000.accept(10);
        
        List<Cassette> cassettes = Arrays.asList(cassette5000, cassette2000, cassette1000);
        DispenseStrategyImpl strategy = new DispenseStrategyImpl();
        
        Map<Nominal, Integer> plan = strategy.plan(cassettes, new BigDecimal("7000"));
        
        assertNotNull(plan);
        assertEquals(1, plan.get(Nominal.FIVE_THOUSAND));
        assertEquals(1, plan.get(Nominal.TWO_THOUSAND));
    }

    @Test
    void shouldThrowOnNegativeAmount() {
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        cassette.accept(5);
        
        List<Cassette> cassettes = List.of(cassette);
        DispenseStrategyImpl strategy = new DispenseStrategyImpl();
        
        assertThrows(IllegalArgumentException.class, () -> strategy.plan(cassettes, new BigDecimal("-100")));
    }
}
