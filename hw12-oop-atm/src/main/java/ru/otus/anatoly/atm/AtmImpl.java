package ru.otus.anatoly.atm;

import lombok.RequiredArgsConstructor;
import ru.otus.anatoly.atm.model.Nominal;
import ru.otus.anatoly.atm.port.BalanceProvider;
import ru.otus.anatoly.atm.port.CashAcceptor;
import ru.otus.anatoly.atm.port.CashDispenser;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class AtmImpl implements Atm {
    private final CashAcceptor cashAcceptor;
    private final CashDispenser cashDispenser;
    private final BalanceProvider balanceProvider;

    @Override
    public void accept(Nominal nominal, int count) {
        cashAcceptor.accept(nominal, count);
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount) {
        return cashDispenser.dispense(amount);
    }

    @Override
    public BigDecimal getBalance() {
        return balanceProvider.getTotalBalance();
    }

    @Override
    public Map<Nominal, Integer> getState() {
        return balanceProvider.getState();
    }
}
