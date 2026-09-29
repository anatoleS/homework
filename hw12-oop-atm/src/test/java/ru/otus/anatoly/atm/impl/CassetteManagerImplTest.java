package ru.otus.anatoly.atm.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.anatoly.atm.model.Cassette;
import ru.otus.anatoly.atm.model.Nominal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CassetteManagerImplTest {

    private CassetteManagerImpl manager;
    private List<Cassette> cassettes;

    @BeforeEach
    void setUp() {
        cassettes = new ArrayList<>();
        cassettes.add(new Cassette(Nominal.HUNDRED));
        cassettes.add(new Cassette(Nominal.FIVE_HUNDRED));
        cassettes.add(new Cassette(Nominal.THOUSAND));
        manager = new CassetteManagerImpl(cassettes);
    }

    @Test
    void shouldAcceptBanknotes() {
        manager.accept(Nominal.HUNDRED, 5);
        Cassette cassette = manager.findCassette(Nominal.HUNDRED);
        assertEquals(5, cassette.getCount());
    }

    @Test
    void shouldReturnTotalBalance() {
        manager.accept(Nominal.HUNDRED, 3);
        manager.accept(Nominal.FIVE_HUNDRED, 2);
        BigDecimal balance = manager.getTotalBalance();
        assertEquals(new BigDecimal("1300"), balance);
    }

    @Test
    void shouldFindCassette() {
        Cassette cassette = manager.findCassette(Nominal.HUNDRED);
        assertNotNull(cassette);
        assertEquals(Nominal.HUNDRED, cassette.getNominal());
    }

    @Test
    void shouldThrowOnUnknownNominal() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<Cassette> empty = new ArrayList<>();
            CassetteManagerImpl emptyManager = new CassetteManagerImpl(empty);
            emptyManager.findCassette(Nominal.HUNDRED);
        });
    }

    @Test
    void shouldReturnUnmodifiableList() {
        List<Cassette> list = manager.getCassettes();
        assertThrows(UnsupportedOperationException.class, () -> list.add(new Cassette(Nominal.HUNDRED)));
    }
}
