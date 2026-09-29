package ru.otus.anatoly.atm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

/**
 * Ячейка банкомата для одного номинала.
 * Хранит количество банкнот только одного номинала
 */
@Getter
@RequiredArgsConstructor
public class Cassette {
    private final Nominal nominal;
    private int count;

    public void accept(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be >= 0");
        }
        this.count += count;
    }

    public void dispense(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be >= 0");
        }
        if (count > this.count) {
            throw new IllegalArgumentException("Not enough banknotes");
        }
        this.count -= count;
    }

    public BigDecimal getBalance() {
        return nominal.getValue().multiply(BigDecimal.valueOf(count));
    }
}
