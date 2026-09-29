package ru.otus.anatoly.atm.port;

import ru.otus.anatoly.atm.model.Nominal;

/**
 * Адаптер приёма банкнот.
 */
public interface CashAcceptor {
    void accept(Nominal nominal, int count);
}
