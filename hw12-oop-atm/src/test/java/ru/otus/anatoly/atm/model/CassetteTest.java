package ru.otus.anatoly.atm.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CassetteTest {

    @Test
    void shouldAcceptBanknotes() {
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        cassette.accept(5);
        assertEquals(5, cassette.getCount());
        assertEquals(new BigDecimal("500"), cassette.getBalance());
    }

    @Test
    void shouldDispenseBanknotes() {
        Cassette cassette = new Cassette(Nominal.FIVE_HUNDRED);
        cassette.accept(10);
        cassette.dispense(3);
        assertEquals(7, cassette.getCount());
        assertEquals(new BigDecimal("3500"), cassette.getBalance());
    }

    @Test
    void shouldThrowOnNegativeAccept() {
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        assertThrows(IllegalArgumentException.class, () -> cassette.accept(-1));
    }

    @Test
    void shouldThrowOnNegativeDispense() {
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        cassette.accept(5);
        assertThrows(IllegalArgumentException.class, () -> cassette.dispense(-1));
    }

    @Test
    void shouldThrowOnDispenseMoreThanAvailable() {
        Cassette cassette = new Cassette(Nominal.HUNDRED);
        cassette.accept(2);
        assertThrows(IllegalArgumentException.class, () -> cassette.dispense(5));
    }

    @Test
    void shouldReturnCorrectBalance() {
        Cassette cassette = new Cassette(Nominal.THOUSAND);
        cassette.accept(3);
        assertEquals(new BigDecimal("3000"), cassette.getBalance());
    }
}
